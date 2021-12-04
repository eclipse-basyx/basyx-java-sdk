/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.gateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * A simple ConnectorProvider stub returning connectors based on a String
 * mapping
 * 
 * @author schnicke
 *
 */
public class ConnectorProviderStub implements IConnectorFactory {
	private Map<String, IModelProvider> providerMap = new LinkedHashMap<>();

	public void addMapping(String addr, IModelProvider provider) {
		providerMap.put(addr, provider);
	}

	@Override
	public IModelProvider getConnector(String addr) {
		if (!providerMap.containsKey(addr)) {
			throw new RuntimeException("Unknown addr " + addr);
		}
		return providerMap.get(addr);
	}
}
