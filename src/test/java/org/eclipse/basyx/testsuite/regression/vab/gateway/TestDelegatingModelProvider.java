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

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.IModelProviderStub;
import org.eclipse.basyx.vab.gateway.DelegatingModelProvider;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.junit.Test;

/**
 * Tests the behaviour of DelegatingModelProvider
 * 
 * @author schnicke
 *
 */
public class TestDelegatingModelProvider {
	private String address;
	private IModelProviderStub stub = new IModelProviderStub();

	/**
	 * Tests if the DelegatingModelProvider delegates correctly
	 * 
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		// Test addresses
		String basyx = "basyx://12.34.56.78:9090";
		String rest = "http://abc.de//a/b/c";

		// Create DelegatingModelProvider with a stub writing the address and returning
		// the IModelProviderStub
		DelegatingModelProvider provider = new DelegatingModelProvider(new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				address = addr;
				return stub;
			}
		});

		// Get a value based on path
		provider.getValue(basyx + "//" + rest);

		// Assert that correct address was given to IConnectorProvider
		assertEquals(address, basyx);

		// Assert that stub was passed the correct rest of the path
		assertEquals(rest, stub.getPath());
	}
}
