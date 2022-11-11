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

import java.util.Optional;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

/**
 * Functions for interacting with Keycloak in the context of BaSyx.
 *
 * @author wege
 */
public class KeycloakService {
  private final String serverUrl;
  private final String realm;

  public KeycloakService(final String serverUrl, final String realm) {
    this.serverUrl = serverUrl;
    this.realm = realm;
  }

  public String getRealmUrl() {
    return serverUrl + "/realms/" + realm;
  }

  public String getCertsEndpoint() {
    return getRealmUrl() + "/protocol/openid-connect/certs";
  }

  private String clientId;
  private String clientSecret;
  private String userName;
  private String password;

  public void setCredentials(final String clientId, final String clientSecret, final String userName, final String password) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.userName = userName;
    this.password = password;
  }

  public IConnectorFactory createConnectorFactory() {
    return addr -> new JSONConnector(new HTTPConnector(addr, this::getTokenAsBearer));
  }

  public JwtBearerTokenAuthenticationConfiguration createJwtBearerTokenAuthenticationConfiguration() {
    return JwtBearerTokenAuthenticationConfiguration.of(
        getRealmUrl(),
        getCertsEndpoint(),
        null
    );
  }

  public Optional<String> getTokenAsBearer() {
    final Keycloak keycloak = KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm(realm)
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .username(userName)
        .password(password)
        .build();

    return Optional.ofNullable(keycloak.tokenManager().getAccessToken().getToken())
        .map(token -> "Bearer " + token);
  }
}
