/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.manager;

import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;



/**
 * Allows access to elements provided by the VAB
 * 
 * @author kuhn, schnicke
 *
 */
public class VABConnectionManager {

	/**
	 * Directory service reference
	 */
	protected IVABRegistryService directoryService = null;

	
	/**
	 * Store connection providers
	 */
	protected IConnectorFactory connectorFactory = null;
	
	/**
	 * Factory for creating proxies for addresses with multiple endpoints
	 */
	private ModelProxyFactory proxyFactory = null;

	/**
	 * 
	 * @param networkDirectoryService
	 *            the directory used to map ids to addresses
	 * @param providerProvider
	 *            used to get the appropriate connector for the selected address
	 */
	public VABConnectionManager(IVABRegistryService networkDirectoryService, IConnectorFactory providerProvider) {
		// Set directory service reference
		directoryService = networkDirectoryService;

		// Set connector reference
		this.connectorFactory = providerProvider;
		
		// Set proxy factory
		this.proxyFactory = new ModelProxyFactory(providerProvider);
	}
	
	/**
	 * Connect to an VAB element
	 * 
	 * @param urn the URN that describes the element. 
	 */
	public VABElementProxy connectToVABElement(String urn) {

		// Get AAS from directory
		String addr = "";

		// Lookup address in directory server
		addr = directoryService.lookup(urn);
		
		return connectToVABElementByPath(addr);
	}

	/**
	 * Connect to an VAB element on an VAB server using a qualified path
	 * 
	 * @param path
	 *            the path that describes the element location.
	 */
	public VABElementProxy connectToVABElementByPath(String path) {
		return proxyFactory.createProxy(path);	
	}
}
