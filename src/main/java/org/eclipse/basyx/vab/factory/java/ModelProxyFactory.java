/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.factory.java;

import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * A factory for creating model providers out of addresses with multiple endpoints included.
 * 
 *  @author espen
 *
 */
public class ModelProxyFactory {
	private IConnectorFactory connectorFactory;

	public ModelProxyFactory(IConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}
	
	/**
	 * Returns an element proxy for a path that can contain multiple endpoints
	 * 
	 * @param path A path containing one or more endpoints.
	 * @return A proxy that directly connects to the element referenced by the given path.
	 */
	public VABElementProxy createProxy(String path) {
		// Create a model provider for the first endpoint
		String addressEntry = VABPathTools.getFirstEndpoint(path);
		IModelProvider provider = connectorFactory.getConnector(addressEntry);
		
		// Return a proxy for the whole path using the connector to the first endpoint
		String subPath = VABPathTools.removeFirstEndpoint(path);
		return new VABElementProxy(subPath, provider);
	}
}
