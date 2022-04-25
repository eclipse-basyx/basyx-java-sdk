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
package org.eclipse.basyx.vab.registry.proxy;

import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnector;
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
		this(getJSONConnectorWithProtocol(directoryUrl));
	}

	protected static boolean isHTTPSUrl(String url) {
		return url.toLowerCase().startsWith("https");
	}

	private static VABElementProxy getJSONConnectorWithProtocol(String directoryUrl) {
		if (isHTTPSUrl(directoryUrl)) {
			return new VABElementProxy("", new JSONConnector(new HTTPSConnector(directoryUrl)));
		}
		return new VABElementProxy("", new JSONConnector(new HTTPConnector(directoryUrl)));
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
