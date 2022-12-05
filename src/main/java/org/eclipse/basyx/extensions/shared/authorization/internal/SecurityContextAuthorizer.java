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
package org.eclipse.basyx.extensions.shared.authorization.internal;

import java.util.Optional;

import org.eclipse.basyx.extensions.aas.api.authorization.internal.IAASAPIAuthorizer;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal.ITaggedDirectoryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.IAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.ISubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.internal.ISubmodelAPIAuthorizer;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Checks authorization for a specific authority according to the current security context
 *
 * @author espen
 */

/**
 * @deprecated This class has been replaced in favor of more adapted and
 *             implementable authorizer interfaces
 *             {@link org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.IAASAggregatorAuthorizer}
 *             {@link IAASAPIAuthorizer} {@link IAASRegistryAuthorizer}
 *             {@link ITaggedDirectoryAuthorizer}
 *             {@link ISubmodelAggregatorAuthorizer}
 *             {@link ISubmodelAPIAuthorizer}
 */
@Deprecated
public class SecurityContextAuthorizer {
	public void throwExceptionInCaseOfInsufficientAuthorization(final String requiredAuthority) {
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
}