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
package org.eclipse.basyx.vab.protocol.https;

import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;

/**
 * An HTTPS Connector provider class which uses a HTTPSConnector in domain
 * having self signed certificate
 * 
 * @author haque
 *
 */
public class HTTPSConnectorProvider extends ConnectorFactory {
	private IAuthorizationSupplier supplier;
	private boolean validateFlag = true;

	public HTTPSConnectorProvider() {
	}

	/**
	 * Constructor to create a HTTPSConenctorProvider with a given authorization
	 * supplier
	 * 
	 * @param supplier
	 *            given authorization supplier
	 */
	public HTTPSConnectorProvider(IAuthorizationSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * Enable the validation of hostname in Jersey Https client
	 */
	public void enableValidation() {
		this.validateFlag = true;
	}

	/**
	 * Disable the validation of hostname in Jersey Https client
	 */
	public void disableValidation() {
		this.validateFlag = false;
	}

	/**
	 * returns HTTPSConnetor wrapped with ConnectedHashmapProvider that handles
	 * message header information
	 */
	@Override
	protected IModelProvider createProvider(String addr) {
		return new JSONConnector(new HTTPSConnector(addr, supplier, validateFlag));
	}
}
