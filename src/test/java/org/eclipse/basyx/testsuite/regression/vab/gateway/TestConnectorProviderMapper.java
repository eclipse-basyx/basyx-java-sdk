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
