/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

/**
 * Constants for the permission scopes related to the {@link AuthorizedSubmodelAggregator}.
 *
 * @author pneuschwander, wege
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
