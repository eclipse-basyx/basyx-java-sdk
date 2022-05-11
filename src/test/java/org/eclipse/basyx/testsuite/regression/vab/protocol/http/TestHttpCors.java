/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.testsuite.regression.vab.protocol.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test CORS configuration
 * 
 * @author danish
 *
 */
public class TestHttpCors {
	private static BaSyxContext contextConfig;
	private static BaSyxHTTPServer server;
	private static Client client;
	private static WebTarget resource;
	private static Builder request;
	private static Response response;
	
	private static final String ALLOW_SPECIFIC_ORIGIN = "http://basyx-example.com";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ALLOW_ALL_ORIGIN = "*";
	private static final String TARGET_URL = "http://localhost:4001/aasServer/shells";
	private static final String CONTEXT_PATH = "/aasServer";
	private static final String DOCBASE_PATH = System.getProperty("java.io.tmpdir");
	private static final String HOSTNAME = "localhost";
	private static final int PORT = 4001;
	
	
	@Before
	public void initializeContext() {
		contextConfig = new BaSyxContext(CONTEXT_PATH, DOCBASE_PATH, HOSTNAME, PORT);
		contextConfig.addServletMapping("/shells/*", new SimpleVABElementServlet());
		client = ClientBuilder.newClient();
	}
	
	@After
	public void stopServer() {
		server.shutdown();
	}
	
	@Test
	public void addSpecificCorsOrigin() throws InterruptedException {
		contextConfig.setAccessControlAllowOrigin(ALLOW_SPECIFIC_ORIGIN);
		
		createAndStartHttpServer();

		resource = client.target(TARGET_URL);

		buildRequest();

		response = request.get();
		
		assertEquals(ALLOW_SPECIFIC_ORIGIN, response.getHeaderString(ACCESS_CONTROL_ALLOW_ORIGIN));
	}
	
	@Test
	public void allowAllOriginInCors() throws InterruptedException {
		contextConfig.setAccessControlAllowOrigin(ALLOW_ALL_ORIGIN);
		
		createAndStartHttpServer();

		resource = client.target(TARGET_URL);

		buildRequest();

		response = request.get();
		
		assertEquals(ALLOW_ALL_ORIGIN, response.getHeaderString(ACCESS_CONTROL_ALLOW_ORIGIN));
	}
	
	@Test
	public void withoutCorsConfiguration() throws InterruptedException {
		createAndStartHttpServer();

		resource = client.target(TARGET_URL);

		buildRequest();

		response = request.get();
		
		assertNull(response.getHeaderString(ACCESS_CONTROL_ALLOW_ORIGIN)); 
	}
	
	private void createAndStartHttpServer() {
		server = new BaSyxHTTPServer(contextConfig);
		
		startServer();
	}
	
	public void startServer() {
		server.start();
	}

	private void buildRequest() {
		request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);
	}
}
