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
