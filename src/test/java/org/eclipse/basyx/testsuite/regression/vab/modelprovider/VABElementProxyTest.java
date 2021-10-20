/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
