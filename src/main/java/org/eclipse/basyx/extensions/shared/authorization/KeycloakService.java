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
 * Functions for interacting with Keycloak in the context of BaSyx. This is meant as a
 * helper class wrapping the Keycloak admin client library and bridging to BaSyx.
 *
 * @author wege
 */
public class KeycloakService {
  private final String serverUrl;
  private final String realm;

  private String clientId;
  private String clientSecret;
  private String userName;
  private String password;

  /**
   * Creates a new KeycloakService object.
   *
   * @param serverUrl
   *                  the base url of the keycloak server.
   * @param realm
   *                  the realm to interact with.
   */
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

  /**
   * Sets the authentication credentials that are used when making requests to the Keycloak server.
   * Those refer to objects on the Keycloak realm passed in the constructor.
   *
   * @param clientId
   *                 the id of the client in Keycloak.
   * @param clientSecret
   *                 the secret of the client in Keycloak.
   * @param userName
   *                 the name of the user to authenticate for.
   * @param password
   *                 the password of the user to authenticate for.
   */
  public void setCredentials(final String clientId, final String clientSecret, final String userName, final String password) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.userName = userName;
    this.password = password;
  }

  /**
   * This creates a connector factory which injects logic for getting an access token from Keycloak
   * before making some other network request. The token will be used in the Authorization HTTP
   * header so that the target of the connections made by this connection factory can use it for
   * authentication/authorization checks.
   *
   * @return connector factory with Keycloak access token fetching logic.
   */
  public IConnectorFactory createConnectorFactory() {
    return addr -> new JSONConnector(new HTTPConnector(addr, this::getTokenAsBearer));
  }

  /**
   * Creates a new {@link JwtBearerTokenAuthenticationConfiguration} object based on the Keycloak
   * server information. This can be used when making the security filter of a BaSyx server in order
   * to extract the JWT from the HTTP Authorization header, validating it and exporting it to
   * Spring's {@link org.springframework.security.core.context.SecurityContext}.
   *
   * @return the new {@link JwtBearerTokenAuthenticationConfiguration} object
   */
  public JwtBearerTokenAuthenticationConfiguration createJwtBearerTokenAuthenticationConfiguration() {
    return JwtBearerTokenAuthenticationConfiguration.of(
        getRealmUrl(),
        getCertsEndpoint(),
        null
    );
  }

  /**
   * Makes a {@link Keycloak} object used for interacting with the Keycloak server, basing on the
   * parameters given in the constructor and {@link KeycloakService#setCredentials} method.
   * <p>
   * Note: This gives direct access to the underlying Keycloak admin client library. So it can be
   * used for use cases not covered by this helper class.
   * </p>
   *
   * @return
   */
  public Keycloak getKeycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm(realm)
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .username(userName)
        .password(password)
        .build();
  }

  /**
   * Fetches an access token to be used as a bearer token in HTTP Authorization from the Keycloak server
   * using the parameters passed in the constructor and {@link KeycloakService#setCredentials} method.
   *
   * @return a string consisting of the token prefixed by "Bearer " if any
   */
  public Optional<String> getTokenAsBearer() {
    return getToken().map(token -> "Bearer " + token);
  }

  /**
   * Fetches an access token from the Keycloak server
   * using the parameters passed in the constructor and {@link KeycloakService#setCredentials} method.
   *
   * @return the token if any
   */
  public Optional<String> getToken() {
    final Keycloak keycloak = getKeycloak();

    return Optional.ofNullable(keycloak.tokenManager().getAccessToken().getToken());
  }
}
