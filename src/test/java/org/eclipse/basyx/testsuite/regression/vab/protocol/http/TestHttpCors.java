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
import org.junit.Test;

/**
 * Test CORS configuration
 * 
 * @author danish
 *
 */
public class TestHttpCors {
	private static BaSyxHTTPServer server;
	
	protected static final String CONTEXT_PATH = "/aasServer";
	protected static final String DOCBASE_PATH = System.getProperty("java.io.tmpdir");
	protected static final String HOSTNAME = "localhost";
	protected static final int PORT = 4001;

	private static final String TARGET_URL = "http://" + HOSTNAME + ":" + PORT + CONTEXT_PATH + "/shells";
	protected static final String ALLOW_SPECIFIC_ORIGIN = "http://basyx-example.com";

	@After
	public void stopServer() {
		server.shutdown();
	}
	
	@Test
	public void allowSpecificCorsOrigin() {
		createAndStartHttpServerWithCORS(ALLOW_SPECIFIC_ORIGIN);

		assertEquals(ALLOW_SPECIFIC_ORIGIN, getAccessControlAllowOriginResponseHeader());
	}
	
	@Test
	public void allowAllOriginInCors() {
		String allowAllOrigin = "*";
		createAndStartHttpServerWithCORS(allowAllOrigin);

		assertEquals(allowAllOrigin, getAccessControlAllowOriginResponseHeader());
	}
	
	@Test
	public void withoutCorsConfiguration() {
		createAndStartHttpServerWithoutCORS();
		
		assertNull(getAccessControlAllowOriginResponseHeader());
	}
	
	@Test
	public void allowSpecificMethods() {
		createAndStartHttpServerWithCORS(ALLOW_SPECIFIC_ORIGIN);
		
		String allowMethods = "GET, POST, DELETE, PUT, PATCH";

		assertEquals(allowMethods, getAccessControlAllowMethodsResponseHeader());
	}
	
	@Test
	public void allowSpecificHeaders() {
		createAndStartHttpServerWithCORS(ALLOW_SPECIFIC_ORIGIN);
		
		String allowHeaders = "X-Requested-With";

		assertEquals(allowHeaders, getAccessControlAllowHeadersResponseHeader());
	}
	
	private void createAndStartHttpServerWithoutCORS() {
		BaSyxContext contextConfig = createBaseContext();
		server = new BaSyxHTTPServer(contextConfig);
		
		configureAndStartServer(contextConfig);
	}
	
	protected void createAndStartHttpServerWithCORS(String accessControlAllowOrigin) {
		BaSyxContext contextConfig = createBaseContext();
		contextConfig.setAccessControlAllowOrigin(accessControlAllowOrigin);

		configureAndStartServer(contextConfig);
	}

	private BaSyxContext createBaseContext() {
		BaSyxContext contextConfig = new BaSyxContext(CONTEXT_PATH, DOCBASE_PATH, HOSTNAME, PORT);
		contextConfig.addServletMapping("/shells/*", new SimpleVABElementServlet());
		return contextConfig;
	}

	protected void configureAndStartServer(BaSyxContext contextConfig) {
		server = new BaSyxHTTPServer(contextConfig);
		server.start();
	}

	protected String getAccessControlAllowOriginResponseHeader() {
		String accessControlAllowOrigin = "Access-Control-Allow-Origin";

		Response response = doRequest();
		return response.getHeaderString(accessControlAllowOrigin);
	}
	
	private String getAccessControlAllowMethodsResponseHeader() {
		String accessControlAllowMethods = "Access-Control-Allow-Methods";
		
		Response response = doRequest();
		return response.getHeaderString(accessControlAllowMethods);
	}
	
	private String getAccessControlAllowHeadersResponseHeader() {
		String accessControlAllowHeaders = "Access-Control-Allow-Headers";
		
		Response response = doRequest();
		return response.getHeaderString(accessControlAllowHeaders);
	}

	private Response doRequest() {
		Client client = ClientBuilder.newClient();
		WebTarget resource = client.target(TARGET_URL);

		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

		return request.get();
	}
}
