/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.http.server;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

/**
 * Configuration for a Resource Server to support JWT Bearer Tokens for authentication.
 *
 * @author pneuschwander
 */
public class JwtBearerTokenAuthenticationConfiguration {
	private final String issuerUri;
	private final String jwkSetUri;
	@Nullable
	private final String requiredAud;

	private JwtBearerTokenAuthenticationConfiguration(final String issuerUri, final String jwkSetUri, @Nullable final String requiredAud) {
		this.issuerUri = issuerUri;
		this.jwkSetUri = jwkSetUri;
		this.requiredAud = requiredAud;
	}

	/**
	 * Provides configuration for a Resource Server to support JWT Bearer Tokens for authentication.
	 *
	 * @param issuerUri   URI addressing the Token Issuer / Authorization Server
	 * @param jwkSetUri   URI addressing the JWK Set to be used to decode and validate JWTs
	 * @param requiredAud Audience the JWT has to contain in order to be accepted
	 */
	public static JwtBearerTokenAuthenticationConfiguration of(final String issuerUri, final String jwkSetUri, @Nullable final String requiredAud) {
		if (!isValidIssuerUri(issuerUri)) {
			throw new IllegalArgumentException("invalid issuerUri");
		}
		if (!isValidJwkSetUri(jwkSetUri)) {
			throw new IllegalArgumentException("invalid jwkSetUri");
		}
		if(requiredAud != null && !isValidRequiredAudience(requiredAud)){
			throw new IllegalArgumentException("invalid requiredAud");
		}
		return new JwtBearerTokenAuthenticationConfiguration(issuerUri, jwkSetUri, requiredAud);
	}

	private static boolean isValidIssuerUri(@Nullable final String issuerUri) {
		return StringUtils.isNotBlank(issuerUri);
	}

	private static boolean isValidJwkSetUri(@Nullable final String jwkSetUri) {
		return StringUtils.isNotBlank(jwkSetUri);
	}

	private static boolean isValidRequiredAudience(@Nullable final String requiredAud) {
		return StringUtils.isNotBlank(requiredAud);
	}

	public String getIssuerUri() {
		return issuerUri;
	}

	public String getJwkSetUri() {
		return jwkSetUri;
	}

	public Optional<String> getRequiredAud() {
		return Optional.ofNullable(requiredAud);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("JwtBearerTokenAuthenticationConfiguration{");
		sb.append("issuerUri='").append(issuerUri).append('\'');
		sb.append(", jwkSetUri='").append(jwkSetUri).append('\'');
		sb.append(", requiredAud='").append(requiredAud).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof JwtBearerTokenAuthenticationConfiguration)) return false;
		final JwtBearerTokenAuthenticationConfiguration that = (JwtBearerTokenAuthenticationConfiguration) o;
		return Objects.equals(getIssuerUri(), that.getIssuerUri()) && Objects.equals(getJwkSetUri(), that.getJwkSetUri()) && Objects.equals(getRequiredAud(), that.getRequiredAud());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getIssuerUri(), getJwkSetUri(), getRequiredAud());
	}
}
