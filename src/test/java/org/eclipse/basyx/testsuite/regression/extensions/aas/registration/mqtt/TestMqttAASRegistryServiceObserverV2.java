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
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryServiceV2;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryHelper;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryHelperV2;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServiceObserverV2;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
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
 * Tests events emitting with the MqttAASRegistryServiceObserver
 * 
 * @author haque
 *
 */
public class TestMqttAASRegistryServiceObserverV2 {

	private static final String AASID = "aasid1";
	private static final String SUBMODELID = "submodelid1";
	private static final String AASENDPOINT = "http://localhost:8080/aasList/" + AASID + "/aas";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	private static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);

	private static Server mqttBroker;
	private static ObservableAASRegistryServiceV2 observedAPI;
	private static MqttAASRegistryServiceObserverV2 mqttObserver;
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
		observedAPI = new ObservableAASRegistryServiceV2(registryService);

		mqttObserver = new MqttAASRegistryServiceObserverV2("tcp://localhost:1884", "testClient");
		observedAPI.addObserver(mqttObserver);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		AASDescriptor aasDescriptor = new AASDescriptor(shell, AASENDPOINT);
		observedAPI.register(aasDescriptor);

		Submodel submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);
		observedAPI.register(AASIDENTIFIER, submodelDescriptor);

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
		observedAPI.register(aasDescriptor);

		assertEquals(aasDescriptor, deserializePayload(listener.lastPayload));
		assertEquals(MqttAASRegistryHelperV2.createCreateAASTopic(this.observedAPI.getRegistryId()), listener.lastTopic);
	}

	@Test
	public void testRegisterSubmodel() {
		String submodelid = "submodelid2";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, submodelid);
		Submodel submodel = new Submodel(submodelid, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + submodelid + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);

		observedAPI.register(AASIDENTIFIER, submodelDescriptor);

		assertEquals(submodelDescriptor, deserializePayload(listener.lastPayload));
		assertEquals(MqttAASRegistryHelperV2.createCreateSubmodelTopic(this.observedAPI.getRegistryId()), listener.lastTopic);
	}

	@Test
	public void testDeleteAAS() {		
		AASDescriptor aasDescriptor = observedAPI.lookupAAS(AASIDENTIFIER);
		observedAPI.delete(AASIDENTIFIER);

		assertEquals(aasDescriptor, deserializePayload(listener.lastPayload));
		assertEquals(MqttAASRegistryHelperV2.createDeleteAASTopic(this.observedAPI.getRegistryId()), listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodel() {
		observedAPI.delete(AASIDENTIFIER, SUBMODELIDENTIFIER);

		assertEquals(MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(AASIDENTIFIER, SUBMODELIDENTIFIER), listener.lastPayload);
		assertEquals(MqttAASRegistryHelperV2.createDeleteSubmodelTopic(this.observedAPI.getRegistryId()), listener.lastTopic);
	}
	
	public Object deserializePayload(String payload) {
		GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);
		
		return tools.deserialize(payload);
	}
}
