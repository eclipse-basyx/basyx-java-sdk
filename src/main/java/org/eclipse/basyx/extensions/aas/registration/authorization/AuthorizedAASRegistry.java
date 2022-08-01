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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.CodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A registry implementation that authorizes invocations before forwarding them
 * to an underlying registry implementation.
 *
 * @author pneuschwander, wege
 * @see AASRegistryScopes
 */
public class AuthorizedAASRegistry<SubjectInformationType> implements IAASRegistry {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.WRITE_SCOPE;

	private static final Logger logger = LoggerFactory.getLogger(AuthorizedAASRegistry.class);

	protected final IAASRegistry decoratedRegistry;
	protected final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer;
	protected final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

	/**
	 * Provides registry implementation that authorizes invocations before
	 * forwarding them to the provided registry implementation.
	 */
	public AuthorizedAASRegistry(
			final IAASRegistry decoratedRegistry,
			final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer,
			final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider
	) {
		this.decoratedRegistry = decoratedRegistry;
		this.aasRegistryAuthorizer = aasRegistryAuthorizer;
		this.subjectInformationProvider = subjectInformationProvider;
	}

	/**
	 * @deprecated
	 */
	public AuthorizedAASRegistry(final IAASRegistry decoratedRegistry) {
		this(
			decoratedRegistry,
			(IAASRegistryAuthorizer<SubjectInformationType>) new GrantedAuthorityAASRegistryAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public void register(final AASDescriptor deviceAASDescriptor) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(deviceAASDescriptor);
			return;
		}

		try {
			enforceRegister(deviceAASDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.register(deviceAASDescriptor);
	}

	protected void enforceRegister(final AASDescriptor deviceAASDescriptor) throws InhibitException {
		final IIdentifier aasId = deviceAASDescriptor.getIdentifier();

		aasRegistryAuthorizer.enforceRegisterAas(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public void register(final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(aasId, smDescriptor);
			return;
		}

		try {
			enforceRegister(aasId, smDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.register(aasId, smDescriptor);
	}

	protected void enforceRegister(final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws InhibitException {
		final IIdentifier smId = smDescriptor.getIdentifier(); // TODO: semantic id? but can be null

		aasRegistryAuthorizer.enforceRegisterSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public void delete(final IIdentifier aasId) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.delete(aasId);
			return;
		}

		try {
			enforceDelete(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.delete(aasId);
	}

	protected void enforceDelete(final IIdentifier aasId) throws InhibitException {
		aasRegistryAuthorizer.enforceUnregisterAas(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public void delete(final IIdentifier aasId, final IIdentifier smId) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.delete(aasId, smId);
			return;
		}

		try {
			enforceDelete(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.delete(aasId, smId);
	}

	protected void enforceDelete(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		aasRegistryAuthorizer.enforceUnregisterSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public AASDescriptor lookupAAS(final IIdentifier aasId) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupAAS(aasId);
		}

		try {
			return enforceLookupAAS(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected AASDescriptor enforceLookupAAS(final IIdentifier aasId) throws InhibitException {
		final AASDescriptor enforcedAAS = aasRegistryAuthorizer.enforceLookupAas(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedRegistry.lookupAAS(aasId)
		);

		if (enforcedAAS != null) {
			final AASDescriptor enforcedAASDescriptor = new AASDescriptor(enforcedAAS);
			final List<SubmodelDescriptor> submodelDescriptorsToRemove = enforcedAASDescriptor.getSubmodelDescriptors().stream().map(submodelDescriptor -> {
				final IIdentifier smId = submodelDescriptor.getIdentifier();
				try {
					return enforceLookupSubmodel(aasId, smId);
				} catch (final InhibitException e) {
					// remove submodel descriptor if enforcement was unsuccessful
					logger.info(e.getMessage(), e);
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toList());
			submodelDescriptorsToRemove.forEach(submodelDescriptor -> enforcedAASDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdentifier()));
			return enforcedAASDescriptor;
		}
		return enforcedAAS;
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupAll();
		}

		try {
			return enforceLookupAll();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected List<AASDescriptor> enforceLookupAll() throws InhibitException {
		final List<AASDescriptor> enforcedAASDescriptors = aasRegistryAuthorizer.enforceLookupAll(
				subjectInformationProvider.get(),
				decoratedRegistry::lookupAll
		);
		return enforcedAASDescriptors.stream().map(aas -> {
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
	public List<SubmodelDescriptor> lookupSubmodels(final IIdentifier aasId) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupSubmodels(aasId);
		}

		try {
			return enforceLookupSubmodels(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected List<SubmodelDescriptor> enforceLookupSubmodels(final IIdentifier aasId) throws InhibitException {
		final List<SubmodelDescriptor> enforcedSubmodelDescriptors = aasRegistryAuthorizer.enforceLookupSubmodels(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedRegistry.lookupSubmodels(aasId)
		);
		return enforcedSubmodelDescriptors.stream().map(submodel -> {
			try {
				return enforceLookupSubmodel(aasId, submodel.getIdentifier());
			} catch (final InhibitException e) {
				// leave out that submodel descriptor
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(final IIdentifier aasId, final IIdentifier smId) throws ProviderException {
		if (CodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupSubmodel(aasId, smId);
		}

		try {
			return enforceLookupSubmodel(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected SubmodelDescriptor enforceLookupSubmodel(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		return aasRegistryAuthorizer.enforceLookupSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> decoratedRegistry.lookupSubmodel(aasId, smId)
		);
	}
}
