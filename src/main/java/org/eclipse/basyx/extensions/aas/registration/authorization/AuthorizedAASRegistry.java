/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.authorization;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A registry implementation that authorizes invocations before forwarding them to
 * an underlying registry implementation.
 * <p>
 * Implementation Note:
 * This implementation internally uses {@link SecurityContextHolder} to access the {@link SecurityContext} and its {@link Authentication}.
 * For read operations we require Read Scope Authority, for mutations we require Write Scope Authority.
 *
 * @author pneuschwander
 * @see AASRegistryScopes
 */
public class AuthorizedAASRegistry implements IAASRegistry {
	private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	private static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.READ_SCOPE;
	private static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.WRITE_SCOPE;

	private final IAASRegistry registry;

	/**
	 * Provides registry implementation that authorizes invocations before forwarding them to the provided registry implementation.
	 */
	public AuthorizedAASRegistry(final IAASRegistry registry) {
		this.registry = registry;
	}

	private void throwExceptionInCaseOfInsufficientAuthorization(final String requiredAuthority) {
		final Optional<Authentication> authentication = getAuthentication();
		if (!authentication.isPresent()) {
			throw new ProviderException("Access denied for unauthenticated requestor");
		}
		if (!hasRequiredAuthority(authentication.get(), requiredAuthority)) {
			throw new ProviderException("Access denied as required authority is missing for requestor");
		}
	}

	private Optional<Authentication> getAuthentication() {
		final SecurityContext context = SecurityContextHolder.getContext();
		return Optional.ofNullable(context.getAuthentication());
	}

	private boolean hasRequiredAuthority(final Authentication authentication, final String requiredAuthority) {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(requiredAuthority::equals);
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.register(deviceAASDescriptor);
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.register(aas, smDescriptor);
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.delete(aasId);
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.delete(aasId, smId);
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupAAS(aasId);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupAll();
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupSubmodels(aasId);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupSubmodel(aasId, smId);
	}
}
