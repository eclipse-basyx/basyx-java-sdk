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
	 * @param urn
	 *            the URN that describes the element.
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
