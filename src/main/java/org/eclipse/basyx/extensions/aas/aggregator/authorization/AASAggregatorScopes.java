/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

/**
 * Constants for the OAuth2 scopes related to the
 * {@link AuthorizedAASAggregator}.
 *
 * @author jungjan, fried, fischer
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-3.3">https://tools.ietf.org/html/rfc6749#section-3.3</a>
 */
public final class AASAggregatorScopes {
	public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:aas-aggregator:read";
	public static final String WRITE_SCOPE = "urn:org.eclipse.basyx:scope:aas-aggregator:write";

	private AASAggregatorScopes() {
		// This class should not be instantiated as it serves as a holder for constants only
	}
}
