package org.eclipse.basyx.extensions.submodel.authorization;

import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedSubmodelAggregator;

/**
 * Constants for the OAuth2 scopes related to the {@link AuthorizedSubmodelAggregator}.
 *
 * @author pneuschwander
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-3.3">https://tools.ietf.org/html/rfc6749#section-3.3</a>
 */
public final class SubmodelAPIScopes {
	public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:sm-api:read";
	public static final String WRITE_SCOPE = "urn:org.eclipse.basyx:scope:sm-api:write";

	private SubmodelAPIScopes() {
		// This class should not be instantiated as it serves as a holder for constants only
	}
}
