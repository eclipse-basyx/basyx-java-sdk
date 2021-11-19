/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryHelper;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryService;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

/**
 * Tests events emitting with the MqttAASRegistryService
 * 
 * @author haque
 *
 */
public class TestMqttAASRegistryService {
	
	private static final String AASID = "aasid1";
	private static final String SUBMODELID = "submodelid1";
	private static final String AASENDPOINT = "http://localhost:8080/aasList/" + AASID + "/aas";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	private static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);
	
	private static Server mqttBroker;
	private static MqttAASRegistryService eventAPI;
	private MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and AASRegistryService for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		// Create underlying registry service
		IAASRegistry registryService = new InMemoryRegistry();
		
		eventAPI = new MqttAASRegistryService(registryService, "tcp://localhost:1884", "testClient");
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}
	
	@Before
	public void setUp() {
		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		AASDescriptor aasDescriptor = new AASDescriptor(shell, AASENDPOINT);
		eventAPI.register(aasDescriptor);
		
		Submodel submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);
		eventAPI.register(AASIDENTIFIER, submodelDescriptor);
		
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}
	
	@After
	public void tearDown() {
		mqttBroker.removeInterceptHandler(listener);
	}
	
	@Test
	public void testRegisterAAS() {
		String newAASId = "aasid2";
		Identifier newIdentifier = new Identifier(IdentifierType.IRI, newAASId);
		AssetAdministrationShell shell = new AssetAdministrationShell(newAASId, newIdentifier, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		String aasEndpoint = "http://localhost:8080/aasList/" + newAASId + "/aas";
		
		AASDescriptor aasDescriptor = new AASDescriptor(shell, aasEndpoint);
		eventAPI.register(aasDescriptor);
		
		assertEquals(newAASId, listener.lastPayload);
		assertEquals(MqttAASRegistryHelper.TOPIC_REGISTERAAS, listener.lastTopic);
	}
	
	@Test
	public void testRegisterSubmodel() {
		String submodelid = "submodelid2";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, submodelid);
		Submodel submodel = new Submodel(submodelid, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + submodelid + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);
		
		eventAPI.register(AASIDENTIFIER, submodelDescriptor);
		
		assertEquals(MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(AASIDENTIFIER, newSubmodelIdentifier), listener.lastPayload);
		assertEquals(MqttAASRegistryHelper.TOPIC_REGISTERSUBMODEL, listener.lastTopic);
	}
	
	@Test
	public void testDeleteAAS() {
		eventAPI.delete(AASIDENTIFIER);
		
		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttAASRegistryHelper.TOPIC_DELETEAAS, listener.lastTopic);
	}
	
	@Test
	public void testDeleteSubmodel() {
		eventAPI.delete(AASIDENTIFIER, SUBMODELIDENTIFIER);

		assertEquals(MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(AASIDENTIFIER, SUBMODELIDENTIFIER), listener.lastPayload);
		assertEquals(MqttAASRegistryHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}
}
