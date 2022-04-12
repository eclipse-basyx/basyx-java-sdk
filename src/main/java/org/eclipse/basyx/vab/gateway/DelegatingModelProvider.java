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

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.factory.java.ModelProxyFactory;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;

/**
 * IModelProvider that delegates all calls to a Connector to enable gateway
 * functionality
 * 
 * @author schnicke
 *
 */
public class DelegatingModelProvider implements IModelProvider {

	// Provider that provides the connectors
	private ModelProxyFactory proxyFactory;

	public DelegatingModelProvider(IConnectorFactory connectorFactory) {
		super();
		this.proxyFactory = new ModelProxyFactory(connectorFactory);
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		return getProvider(path).getValue("");
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		getProvider(path).setValue("", newValue);
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		getProvider(path).createValue("", newEntity);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		getProvider(path).deleteValue("");
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		getProvider(path).deleteValue("", obj);
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		return getProvider(path).invokeOperation("", parameter);
	}

	/**
	 * Returns the appropriate connector based on address
	 * 
	 * @param path
	 * @return
	 */
	private IModelProvider getProvider(String path) {
		return proxyFactory.createProxy(path);
	}
}
