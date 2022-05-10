/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
