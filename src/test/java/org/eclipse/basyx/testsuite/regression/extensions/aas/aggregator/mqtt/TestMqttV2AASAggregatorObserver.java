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
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.aas.aggregator.AASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.metamodel.map.parts.ConceptDictionary;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttV2AASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttV2DecoratingAASAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
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
 * Tests events emitting with the MqttAASAggregatorObserver
 *
 * @author haque
 *
 */
public class TestMqttV2AASAggregatorObserver {
	protected AssetAdministrationShell shell;
	private static final String AASID = "aasid1";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);

	private static Server mqttBroker;
	private static IAASAggregator observedAPI;
	private static MqttClient client;
	private static MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and ObservableAASAggregator for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		observedAPI = createObservedAASAggregator();

	}

	private static IAASAggregator createObservedAASAggregator() throws MqttException {
		return new MqttV2DecoratingAASAggregatorFactory(new AASAggregatorFactory(), client).create();
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetId", new Identifier(IdentifierType.IRI, "assetId"), AssetKind.INSTANCE));
		observedAPI.createAAS(shell);
	}

	@Test
	public void testCreateAAS() {
		String newAASId = "newAASId";
		Identifier newAASIdentifier = new Identifier(IdentifierType.IRDI, newAASId);
		AssetAdministrationShell newShell = new AssetAdministrationShell(newAASId, newAASIdentifier, new Asset("newAssetId", new Identifier(IdentifierType.IRI, "newAssetId"), AssetKind.INSTANCE));
		Collection<IConceptDictionary> dict = new ArrayList<IConceptDictionary>();
		dict.add(new ConceptDictionary("conceptDict"));
		newShell.setConceptDictionary(dict);
		observedAPI.createAAS(newShell);

		assertEquals(removeConceptDictionaries(newShell), deserializePayload(listener.lastPayload));
		assertEquals(MqttV2AASAggregatorHelper.createCreateAASTopic(observedAPI.getRepositoryId()), listener.lastTopic);
	}

	@Test
	public void testUpdateAAS() {
		Collection<IConceptDictionary> dict = new ArrayList<IConceptDictionary>();
		dict.add(new ConceptDictionary("conceptDict"));
		shell.setConceptDictionary(dict);
		shell.setCategory("newCategory");
		observedAPI.updateAAS(shell);

		assertEquals(removeConceptDictionaries(shell), deserializePayload(listener.lastPayload));
		assertEquals(MqttV2AASAggregatorHelper.createUpdateAASTopic(observedAPI.getRepositoryId()), listener.lastTopic);
	}

	@Test
	public void testDeleteAAS() {
		Collection<IConceptDictionary> dict = new ArrayList<IConceptDictionary>();
		dict.add(new ConceptDictionary("conceptDict"));
		shell.setConceptDictionary(dict);
		IAssetAdministrationShell aas = observedAPI.getAAS(AASIDENTIFIER);
		observedAPI.deleteAAS(AASIDENTIFIER);

		assertEquals(removeConceptDictionaries(aas), deserializePayload(listener.lastPayload));
		assertEquals(MqttV2AASAggregatorHelper.createDeleteAASTopic(observedAPI.getRepositoryId()), listener.lastTopic);
	}
	
	private Object deserializePayload(String payload) {
		GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);
		
		return tools.deserialize(payload);
	}
	
	@SuppressWarnings("unchecked")
	private AssetAdministrationShell removeConceptDictionaries(IAssetAdministrationShell shell) {
		Map<String, Object> map = (Map<String, Object>) shell;
		AssetAdministrationShell aas = AssetAdministrationShell.createAsFacade(map);
		aas.setConceptDictionary(Collections.emptyList());
		
		return aas;
	}
}
