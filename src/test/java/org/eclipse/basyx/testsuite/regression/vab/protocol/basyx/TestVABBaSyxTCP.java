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
package org.eclipse.basyx.testsuite.regression.vab.protocol.basyx;

import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnector;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnectorFactory;
import org.eclipse.basyx.vab.protocol.basyx.server.VABBaSyxTCPInterface;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test VAB using the BaSyx protocol. This is an integration test
 * 
 * @author schnicke, pschorn
 *
 */
public class TestVABBaSyxTCP extends TestProvider {
	protected VABConnectionManager connManager = new VABConnectionManager(new TestsuiteDirectory_BaSyxNative(),
			new BaSyxConnectorFactory());

	@Rule
	public VABTCPServerResource res = new VABTCPServerResource(new VABMapProvider(new SimpleVABElement()));

	@Override
	protected VABConnectionManager getConnectionManager() {
		return connManager;
	}

	/**
	 * Tests if a call to {@link BaSyxConnector#closeConnection} of the
	 * BaSyxConnector also terminates the thread on the server side
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testSuccessfulShutdown() throws InterruptedException {
		BaSyxConnector connector = new BaSyxConnector("localhost", 6998);

		connector.getValue("integer");

		// Wait until thread is closed on server
		Thread.sleep(100);

		// Ensure that thread is closed
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		assertFalse(threads.stream().anyMatch(t -> t.getName().contains(VABBaSyxTCPInterface.class.getName())));
	}
}
