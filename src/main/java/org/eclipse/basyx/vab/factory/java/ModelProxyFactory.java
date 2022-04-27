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
package org.eclipse.basyx.vab.factory.java;

import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * A factory for creating model providers out of addresses with multiple
 * endpoints included.
 * 
 * @author espen
 *
 */
public class ModelProxyFactory {
	private IConnectorFactory connectorFactory;

	public ModelProxyFactory(IConnectorFactory connectorFactory) {
		this.connectorFactory = connectorFactory;
	}

	/**
	 * Returns an element proxy for a path that can contain multiple endpoints
	 * 
	 * @param path
	 *            A path containing one or more endpoints.
	 * @return A proxy that directly connects to the element referenced by the given
	 *         path.
	 */
	public VABElementProxy createProxy(String path) {
		// Create a model provider for the first endpoint
		String addressEntry = VABPathTools.getFirstEndpoint(path);
		IModelProvider provider = connectorFactory.create(addressEntry);

		// Return a proxy for the whole path using the connector to the first endpoint
		String subPath = VABPathTools.removeFirstEndpoint(path);
		return new VABElementProxy(subPath, provider);
	}
}
