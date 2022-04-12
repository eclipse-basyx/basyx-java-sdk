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
package org.eclipse.basyx.testsuite.regression.vab.manager;

import org.eclipse.basyx.testsuite.regression.vab.gateway.ConnectorProviderStub;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;

/**
 * A VABConnectionManager stub which automatically creates the directory entries
 * for added IModelProviders
 * 
 * @author schnicke
 *
 */
public class VABConnectionManagerStub extends VABConnectionManager {

	public VABConnectionManagerStub() {
		// Create Stub with default DirectoryStub/ConnectorProviderStub
		super(new VABInMemoryRegistry(), new ConnectorProviderStub());
	}

	/**
	 * Creates a stub containing a default provider
	 * 
	 * @param provider
	 */
	public VABConnectionManagerStub(IModelProvider provider) {
		this();
		// Add default mapping for empty id
		getConnectorProvider().addMapping("", provider);
		getDirectoryService().addMapping("", "");
	}

	private VABInMemoryRegistry getDirectoryService() {
		return (VABInMemoryRegistry) directoryService;
	}

	private ConnectorProviderStub getConnectorProvider() {
		return (ConnectorProviderStub) connectorFactory;
	}

	/**
	 * Add the id to the Directory and also add the mapping to the ConnectorProvider
	 * 
	 * @param id
	 * @param address
	 *            address to map to
	 * @param provider
	 */
	public void addProvider(String id, String address, IModelProvider provider) {
		getDirectoryService().addMapping(id, address);
		getConnectorProvider().addMapping(address, provider);
	}
}
