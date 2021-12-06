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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.gateway.ConnectorProviderMapper;
import org.eclipse.basyx.vab.gateway.DelegatingModelProvider;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.basyx.connector.BaSyxConnectorFactory;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the integration of DelegatingModelProvider and
 * ConnectorProviderMapper work as expected
 * 
 * @author schnicke
 *
 */
public class TestGateway {
	private BaSyxTCPServer<VABMapProvider> server;
	private BaSyxTCPServer<DelegatingModelProvider> basyxGateway;
	private BaSyxHTTPServer httpGateway;

	@Before
	public void build() { // Create VAB element
		Map<String, Object> vabElem = new LinkedHashMap<String, Object>();
		vabElem.put("propertyA", 10);

		// Provide it using VABMapProvider and a tcp server on port 6998
		server = new BaSyxTCPServer<>(new VABMapProvider(vabElem), 6998);

		// Create ConnectorProviderMapper and add mapping from "basyx" to
		// BaSyxConnectorProvider for gateway
		ConnectorProviderMapper gatewayMapper = new ConnectorProviderMapper();
		gatewayMapper.addConnectorProvider("basyx", new BaSyxConnectorFactory());
		gatewayMapper.addConnectorProvider("http", new HTTPConnectorFactory());

		// Create tcp gateway using DelegatingModelProvider
		basyxGateway = new BaSyxTCPServer<>(new DelegatingModelProvider(gatewayMapper), 6999);
		
		// Create a http gateway using DelegatingModelProvider
		DelegatingModelProvider httpGWProvider = new DelegatingModelProvider(gatewayMapper);
		BaSyxContext context = new BaSyxContext("", "", "localhost", 5123);
		context.addServletMapping("/path/to/gateway/*", new VABHTTPInterface<DelegatingModelProvider>(httpGWProvider));
		httpGateway = new BaSyxHTTPServer(context);

		// Start element provider and gateway
		server.start();
		basyxGateway.start();
		httpGateway.start();
	}

	/**
	 * Tests the following gateway scenario: <br />
	 * Creates VABElementProxy using gateway; gateway forwards call to serverA
	 * providing a VAB element <br />
	 * <b>Assumption:</b> The path has already been retrieved from a directory
	 * <br />
	 * <ul>
	 * <li>The following request is processed:
	 * <i>basyx://127.0.0.1:6999//basyx://127.0.0.1:6998//propertyA</i></li>
	 * <li>The local connector connects to gateway on <i>127.0.0.1:6999</i> via
	 * tcp</li>
	 * <li>Connector sends request <i>basyx://127.0.0.1:6998//propertyA</i> to
	 * gateway</li>
	 * <li>Gateway forwards the request <i>propertyA</i> to <i>127.0.0.1:6998</i>
	 * via tcp</li>
	 * <li>server handles the request <i>propertyA</i></li>
	 * </ul>
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	@Test
	public void test() throws UnknownHostException, IOException {
		// Create Directory, here it is configured statically, of course a dynamic
		// request to e.g. a servlet is also possible
		VABInMemoryRegistry directory = new VABInMemoryRegistry();
		directory.addMapping("Elem", "http://localhost:5123/path/to/gateway//basyx://127.0.0.1:6999//basyx://127.0.0.1:6998");

		// Create ConnectionProviderMapper for client
		ConnectorProviderMapper clientMapper = new ConnectorProviderMapper();
		clientMapper.addConnectorProvider("http", new HTTPConnectorFactory());

		// Create VABConnectionManager
		VABConnectionManager manager = new VABConnectionManager(directory, clientMapper);

		// Retrieve VABElementProxy
		VABElementProxy proxy = manager.connectToVABElement("Elem");

		// Test if the value is retrieved correctly
		assertEquals(10, proxy.getValue("propertyA"));
	}

	@After
	public void breakdown() {
		if (server != null) {
			server.stop();
		}

		if (basyxGateway != null) {
			basyxGateway.stop();
		}

		if (httpGateway != null) {
			httpGateway.shutdown();
		}
	}
}
