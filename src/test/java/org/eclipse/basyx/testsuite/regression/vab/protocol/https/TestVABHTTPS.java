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
package org.eclipse.basyx.testsuite.regression.vab.protocol.https;

import static org.junit.Assert.assertEquals;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.SimpleVABElementServlet;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestVABHTTP;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.testsuite.regression.vab.support.RecordingProvider;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;
import org.eclipse.basyx.vab.protocol.https.JerseyHttpsClientFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test VAB using HTTPS protocol. This is an integration test
 * 
 * @author haque
 *
 */
public class TestVABHTTPS extends TestProvider {

	protected VABConnectionManager connManager;

	private RecordingProvider recorder = new RecordingProvider(new VABMapProvider(new LinkedHashMap<>()));

	/**
	 * Makes sure Tomcat Server is started after before each test case
	 */
	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir"), "localhost", 8080, true, "resources/ssl.cert", "pass123")
			.addServletMapping("/Testsuite/SimpleVAB/*", new SimpleVABElementServlet()).addServletMapping("/Testsuite/Recorder/*", new VABHTTPInterface<RecordingProvider>(recorder)));

	@Before
	public void setUp() {
		HTTPSConnectorProvider httpsConnectorProvider = new HTTPSConnectorProvider();
		httpsConnectorProvider.disableValidation();
		connManager = new VABConnectionManager(new TestsuiteDirectory("https"), httpsConnectorProvider);
	}

	@Override
	protected VABConnectionManager getConnectionManager() {
		return connManager;
	}

	/**
	 * Tests for URL with no ending slash when accessing the root element, e.g.
	 * http://localhost:8080/basys.sdk/Testsuite/SimpleVAB <br />
	 * The SDK ensures that each access ends with a <i>/</i>. However, browser
	 * requests do not necessarily conform to this
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	@Test
	public void testRootURL() throws KeyManagementException, NoSuchAlgorithmException {
		performRequest("https://localhost:8080/basys.sdk/Testsuite/SimpleVAB");
	}

	/**
	 * Tests if multiple parameters are correctly accepted and passed to the
	 * provider
	 */
	@Test
	public void testParameters() {
		recorder.reset();

		String parameterRequest = "test?a=1,2&b=3,4";

		performRequestNoException("https://localhost:8080/basys.sdk/Testsuite/Recorder/" + parameterRequest);

		List<String> paths = recorder.getPaths();

		assertEquals(1, paths.size());
		assertEquals(parameterRequest, paths.get(0));
	}

	/**
	 * Tests if having no parameter has no influence on the path
	 */
	@Test
	public void testNoParameters() {
		recorder.reset();

		String parameterRequest = "test";

		performRequestNoException("https://localhost:8080/basys.sdk/Testsuite/Recorder/" + parameterRequest);

		List<String> paths = recorder.getPaths();

		assertEquals(1, paths.size());
		assertEquals(parameterRequest, VABPathTools.stripSlashes(paths.get(0)));
	}

	/**
	 * Performs an HTTP request on an URL
	 * 
	 * @param URL
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private void performRequest(String URL) throws KeyManagementException, NoSuchAlgorithmException {
		Client client = JerseyHttpsClientFactory.getJerseyHTTPSClientWithoutValidation();

		// Called URL
		WebTarget resource = client.target(URL);

		// Build request, set JSON encoding
		Builder request = resource.request();
		request.accept(MediaType.APPLICATION_JSON);

		// Perform request
		request.get(String.class);
	}

	/**
	 * Same as {@link TestVABHTTP#performRequest} but ignores exceptions
	 * 
	 * @param URL
	 */
	private void performRequestNoException(String URL) {
		try {
			performRequest(URL);
		} catch (Exception e) {
			// Does not matter
		}
	}
}
