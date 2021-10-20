/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
