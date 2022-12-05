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
package org.eclipse.basyx.extensions.aas.registration.authorization.internal;

import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnector;

/**
 * Local proxy class that hides HTTP calls to BaSys registry with enabled
 * authorization.
 *
 * @author pneuschwander
 */
public class AuthorizedAASRegistryProxy extends AASRegistryProxy {

	/**
	 * Constructor for an AAS registry proxy based on a HTTP connection
	 *
	 * @param registryUrl
	 *            The endpoint of the registry with a HTTP-REST interface
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedAASRegistryProxy(final String registryUrl, final IAuthorizationSupplier authorizationSupplier) {
		super(new JSONConnector(new HTTPSConnector(harmonizeURL(registryUrl), authorizationSupplier)));
	}

}
