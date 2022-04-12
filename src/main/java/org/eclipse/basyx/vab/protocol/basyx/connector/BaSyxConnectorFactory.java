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
package org.eclipse.basyx.vab.protocol.basyx.connector;

import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;


/**
 * A connector provider for TCP/BaSyx protocol
 * 
 * @author schnicke, kuhn
 *
 */
public class BaSyxConnectorFactory extends ConnectorFactory {

	
	/**
	 * Create the provider
	 */
	@Override
	protected IModelProvider createProvider(String address) {
		// Create address
		address = VABPathTools.getFirstEndpoint(address);
		address = address.replaceFirst("basyx://", "");
		String hostName = address.substring(0, address.indexOf(':'));
		String[] splitted = address.split("/");
		int hostPort = Integer.parseInt(splitted[0].substring(address.indexOf(':') + 1));

		// Create connector, connect
		IModelProvider provider = new JSONConnector(new BaSyxConnector(hostName, hostPort));
		
		// Create a proxy, if necessary
		String path = address.replaceFirst(hostName + ":" + hostPort, "");
		if (!path.isEmpty() && !path.equals("/")) {
			provider = new VABElementProxy(path, provider);
		}

		return provider;
	}

}
