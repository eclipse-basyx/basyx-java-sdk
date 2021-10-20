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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.vab.gateway.ConnectorProviderMapper;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.junit.Test;

/**
 * Tests the behaviour of ConnectorProviderMapper
 * 
 * @author schnicke
 *
 */
public class TestConnectorProviderMapper {
	// Container Strings
	private String basyxAddr = "";
	private String httpAddr = "";

	/**
	 * Tests if an address is mapped to the correct IConnectorProvider
	 */
	@Test
	public void test() {
		// Create ConnectorProviderMapper
		ConnectorProviderMapper provider = new ConnectorProviderMapper();

		// Add basyx IConnectorProvider stub
		provider.addConnectorProvider("basyx", new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				basyxAddr = addr;
				return null;
			}
		});

		// Add http IConnectorProvider stub
		provider.addConnectorProvider("http", new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				httpAddr = addr;
				return null;
			}
		});

		// Check correct mapping of http, i.e. httpAddr is set but not basyxAddr
		String url = "http://test.url.com//path://SensorAAS";
		provider.getConnector(url);
		assertEquals(url, httpAddr);
		assertTrue(basyxAddr.isEmpty());
		httpAddr = "";

		// Check correct mapping of basyx, i.e. basyxAddr is set but not httpAddr
		String addr = "basyx://10.11.12.13:8989//path://SensorAAS";
		provider.getConnector(addr);
		assertEquals(addr, basyxAddr);
		assertTrue(httpAddr.isEmpty());
	}
}
