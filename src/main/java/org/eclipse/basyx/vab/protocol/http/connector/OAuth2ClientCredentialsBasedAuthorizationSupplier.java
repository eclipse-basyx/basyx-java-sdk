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
package org.eclipse.basyx.vab.protocol.http.connector;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

/**
 * Supplier for Bearer Token based HTTP Authorization request header values utilizing
 * the OAuth2 Client Credentials Grant Flow and JSON Web Tokens.
 *
 * @author pneuschwander
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4">https://tools.ietf.org/html/rfc6749#section-4.4 (OAuth2 Client Credentials Grant)</a>
 * @see <a href="https://tools.ietf.org/html/rfc7519">https://tools.ietf.org/html/rfc7519 (JSON Web Token (JWT))</a>
 * @see <a href="https://tools.ietf.org/html/rfc6750">https://tools.ietf.org/html/rfc6750 (Bearer Token Usage)</a>
 */
public class OAuth2ClientCredentialsBasedAuthorizationSupplier implements IAuthorizationSupplier {
	private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientCredentialsBasedAuthorizationSupplier.class);
	private static final String SCOPE_DELIMITER = " ";
	private static final String BEARER_TOKEN_PREFIX = "Bearer ";

	private final Client client;
	private final JsonParser jsonParser;
	private final String tokenEndpoint;
	private final Set<String> scopes;
	private final AtomicReference<JWT> cachedAccessTokenReference;

	/**
	 * Provides supplier for Bearer Token based HTTP Authorization request header values utilizing
	 * the OAuth2 Client Credentials Grant Flow and JSON Web Tokens.
	 *
	 * @param tokenEndpoint The address of the tokenEndpoint of the Authorization Server
	 * @param clientId      The client identifier issued to the client during the registration process
	 * @param clientSecret  The client secret
	 * @param scopes        Set of scopes to be requested from the Authorization Server
	 */
	public OAuth2ClientCredentialsBasedAuthorizationSupplier(final String tokenEndpoint, final String clientId, final String clientSecret, final Set<String> scopes) {
		this.client = ClientBuilder.newClient();
		this.client.register(HttpAuthenticationFeature.basicBuilder()
				.credentials(clientId, clientSecret)
				.build());
		this.jsonParser = new JsonParser();
		this.tokenEndpoint = tokenEndpoint;
		this.scopes = new HashSet<>(scopes);
		this.cachedAccessTokenReference = new AtomicReference<>();
	}

	@Override
	public Optional<String> getAuthorization() {
		return Optional.ofNullable(this.getAccessToken())
				.map(JWT::getParsedString)
				.map(str -> BEARER_TOKEN_PREFIX + str);
	}

	@Nullable
	private JWT getAccessToken() {
		final JWT cachedAccessToken = this.cachedAccessTokenReference.get();
		if (isValidToken(cachedAccessToken)) {
			return cachedAccessToken;
		}
		final JWT freshAccessToken = getFreshAccessToken();
		this.cachedAccessTokenReference.set(freshAccessToken);
		return freshAccessToken;
	}

	private boolean isValidToken(@Nullable final JWT jwt) {
		return jwt != null && !isExpiredToken(jwt);
	}

	private boolean isExpiredToken(final JWT jwt) {
		try {
			final Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();
			if (expirationTime == null) {
				return false;
			}
			return isPastDate(expirationTime);
		} catch (ParseException e) {
			logger.warn(e.getMessage(), e);
		}
		return true;
	}

	private boolean isPastDate(final Date date) {
		final Date now = new Date();
		return now.after(date);
	}

	@Nullable
	private JWT getFreshAccessToken() {
		try {
			final Response response = requestToken();
			final String accessToken = getAccessTokenFromResponse(response);
			if (StringUtils.isNotBlank(accessToken)) {
				return JWTParser.parse(accessToken);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		return null;
	}

	private Response requestToken() {
		return this.client.target(this.tokenEndpoint).request()
				.post(
						Entity.form(
								new Form()
										.param("grant_type", "client_credentials")
										.param("scope", String.join(SCOPE_DELIMITER, this.scopes))
						)
				);
	}

	private String getAccessTokenFromResponse(final Response response) {
		final String responseString = response.readEntity(String.class);
		final JsonElement responseEl = jsonParser.parse(responseString);
		return responseEl.getAsJsonObject().get("access_token").getAsString();
	}
}
