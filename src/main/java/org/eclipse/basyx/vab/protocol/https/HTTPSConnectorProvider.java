/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.https;

import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;

/**
 * An HTTPS Connector provider class
 * which uses a HTTPSConnector in domain having self signed certificate
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
