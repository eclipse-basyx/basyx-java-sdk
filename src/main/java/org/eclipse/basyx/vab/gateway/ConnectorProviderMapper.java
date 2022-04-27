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
