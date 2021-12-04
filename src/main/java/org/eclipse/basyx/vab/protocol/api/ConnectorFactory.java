/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * ConnectorProvider that caches connectors for addresses to save resources
 * 
 * @author schnicke
 *
 */
public abstract class ConnectorFactory implements IConnectorFactory {

	private Map<String, IModelProvider> providerMap = new LinkedHashMap<>();

	@Override
	public IModelProvider getConnector(String addr) {
		if (!providerMap.containsKey(addr)) {
			providerMap.put(addr, createProvider(addr));
		}
		return providerMap.get(addr);
	}

	/**
	 * Creates a new provider for the given address
	 * 
	 * @param addr
	 * @return
	 */
	protected abstract IModelProvider createProvider(String addr);
}
