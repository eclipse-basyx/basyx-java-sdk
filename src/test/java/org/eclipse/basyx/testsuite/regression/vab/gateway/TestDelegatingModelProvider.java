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
