/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.aggregator.AASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttDecoratingAASAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
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
public class TestMqttAASAggregatorObserver {
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
		return new MqttDecoratingAASAggregatorFactory(new AASAggregatorFactory(), client).create();
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
		observedAPI.createAAS(newShell);

		assertEquals(newAASId, listener.lastPayload);
		assertEquals(MqttAASAggregatorHelper.TOPIC_CREATEAAS, listener.lastTopic);
	}

	@Test
	public void testUpdateAAS() {
		shell.setCategory("newCategory");
		observedAPI.updateAAS(shell);

		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttAASAggregatorHelper.TOPIC_UPDATEAAS, listener.lastTopic);
	}

	@Test
	public void testDeleteAAS() {
		observedAPI.deleteAAS(AASIDENTIFIER);

		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttAASAggregatorHelper.TOPIC_DELETEAAS, listener.lastTopic);
	}
}
