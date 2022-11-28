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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.ElevatedCodeAuthentication;
import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
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
	 * @deprecated Please use {@link AuthorizedAASRegistry#AuthorizedAASRegistry(IAASRegistry, IAASRegistryAuthorizer, ISubjectInformationProvider)} instead for more explicit parametrization.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public AuthorizedAASRegistry(final IAASRegistry decoratedRegistry) {
		this(
			decoratedRegistry,
			(IAASRegistryAuthorizer<SubjectInformationType>) new GrantedAuthorityAASRegistryAuthorizer<>(new AuthenticationGrantedAuthorityAuthenticator()),
			(ISubjectInformationProvider<SubjectInformationType>) new AuthenticationContextProvider()
		);
	}

	@Override
	public void register(final AASDescriptor deviceAASDescriptor) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(deviceAASDescriptor);
			return;
		}

		try {
			authorizeRegister(deviceAASDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.register(deviceAASDescriptor);
	}

	protected void authorizeRegister(final AASDescriptor aasDescriptor) throws InhibitException {
		aasRegistryAuthorizer.authorizeRegisterAas(
				subjectInformationProvider.get(),
				aasDescriptor
		);
	}

	@Override
	public void register(final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.register(aasId, smDescriptor);
			return;
		}

		try {
			authorizeRegister(aasId, smDescriptor);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.register(aasId, smDescriptor);
	}

	protected void authorizeRegister(final IIdentifier aasId, final SubmodelDescriptor smDescriptor) throws InhibitException {
		aasRegistryAuthorizer.authorizeRegisterSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smDescriptor
		);
	}

	@Override
	public void delete(final IIdentifier aasId) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.delete(aasId);
			return;
		}

		try {
			authorizeDelete(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.delete(aasId);
	}

	protected void authorizeDelete(final IIdentifier aasId) throws InhibitException {
		aasRegistryAuthorizer.authorizeUnregisterAas(
				subjectInformationProvider.get(),
				aasId
		);
	}

	@Override
	public void delete(final IIdentifier aasId, final IIdentifier smId) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			decoratedRegistry.delete(aasId, smId);
			return;
		}

		try {
			authorizeDelete(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
		decoratedRegistry.delete(aasId, smId);
	}

	protected void authorizeDelete(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		aasRegistryAuthorizer.authorizeUnregisterSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId
		);
	}

	@Override
	public AASDescriptor lookupAAS(final IIdentifier aasId) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupAAS(aasId);
		}

		try {
			return authorizeLookupAAS(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected AASDescriptor authorizeLookupAAS(final IIdentifier aasId) throws InhibitException {
		final AASDescriptor authorizedAASDescriptor = authorizeAASDescriptorOnly(aasId);

		if (authorizedAASDescriptor == null) {
			throw new ResourceNotFoundException("AAS with Id " + aasId.getId() + " does not exist");
		}

		final AASDescriptor enforcedAASDescriptor = authorizedAASDescriptor instanceof TaggedAASDescriptor ? TaggedAASDescriptor.createAsFacade(new HashMap<>(authorizedAASDescriptor)) : new AASDescriptor(authorizedAASDescriptor);
		final List<SubmodelDescriptor> submodelDescriptorsToRemove = enforcedAASDescriptor.getSubmodelDescriptors().stream().map(submodelDescriptor -> {
			final IIdentifier smId = submodelDescriptor.getIdentifier();
			try {
				return authorizeLookupSubmodel(aasId, smId);
			} catch (final InhibitException e) {
				// remove submodel descriptor if authorization was unsuccessful
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		submodelDescriptorsToRemove.forEach(submodelDescriptor -> enforcedAASDescriptor.removeSubmodelDescriptor(submodelDescriptor.getIdentifier()));

		return enforcedAASDescriptor;
	}

	private AASDescriptor authorizeAASDescriptorOnly(final IIdentifier aasId) throws InhibitException {
		return aasRegistryAuthorizer.authorizeLookupAAS(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedRegistry.lookupAAS(aasId)
		);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupAll();
		}

		try {
			return authorizeLookupAll();
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected List<AASDescriptor> authorizeLookupAll() throws InhibitException {
		return authorizeAASDescriptorListOnly().stream().map(aas -> {
			try {
				return authorizeLookupAAS(aas.getIdentifier());
			} catch (final InhibitException e) {
				// leave out that aas descriptor
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private List<AASDescriptor> authorizeAASDescriptorListOnly() throws InhibitException {
		return aasRegistryAuthorizer.authorizeLookupAll(
				subjectInformationProvider.get(),
				decoratedRegistry::lookupAll
		);
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(final IIdentifier aasId) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupSubmodels(aasId);
		}

		try {
			return authorizeLookupSubmodels(aasId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected List<SubmodelDescriptor> authorizeLookupSubmodels(final IIdentifier aasId) throws InhibitException {
		return authorizeSubmodelListOnly(aasId).stream().map(submodel -> {
			try {
				return authorizeLookupSubmodel(aasId, submodel.getIdentifier());
			} catch (final InhibitException e) {
				// leave out that submodel descriptor
				logger.info(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private List<SubmodelDescriptor> authorizeSubmodelListOnly(final IIdentifier aasId) throws InhibitException {
		return aasRegistryAuthorizer.authorizeLookupSubmodels(
				subjectInformationProvider.get(),
				aasId,
				() -> decoratedRegistry.lookupSubmodels(aasId)
		);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(final IIdentifier aasId, final IIdentifier smId) throws ProviderException {
		if (ElevatedCodeAuthentication.isCodeAuthentication()) {
			return decoratedRegistry.lookupSubmodel(aasId, smId);
		}

		try {
			return authorizeLookupSubmodel(aasId, smId);
		} catch (final InhibitException e) {
			throw new NotAuthorized(e);
		}
	}

	protected SubmodelDescriptor authorizeLookupSubmodel(final IIdentifier aasId, final IIdentifier smId) throws InhibitException {
		return aasRegistryAuthorizer.authorizeLookupSubmodel(
				subjectInformationProvider.get(),
				aasId,
				smId,
				() -> decoratedRegistry.lookupSubmodel(aasId, smId)
		);
	}
}
