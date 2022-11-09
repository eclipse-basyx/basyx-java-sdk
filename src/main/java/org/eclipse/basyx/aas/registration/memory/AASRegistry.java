/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.memory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a generic AAS registry that makes use of a given handler.
 */
public class AASRegistry implements IAASRegistry {
	private static Logger logger = LoggerFactory.getLogger(AASRegistry.class);
	protected IRegistryHandler handler;
	private String registryId = "basyx-registry";

	public AASRegistry(IRegistryHandler handler) {
		this.handler = handler;
	}

	@Override
	public String getRegistryId() {
		return registryId;
	}

	@Override
	public void setRegistryId(String id) {
		this.registryId = id;
	}

	@Override
	public void register(AASDescriptor aasDescriptor) {
		IIdentifier aasIdentifier = aasDescriptor.getIdentifier();
		if (handler.contains(aasIdentifier)) {
			handler.update(aasDescriptor);
		} else {
			handler.insert(aasDescriptor);
		}
		logger.debug("Registered " + aasIdentifier.getId());
	}

	@Override
	public void delete(IIdentifier aasIdentifier) {
		String aasId = aasIdentifier.getId();
		if (!handler.contains(aasIdentifier)) {
			throw new ResourceNotFoundException("Could not delete key for AAS " + aasId + " since it does not exist");
		} else {
			handler.remove(aasIdentifier);
			logger.debug("Removed " + aasId);
		}
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) {
		String aasId = aasIdentifier.getId();
		if (!handler.contains(aasIdentifier)) {
			throw new ResourceNotFoundException("Could not look up descriptor for AAS " + aasId + " since it does not exist");
		}
		return handler.get(aasIdentifier);
	}

	@Override
	public List<AASDescriptor> lookupAll() {
		logger.debug("Looking up all AAS");
		return handler.getAll();
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) {
		try {
			delete(aas, smDescriptor.getIdentifier());
		} catch (ResourceNotFoundException e) {
			// Doesn't matter
		}

		AASDescriptor descriptor = handler.get(aas);
		if (descriptor == null) {
			throw new ResourceNotFoundException("Could not add submodel descriptor for AAS " + aas.getId() + " since the AAS does not exist");
		}

		descriptor.addSubmodelDescriptor(smDescriptor);
		handler.update(descriptor);
		logger.debug("Registered submodel " + smDescriptor.getIdShort() + " for AAS " + aas.getId());
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) {
		String smIdString = smId.getId();
		AASDescriptor desc = handler.get(aasId);
		if (desc == null) {
			throw new ResourceNotFoundException("Could not delete submodel descriptor for AAS " + aasId.getId() + " since the AAS does not exist");
		}
		if (desc.getSubmodelDescriptorFromIdentifierId(smIdString) == null) {
			throw new ResourceNotFoundException("Could not delete submodel descriptor for AAS " + aasId.getId() + " since the SM does not exist");
		}

		desc.removeSubmodelDescriptor(smId);
		handler.update(desc);
		logger.debug("Deleted submodel " + smIdString + " from AAS " + aasId.getId());
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		AASDescriptor desc = handler.get(aasId);
		if (desc == null) {
			throw new ResourceNotFoundException("Could not look up submodels for AAS " + aasId + " since it does not exist");
		}

		return new ArrayList<>(desc.getSubmodelDescriptors());
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		AASDescriptor desc = handler.get(aasId);
		if (desc == null) {
			throw new ResourceNotFoundException("Could not look up descriptor for SM " + smId + " of AAS " + aasId + " since the AAS does not exist");
		}
		SubmodelDescriptor smDesc = desc.getSubmodelDescriptorFromIdentifierId(smId.getId());
		if (smDesc == null) {
			throw new ResourceNotFoundException("Could not look up descriptor for SM " + smId + " of AAS " + aasId + " since the SM does not exist");
		}
		return smDesc;
	}
}
