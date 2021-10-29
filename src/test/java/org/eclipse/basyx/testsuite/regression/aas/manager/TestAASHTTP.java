/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
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
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Tests access to an AAS provided by a servlet. This is an integration test
 *
 * @author schnicke
 *
 */
public class TestAASHTTP {

	// Manager used to connect to the AAS
	ConnectedAssetAdministrationShellManager manager;

	private static BaSyxContext context = new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir")).addServletMapping("/Testsuite/StubAAS/*", new StubAASServlet());

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
		// Fill directory stub
		VABInMemoryRegistry directory = new VABInMemoryRegistry();
		directory.addMapping(StubAASServlet.AASURN.getId(), "http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas");
		directory.addMapping(StubAASServlet.SMURN.getId(), "http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas/submodels/" + StubAASServlet.SMIDSHORT + "/submodel");

		InMemoryRegistry registry = new InMemoryRegistry();

		// Create aas descriptor for the aas registry
		AASDescriptor aasDescriptor = new AASDescriptor(StubAASServlet.AASURN, new Endpoint("http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas"));

		// Create the submodel descriptor
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(StubAASServlet.SMIDSHORT, StubAASServlet.SMURN,
				new Endpoint("http://localhost:8080/basys.sdk/Testsuite/StubAAS/aas/submodels/" + StubAASServlet.SMIDSHORT + "/submodel"));

		// add submodel descriptor to the aas descriptor
		aasDescriptor.addSubmodelDescriptor(submodelDescriptor);

		// register the aas in the registry
		registry.register(aasDescriptor);

		// Create manager using the directory stub an the HTTPConnectorProvider
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
}
