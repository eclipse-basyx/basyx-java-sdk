/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.gateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * Maps an incoming address to an IConnectorProvider based on the protocol used
 * in the path<br>
 * E.g. basyx://* can be mapped to the BasyxNative connector, http://* can be
 * mapped to the HTTP/REST connector
 * 
 * @author schnicke
 *
 */
public class ConnectorProviderMapper implements IConnectorFactory {

	private Map<String, IConnectorFactory> providerMap = new LinkedHashMap<>();

	/**
	 * 
	 * @param prefix
	 *            assumed without ending colon or slashes, e.g. <i>basyx</i> but not
	 *            <i>basyx://</i>
	 * @param provider
	 */
	public void addConnectorProvider(String prefix, IConnectorFactory provider) {
		providerMap.put(prefix, provider);
	}

	public void removeConnectorProvider(String prefix) {
		providerMap.remove(prefix);
	}

	@Override
	public IModelProvider getConnector(String addr) {
		return providerMap.get(getPrefix(addr)).create(addr);
	}

	/**
	 * Returns the prefix, e.g. address <i>basyx://*</i> returns <i>basyx</i>
	 * 
	 * @param addr
	 * @return
	 */
	private String getPrefix(String addr) {
		String prefix = addr.split("//")[0];
		return prefix.replaceFirst(":", "");
	}

}
