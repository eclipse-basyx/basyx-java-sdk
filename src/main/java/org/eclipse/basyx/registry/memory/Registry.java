/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.memory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a generic AAS registry that makes use of a given handler.
 */
public class Registry implements IRegistry {
	private static Logger logger = LoggerFactory.getLogger(Registry.class);
	protected IRegistryHandler handler;

	public Registry(IRegistryHandler handler) {
		this.handler = handler;
	}

	@Override
	public void register(AASDescriptor shellDescriptor) {
		IIdentifier shellIdentifier = shellDescriptor.getIdentifier();

		if (handler.containsShell(shellIdentifier)) {
			throw new MalformedRequestException("Can not create a new Shell with an existing identifier.");
		}

		handler.insertShell(shellDescriptor);
		logger.debug("Registered {}", shellIdentifier.getId());
	}

	@Override
	public void register(SubmodelDescriptor submodelDescriptor) {
		IIdentifier submodelIdentifier = submodelDescriptor.getIdentifier();

		if (handler.containsSubmodel(submodelIdentifier)) {
			throw new MalformedRequestException("Can not create a new Shell with an existing identifier.");
		}

		handler.insertSubmodel(submodelDescriptor);
		logger.debug("Registered {}", submodelIdentifier.getId());
	}

	@Override
	public void updateShell(IIdentifier shellIdentifier, AASDescriptor shellDescriptor) throws ProviderException {
		if (!handler.containsShell(shellIdentifier)) {
			throw new ResourceNotFoundException("Could not update Shell " + shellIdentifier.getId() + " since it does not exist.");
		}

		handler.updateShell(shellDescriptor);
		logger.debug("Updated {}", shellIdentifier.getId());
	}

	@Override
	public void updateSubmodel(IIdentifier submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		if (!handler.containsSubmodel(submodelIdentifier)) {
			throw new ResourceNotFoundException("Could not update Submodel " + submodelIdentifier.getId() + " since it does not exist.");
		}

		handler.updateSubmodel(submodelDescriptor);
		logger.debug("Updated " + submodelIdentifier.getId());
	}

	@Override
	public void registerSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) {
		if (submodelForShellExists(shellIdentifier, submodelDescriptor.getIdentifier())) {
			throw new MalformedRequestException("Can not register a submodelDescriptor with an already existing identifier.");
		}

		AASDescriptor descriptor = handler.getShell(shellIdentifier);
		if (descriptor == null) {
			throw new ResourceNotFoundException("Could not add submodel descriptor for Shell " + shellIdentifier.getId() + " since it does not exist.");
		}

		descriptor.addSubmodelDescriptor(submodelDescriptor);
		handler.updateShell(descriptor);
		logger.debug("Registered submodel " + submodelDescriptor.getIdShort() + " for Shell " + shellIdentifier.getId());
	}

	private boolean submodelForShellExists(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
		try {
			lookupSubmodelForShell(shellIdentifier, submodelIdentifier);
			return true;
		} catch (ResourceNotFoundException e) {
			return false;
		}
	}

	@Override
	public void updateSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) {
		try {
			deleteSubmodelFromShell(shellIdentifier, submodelDescriptor.getIdentifier());
		} catch (ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Can not update non existing submodelDescriptor.");
		}

		AASDescriptor shellDescriptor = handler.getShell(shellIdentifier);
		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Could not update submodel descriptor for Shell " + shellIdentifier.getId() + " since it does not exist.");
		}

		shellDescriptor.addSubmodelDescriptor(submodelDescriptor);
		handler.updateShell(shellDescriptor);
		logger.debug("Updated submodel " + submodelDescriptor.getIdShort() + " for Shell " + shellIdentifier.getId());
	}

	@Override
	public void deleteShell(IIdentifier shellIdentifier) {
		String shellId = shellIdentifier.getId();

		if (!handler.containsShell(shellIdentifier)) {
			throw new ResourceNotFoundException("Could not delete key for Shell " + shellId + " since it does not exist.");
		}

		handler.removeShell(shellIdentifier);
		logger.debug("Removed " + shellId);
	}

	@Override
	public void deleteSubmodel(IIdentifier submodelIdentifier) {
		String submodelId = submodelIdentifier.getId();

		if (!handler.containsSubmodel(submodelIdentifier)) {
			throw new ResourceNotFoundException("Could not delete key for Submodel " + submodelId + " since it does not exist.");
		}

		handler.removeSubmodel(submodelIdentifier);
		logger.debug("Removed " + submodelId);
	}

	@Override
	public AASDescriptor lookupShell(IIdentifier shellIdentifier) {
		if (!handler.containsShell(shellIdentifier)) {
			throw new ResourceNotFoundException("Could not look up descriptor for Shell " + shellIdentifier.getId() + " since it does not exist");
		}

		return handler.getShell(shellIdentifier);
	}

	@Override
	public List<AASDescriptor> lookupAllShells() {
		logger.debug("Looking up all Shells");
		return handler.getAllShells();
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodels() {
		logger.debug("Looking up all Submodels");
		return handler.getAllSubmodels();
	}

	@Override
	public void deleteSubmodelFromShell(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
		AASDescriptor shellDescriptor = handler.getShell(shellIdentifier);

		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Could not delete submodel descriptor for Shell " + shellIdentifier.getId() + " since the Shell does not exist");
		}

		if (shellDescriptor.getSubmodelDescriptorFromIdentifier(submodelIdentifier) == null) {
			throw new ResourceNotFoundException("Could not delete submodel descriptor for Shell " + shellIdentifier.getId() + " since the Submodel does not exist");
		}

		shellDescriptor.removeSubmodelDescriptor(submodelIdentifier);
		handler.updateShell(shellDescriptor);
		logger.debug("Deleted submodel " + submodelIdentifier.getId() + " from Shell " + shellIdentifier.getId());
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodelsForShell(IIdentifier shellIdentifier) throws ProviderException {
		AASDescriptor desc = handler.getShell(shellIdentifier);

		if (desc == null) {
			throw new ResourceNotFoundException("Could not look up submodels for Shell " + shellIdentifier + " since it does not exist");
		}

		return new ArrayList<>(desc.getSubmodelDescriptors());
	}

	@Override
	public SubmodelDescriptor lookupSubmodelForShell(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		AASDescriptor shellDescriptor = handler.getShell(shellIdentifier);

		if (shellDescriptor == null) {
			throw new ResourceNotFoundException("Could not look up descriptor for Submodel " + submodelIdentifier + " of Shell " + shellIdentifier + " since the Shell does not exist");
		}

		SubmodelDescriptor submodelDescriptor = shellDescriptor.getSubmodelDescriptorFromIdentifier(submodelIdentifier);

		if (submodelDescriptor == null) {
			throw new ResourceNotFoundException("Could not look up descriptor for Submodel " + submodelIdentifier + " of Shell " + shellIdentifier + " since the Submodel does not exist");
		}

		return submodelDescriptor;
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		if (!handler.containsSubmodel(submodelIdentifier)) {
			throw new ResourceNotFoundException("Could not look up descriptor for Submodel " + submodelIdentifier.getId() + " since it does not exist");
		}
		return handler.getSubmodel(submodelIdentifier);
	}
}