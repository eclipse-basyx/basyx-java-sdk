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
package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Implementation for a role authenticator that reads from the security context
 * {@link SecurityContextHolder} and parses it according to an access token
 * (JSON Web Token) as handed out by Keycloak.
 *
 * @author wege
 */
public class KeycloakAuthenticator implements RoleAuthenticator {
  private static final Logger logger = LoggerFactory.getLogger(KeycloakAuthenticator.class);

  public KeycloakAuthenticator() {}

  @Override
  public List<String> getRoles() {
    return jwtStr2roles(getAuthentication());
  }

  public Optional<JwtAuthenticationToken> getAuthentication() {
    final SecurityContext context = SecurityContextHolder.getContext();
    return Optional.ofNullable(context.getAuthentication())
        .filter(JwtAuthenticationToken.class::isInstance)
        .map(JwtAuthenticationToken.class::cast);
  }

  public List<String> jwtStr2roles(
      Optional<JwtAuthenticationToken> tokenOrEmpty
  ) {
      logger.info("jwtStr: {}", tokenOrEmpty.map(token -> token.getToken().getTokenValue()).orElse(""));
      return tokenOrEmpty.map(token -> {
        logger.info("jwt: {}", token.getTokenAttributes());

        try {
          return (List) ((Map<String, Object>) token.getTokenAttributes().get("realm_access")).get("roles");
        } catch (final Exception e) {

        }
        return Collections.singletonList("anonymous");
      }).orElseGet(() -> Collections.singletonList("anonymous"));
    }
}
