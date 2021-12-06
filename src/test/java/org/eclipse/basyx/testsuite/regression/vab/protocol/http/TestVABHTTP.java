/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.protocol.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.testsuite.regression.vab.modelprovider.TestProvider;
import org.eclipse.basyx.testsuite.regression.vab.support.RecordingProvider;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.helper.HTTPUploadHelper;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test VAB using HTTP protocol. This is an integration test
 * 
 * @author schnicke, pschorn
 *
 */
public class TestVABHTTP extends TestProvider {
	public static final String AASX_PATH = "src/test/resources/aas/factory/aasx/01_Festo.aasx";
	
	private static final String RECORDER_URL = "http://localhost:8080/basys.sdk/Testsuite/Recorder/";
	private static final String SIMPLE_VAB_URL = "http://localhost:8080/basys.sdk/Testsuite/SimpleVAB";
	
	protected VABConnectionManager connManager = new VABConnectionManager(new TestsuiteDirectory(),
			new HTTPConnectorFactory());

	private RecordingProvider recorder = new RecordingProvider(new VABMapProvider(new LinkedHashMap<>()));

	/**
	 * Makes sure Tomcat Server is started after before each test case
	 */
	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(
			new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir"))
					.addServletMapping("/Testsuite/SimpleVAB/*", new SimpleVABElementServlet())
					.addServletMapping("/Testsuite/Recorder/*", new VABHTTPInterface<RecordingProvider>(recorder)));

	@Override
	protected VABConnectionManager getConnectionManager() {
		return connManager;
	}

	/**
	 * Tests for URL with no ending slash when accessing the root element, e.g.
	 * http://localhost:8080/basys.sdk/Testsuite/SimpleVAB <br />
	 * The SDK ensures that each access ends with a <i>/</i>. However, browser
	 * requests do not necessarily conform to this
	 */
	@Test
	public void testRootURL() {
		performRequest(SIMPLE_VAB_URL);
	}

	/**
	 * Tests, if the provider throws a ResourceNotFoundException, if the HTTP endpoint
	 * could not be reached
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void testResourceNotFound() {
		IModelProvider httpProvider = new HTTPConnectorFactory().getConnector("notFound");
		httpProvider.getValue("");
	}

	/**
	 * Tests if multiple parameters are correctly accepted and passed to the
	 * provider
	 */
	@Test
	public void testParameters() {
		recorder.reset();

		String parameterRequest = "test?a=1,2&b=3,4";

		performRequestNoException(RECORDER_URL + parameterRequest);
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

		performRequestNoException(RECORDER_URL + parameterRequest);

		List<String> paths = recorder.getPaths();

		assertEquals(1, paths.size());
		assertEquals(parameterRequest, VABPathTools.stripSlashes(paths.get(0)));
	}
	
	@Test
	public void testUpload() throws ClientProtocolException, IOException {
		String parameterRequest = "aasx";
		String uploadURL = RECORDER_URL + parameterRequest;
		String strToSend = "1";
		HTTPUploadHelper.uploadHTTPPost(new ByteArrayInputStream(strToSend.getBytes()), uploadURL);
		
		List<String> paths = recorder.getPaths();
		assertEquals(1, paths.size());
		assertEquals(parameterRequest, VABPathTools.stripSlashes(paths.get(0)));
		
		Object retStr = recorder.getValue(parameterRequest);
		assertEquals(strToSend, retStr);
	}
	
	@Test
	public void invokeExceptionFunction() {
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
		try {
			connVABElement.invokeOperation("operations/providerException/" + Operation.INVOKE);
			fail();
		} catch (ProviderException e) {
			List<Message> msg = e.getMessages();
			assertEquals(2, msg.size());
			String msgText = msg.get(1).getText();
			assertTrue(msgText.contains("ProviderException: " + SimpleVABElement.EXCEPTION_MESSAGE));
		}
	}

	@Test
	public void invokeNullPointerExceptionFunction() {
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
		try {
			connVABElement.invokeOperation("operations/nullException/" + Operation.INVOKE);
			fail();
		} catch (ProviderException e) {
			List<Message> msg = e.getMessages();
			assertEquals(2, msg.size());
			String msgText = msg.get(1).getText();
			assertTrue(msgText.contains("ProviderException: " + NullPointerException.class.getName()));
		}
	}

	/**
	 * Performs an HTTP request on an URL
	 * 
	 * @param URL
	 */
	private void performRequest(String URL) {
		Client client = ClientBuilder.newClient();


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
