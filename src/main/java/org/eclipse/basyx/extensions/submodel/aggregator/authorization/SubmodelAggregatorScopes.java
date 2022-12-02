package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

/**
 * Constants for the OAuth2 scopes related to the
 * {@link AuthorizedSubmodelAggregator}.
 *
 * @author pneuschwander
 * @see <a href=
 *      "https://tools.ietf.org/html/rfc6749#section-3.3">https://tools.ietf.org/html/rfc6749#section-3.3</a>
 */
public final class SubmodelAggregatorScopes {
	public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:sm-aggregator:read";
	public static final String WRITE_SCOPE = "urn:org.eclipse.basyx:scope:sm-aggregator:write";

	private SubmodelAggregatorScopes() {
		// This class should not be instantiated as it serves as a holder for constants
		// only
	}
}
