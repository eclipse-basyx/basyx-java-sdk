/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.extensions.aas.manager.authorized;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;

/**
 * A ConnectedAASManager that uses a HTTPSConnector with authorization.
 * Optionally, an authorized registry can also be created with an registry-URL
 * 
 * @author mueller-zhang, espen
 */
public class AuthorizedConnectedAASManager extends ConnectedAssetAdministrationShellManager {

	/**
	 * Constructor to create a ConnectedAASManager with an user defined registry and
	 * authorization supplier
	 * 
	 * @param registry
	 *            an user defined registry
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedConnectedAASManager(IAASRegistry registry, IAuthorizationSupplier authorizationSupplier) {
		super(registry, new HTTPSConnectorProvider(authorizationSupplier));
	}

	/**
	 * Constructor to create a ConnectedAASManager with authorized registry and
	 * authorization supplier
	 * 
	 * @param registryUrl
	 *            registry url to create a registry with authorization
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedConnectedAASManager(String registryUrl, IAuthorizationSupplier authorizationSupplier) {
		super(new AuthorizedAASRegistryProxy(registryUrl, authorizationSupplier), new HTTPSConnectorProvider(authorizationSupplier));
	}

}
