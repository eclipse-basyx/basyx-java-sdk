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
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.GrantedAuthoritySubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.SimpleRbacSubmodelAggregatorAuthorizer;

/**
 * Constants for the permission scopes related to the
 * {@link AuthorizedSubmodelAggregator}. Used in
 * {@link SimpleRbacSubmodelAggregatorAuthorizer},
 * {@link GrantedAuthoritySubmodelAggregatorAuthorizer}
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
