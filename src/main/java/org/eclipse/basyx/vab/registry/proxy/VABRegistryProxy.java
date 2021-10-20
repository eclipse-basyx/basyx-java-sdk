/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.registry.proxy;

import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;

public class VABRegistryProxy implements IVABRegistryService {
	/**
	 * Store the URL of the registry of this proxy
	 */
	protected IModelProvider provider;

	/**
	 * Constructor for a generic VAB directory proxy based on a HTTP connection
	 * 
	 * @param directoryUrl
	 *            The endpoint of the registry with a HTTP-REST interface
	 */
	public VABRegistryProxy(String directoryUrl) {
		this(new VABElementProxy("", new JSONConnector(new HTTPConnector(directoryUrl))));
	}
	
	/**
	 * Constructor for a generic VAB directory based on the registry model provider
	 * 
	 * @param provider
	 *            A model provider for the actual registry
	 */
	public VABRegistryProxy(IModelProvider provider) {
		this.provider = provider;
	}

	/**
	 * Adds a single entry to the directory
	 */
	@Override
	public IVABRegistryService addMapping(String key, String value) {
		provider.createValue(key, value);
		return this;
	}

	/**
	 * Deletes an entry from the directory
	 */
	@Override
	public void removeMapping(String key) {
		provider.deleteValue(key);
	}

	/**
	 * Returns a single entry in the directory
	 */
	@Override
	public String lookup(String id) {
		Object response = provider.getValue(id);
		
		if (response instanceof String) {
			return (String) response;
		} else {
			throw new ProviderException("Lookup returned unexpected object: " + response);
		}
	}
}
