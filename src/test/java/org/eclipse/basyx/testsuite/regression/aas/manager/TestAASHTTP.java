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
package org.eclipse.basyx.testsuite.regression.aas.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.testsuite.regression.aas.restapi.StubAASServlet;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests access to an AAS provided by a servlet. This is an integration test
 * 
 * @author schnicke, danish
 *
 */
public class TestAASHTTP {

	private static final String WORKING_SM_ENDPOINT = "http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas/submodels/" + StubAASServlet.SMIDSHORT + "/submodel";
	private static final String WORKING_AAS_ENDPOINT = "http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas";
	private static final String NOT_WORKING_404_ENDPOINT_1 = "http://localhost:8080/basys.sdk/Testsuite/StubAAS1/aas";
	private static final String NOT_WORKING_404_ENDPOINT_2 = "http://localhost:8080/basys.sdk/Testsuite/StubAAS2/aas";

	// Manager used to connect to the AAS
	ConnectedAssetAdministrationShellManager manager;

	private static BaSyxContext context = new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir")).addServletMapping("/Testsuite/StubAAS/*", new StubAASServlet());
	
	private AASDescriptor aasDescriptor;
	private SubmodelDescriptor submodelDescriptor;

	/**
	 * Makes sure Tomcat Server is started
	 */
	@ClassRule
	public static AASHTTPServerResource res = new AASHTTPServerResource(context);

	/**
	 * Creates the manager to be used in the test cases
	 */
	@Before
	public void build() {
		InMemoryRegistry registry = new InMemoryRegistry();
		
		aasDescriptor = createAasDescriptor(WORKING_AAS_ENDPOINT);

		registerAasDescriptorWithSubmodelDescriptor(registry, aasDescriptor);

		manager = new ConnectedAssetAdministrationShellManager(registry, new HTTPConnectorFactory());
	}

	/**
	 * Tests accessing an aas
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAAS() throws Exception {
		// Retrieve AAS
		IAssetAdministrationShell shell = manager.retrieveAAS(StubAASServlet.AASURN);

		// Check id
		assertEquals(StubAASServlet.AASIDSHORT, shell.getIdShort());

		// Retrieve submodels
		Map<String, ISubmodel> submodels = shell.getSubmodels();

		// Check content of submodels
		assertEquals(1, submodels.size());
		assertTrue(submodels.containsKey(StubAASServlet.SMIDSHORT));
	}
	
	@Test
	public void retrieveSingleAas() throws Exception {
		prepareAasDescriptorForMultipleEndpoints();
		
		IAssetAdministrationShell assetAdministrationShell = manager.retrieveAAS(StubAASServlet.AASURN);

		assertEquals(StubAASServlet.AASIDSHORT, assetAdministrationShell.getIdShort());
	}

	/**
	 * Tests accessing a submodel
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubmodel() throws Exception {
		// Retrieve Submodel
		ISubmodel sm = manager.retrieveSubmodel(StubAASServlet.AASURN, StubAASServlet.SMURN);

		// Check id
		assertEquals(StubAASServlet.SMIDSHORT, sm.getIdShort());

		// TODO: Extend
		// - retrieve properties and operations

		Map<String, IProperty> properties = sm.getProperties();
		assertEquals(3, properties.size());
		IProperty prop = properties.get("integerProperty");
		assertEquals(123, prop.getValue());

		Map<String, IOperation> operations = sm.getOperations();
		assertEquals(4, operations.size());

		IOperation op = operations.get("complex");
		assertEquals(1, op.invokeSimple(2, 1));

		op = operations.get("exception1");
		try {
			op.invokeSimple();
			fail();
		} catch (ProviderException e) {
			List<Message> msg = e.getMessages();
			assertEquals(2, msg.size());
			String msgText = msg.get(1).getText();
			assertTrue(msgText.contains("ProviderException: " + NullPointerException.class.getName()));
		}

		op = operations.get("exception2");
		try {
			op.invokeSimple();
			fail();
		} catch (ProviderException e) {
			List<Message> msg = e.getMessages();
			assertEquals(2, msg.size());
			String msgText = msg.get(1).getText();
			assertTrue(msgText.contains("ProviderException: " + SimpleAASSubmodel.EXCEPTION_MESSAGE));
		}

		Map<String, ISubmodelElement> elements = sm.getSubmodelElements();
		// 2 properties, 4 operations, 1 collection
		assertEquals(9, elements.size());
	}
	
	@Test
	public void retrieveSingleSubmodel() {
		prepareSubmodelDescriptorForMultipleEndpoints();
		
		ISubmodel submodel = manager.retrieveSubmodel(StubAASServlet.AASURN, StubAASServlet.SMURN);

		assertEquals(StubAASServlet.SMIDSHORT, submodel.getIdShort());
	}
	
	private void prepareAasDescriptorForMultipleEndpoints() {
		aasDescriptor.removeEndpoint(WORKING_AAS_ENDPOINT);
		aasDescriptor.addEndpoint(NOT_WORKING_404_ENDPOINT_1);
		aasDescriptor.addEndpoint(WORKING_AAS_ENDPOINT);
		aasDescriptor.addEndpoint(NOT_WORKING_404_ENDPOINT_2);
	}
	
	private void prepareSubmodelDescriptorForMultipleEndpoints() {
		submodelDescriptor.removeEndpoint(WORKING_SM_ENDPOINT);
		submodelDescriptor.addEndpoint(NOT_WORKING_404_ENDPOINT_1);
		submodelDescriptor.addEndpoint(WORKING_SM_ENDPOINT);
		submodelDescriptor.addEndpoint(NOT_WORKING_404_ENDPOINT_2);
	}
	
	private void registerAasDescriptorWithSubmodelDescriptor(InMemoryRegistry registry, AASDescriptor aasDescriptor) {
		submodelDescriptor = createSubmodelDescriptor();

		aasDescriptor.addSubmodelDescriptor(submodelDescriptor);

		registry.register(aasDescriptor);
	}

	private SubmodelDescriptor createSubmodelDescriptor() {
		return new SubmodelDescriptor(StubAASServlet.SMIDSHORT, StubAASServlet.SMURN, WORKING_SM_ENDPOINT);
	}

	private AASDescriptor createAasDescriptor(String url) {
		return new AASDescriptor(StubAASServlet.AASURN, url);
	}
}
