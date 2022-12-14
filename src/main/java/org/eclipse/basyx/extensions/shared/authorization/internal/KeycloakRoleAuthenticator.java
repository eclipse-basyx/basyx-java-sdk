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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Implementation for a role authenticator that reads from the security context
 * {@link SecurityContextHolder} and parses it according to an access token
 * (JSON Web Token) as handed out by Keycloak.
 *
 * @author wege
 */
public class KeycloakRoleAuthenticator implements IRoleAuthenticator<Jwt> {
	private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleAuthenticator.class);

	public KeycloakRoleAuthenticator() {
	}

	@Override
	public List<String> getRoles(Jwt subjectInformation) {
		return Optional.ofNullable(subjectInformation).map(info -> new JwtAuthenticationToken(subjectInformation)).map(this::jwtStr2roles).orElse(new ArrayList<>(Collections.singletonList("anonymous")));
	}

	private List<String> jwtStr2roles(JwtAuthenticationToken token) {
		logger.info("jwtStr: {}", token.getToken().getTokenValue());
		logger.info("jwt: {}", token.getTokenAttributes());

		try {
			final Object realmAccessObject = token.getTokenAttributes().get("realm_access");

			final Map<?, ?> realmAccess = (Map<?, ?>) realmAccessObject;

			final Object rolesObject = realmAccess.get("roles");

			if (rolesObject != null) {
				final List<?> roles = (List<?>) rolesObject;
				return roles.stream().map(Object::toString).collect(Collectors.toList());
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>(Collections.singletonList("anonymous"));
	}
}
