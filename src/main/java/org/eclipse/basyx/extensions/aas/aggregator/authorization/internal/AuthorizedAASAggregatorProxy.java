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
package org.eclipse.basyx.extensions.aas.aggregator.authorization.internal;

import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnector;

/**
 * Local proxy class that hides HTTP calls to BaSys aggregator with enabled
 * authorization.
 *
 * @author jungjan, fischer, fried
 */
public class AuthorizedAASAggregatorProxy extends AASAggregatorProxy {

	/**
	 * Constructor for an AAS aggregator proxy based on a HTTP connection
	 *
	 * @param aasAggregatorUrl
	 *            The endpoint of the aggregator
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedAASAggregatorProxy(final String aasAggregatorUrl, final IAuthorizationSupplier authorizationSupplier) {
		super(new JSONConnector(new HTTPSConnector(harmonizeURL(aasAggregatorUrl), authorizationSupplier)));
	}

}
