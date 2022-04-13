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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.model.VABModelMap;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.Test;

/**
 * Tests the VABElementProxy class
 * 
 * @author schnicke
 *
 */
public class VABElementProxyTest {

	/**
	 * Tests the capability of VABElementProxy to create a new proxy element
	 * pointing deeper into the element it is a proxy to
	 */
	@Test
	public void testGetDeepProxy() {
		// Create test map
		VABModelMap<Object> map = new VABModelMap<>();
		map.putPath("a/b/c", 0);

		// Setup provider and connection manager
		VABMapProvider provider = new VABMapProvider(map);
		VABConnectionManagerStub stub = new VABConnectionManagerStub(provider);

		// Connect to element
		VABElementProxy proxy = stub.connectToVABElement("");

		// Connect to element <i>a/b</i>
		VABElementProxy bProxy = proxy.getDeepProxy("a/b");

		assertEquals(0, bProxy.getValue("c"));
	}
}
