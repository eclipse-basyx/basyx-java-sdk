/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
