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

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.internal.SecurityContextAuthorizer;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A registry implementation that authorizes invocations before forwarding them
 * to an underlying registry implementation.
 * <p>
 * Implementation Note: This implementation internally uses
 * {@link SecurityContextHolder} to access the {@link SecurityContext} and its
 * {@link Authentication}. For read operations we require Read Scope Authority,
 * for mutations we require Write Scope Authority.
 *
 * @author pneuschwander
 * @see AASRegistryScopes
 */
public class AuthorizedAASRegistry implements IAASRegistry {
	public static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
	public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.READ_SCOPE;
	public static final String WRITE_AUTHORITY = SCOPE_AUTHORITY_PREFIX + AASRegistryScopes.WRITE_SCOPE;

	private final IAASRegistry registry;
	private final SecurityContextAuthorizer authorizer = new SecurityContextAuthorizer();

	/**
	 * Provides registry implementation that authorizes invocations before
	 * forwarding them to the provided registry implementation.
	 */
	public AuthorizedAASRegistry(final IAASRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.register(deviceAASDescriptor);
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.register(aas, smDescriptor);
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.delete(aasId);
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(WRITE_AUTHORITY);
		registry.delete(aasId, smId);
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupAAS(aasId);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupAll();
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupSubmodels(aasId);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		authorizer.throwExceptionInCaseOfInsufficientAuthorization(READ_AUTHORITY);
		return registry.lookupSubmodel(aasId, smId);
	}
}
