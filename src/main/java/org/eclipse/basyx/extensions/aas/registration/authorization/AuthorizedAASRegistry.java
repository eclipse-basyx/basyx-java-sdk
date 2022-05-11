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
package org.eclipse.basyx.extensions.aas.registration.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

/**
 * A registry implementation that authorizes invocations before forwarding them
 * to an underlying registry implementation.
 *
 * @author pneuschwander, wege
 * @see AASRegistryScopes
 */
public class AuthorizedAASRegistry implements IAASRegistry {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizedAASRegistry.class);

	private final IAASRegistry decoratedRegistry;
	private final IAASRegistryPep aasRegistryPep;

	/**
	 * Provides registry implementation that authorizes invocations before
	 * forwarding them to the provided registry implementation.
	 */
	public AuthorizedAASRegistry(final IAASRegistry decoratedRegistry, final IAASRegistryPep aasRegistryPep) {
		this.decoratedRegistry = decoratedRegistry;
		this.aasRegistryPep = aasRegistryPep;
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		try {
			enforceRegister(deviceAASDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedRegistry.register(deviceAASDescriptor);
	}

	protected void enforceRegister(final AASDescriptor deviceAASDescriptor) throws InhibitException {
		aasRegistryPep.enforceRegisterAas(
				deviceAASDescriptor.getIdentifier()
		);
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		try {
			enforceRegister(aas, smDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedRegistry.register(aas, smDescriptor);
	}

	protected void enforceRegister(final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws InhibitException {
		aasRegistryPep.enforceRegisterSubmodel(
				aasId,
				smDescriptor.getIdentifier() // TODO: semantic id? but can be null
		);
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		try {
			enforceDelete(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedRegistry.delete(aasId);
	}

	protected void enforceDelete(final IIdentifier aasId) throws InhibitException {
		aasRegistryPep.enforceUnregisterAas(
				aasId
		);
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		try {
			enforceDelete(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
		decoratedRegistry.delete(aasId, smId);
	}

	protected void enforceDelete(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		aasRegistryPep.enforceUnregisterSubmodel(
				aasId,
				smId
		);
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException {
		try {
			return enforceLookupAAS(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected AASDescriptor enforceLookupAAS(final IIdentifier aasId) throws InhibitException {
		final AASDescriptor aas = decoratedRegistry.lookupAAS(aasId);
		final AASDescriptor enforcedAAS = aasRegistryPep.enforceLookupAas(
				aasId,
				aas
		);
		if (enforcedAAS != null) {
			final AASDescriptor enforcedAASDescriptor = new AASDescriptor(enforcedAAS);
			final List<SubmodelDescriptor> submodelDescriptors = new ArrayList<>(enforcedAASDescriptor.getSubmodelDescriptors());
			for (SubmodelDescriptor submodelDescriptor : submodelDescriptors) {
				final IIdentifier smId = submodelDescriptor.getIdentifier();
				try {
					enforceLookupSubmodel(aasId, smId);
				} catch (Unauthorized e) {
					// remove submodel descriptor if enforcement was unsuccessful
					enforcedAASDescriptor.removeSubmodelDescriptor(smId);
				}
			}
			return enforcedAASDescriptor;
		}
		return enforcedAAS;
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		return enforceLookupAll();
	}

	protected List<AASDescriptor> enforceLookupAll() {
		return decoratedRegistry.lookupAll().stream().map(aas -> {
			try {
				return enforceLookupAAS(aas.getIdentifier());
			} catch (final InhibitException e) {
				// leave out that aas descriptor
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		return enforceLookupSubmodels(aasId);
	}

	protected List<SubmodelDescriptor> enforceLookupSubmodels(final IIdentifier aasId) {
		final List<SubmodelDescriptor> submodelDescriptors = decoratedRegistry.lookupSubmodels(aasId);
		final List<SubmodelDescriptor> enforcedSubmodelDescriptors = new ArrayList<>();
		for (final SubmodelDescriptor submodelDescriptor : submodelDescriptors) {
			try {
				enforcedSubmodelDescriptors.add(enforceLookupSubmodel(aasId, submodelDescriptor.getIdentifier()));
			} catch (final InhibitException e) {
				// leave out that submodel descriptor
				logger.info(e.getMessage(), e);
			}
		}
		return enforcedSubmodelDescriptors;
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		try {
			return enforceLookupSubmodel(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized();
		}
	}

	protected SubmodelDescriptor enforceLookupSubmodel(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		final SubmodelDescriptor sm = decoratedRegistry.lookupSubmodel(aasId, smId);
		return aasRegistryPep.enforceLookupSubmodel(
				aasId,
				smId,
				sm
		);
	}
}
