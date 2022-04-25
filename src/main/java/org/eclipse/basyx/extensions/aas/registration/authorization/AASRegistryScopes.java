package org.eclipse.basyx.extensions.aas.registration.authorization;

/**
 * Constants for the OAuth2 scopes related to the {@link AuthorizedAASRegistry}.
 *
 * @author pneuschwander
 * @see <a href=
 *      "https://tools.ietf.org/html/rfc6749#section-3.3">https://tools.ietf.org/html/rfc6749#section-3.3</a>
 */
public final class AASRegistryScopes {
	public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:aas-registry:read";
	public static final String WRITE_SCOPE = "urn:org.eclipse.basyx:scope:aas-registry:write";

	private AASRegistryScopes() {
		// This class should not be instantiated as it serves as a holder for constants
		// only
	}
}
