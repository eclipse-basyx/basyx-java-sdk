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

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
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
	private static ObservableAASAggregator observedAPI;
	private static MqttAASAggregatorObserver mqttObserver;
	private MqttTestListener listener;

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

		// Create underlying aas aggregator
		IAASAggregator aggregator = new AASAggregator();
		observedAPI = new ObservableAASAggregator(aggregator);
		
		// Create mqtt as an observer
		mqttObserver = new MqttAASAggregatorObserver("tcp://localhost:1884", "testClient");
		observedAPI.addObserver(mqttObserver);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}
	
	@Before
	public void setUp() {
		shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		observedAPI.createShell(shell);
		
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}
	
	@After
	public void tearDown() {
		mqttBroker.removeInterceptHandler(listener);
	}
	
	@Test
	public void testCreateAAS() {
		String aasId2 = "aas2";
		Identifier identifier2 = new Identifier(IdentifierType.IRDI, aasId2);
		AssetAdministrationShell shell2 = new AssetAdministrationShell(aasId2, identifier2, new Asset("assetid2", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		observedAPI.createShell(shell2);

		assertEquals(aasId2, listener.lastPayload);
		assertEquals(MqttAASAggregatorObserver.TOPIC_CREATEAAS, listener.lastTopic);
	}
	
	@Test
	public void testUpdateAAS() {
		shell.setCategory("newCategory");
		observedAPI.updateAAS(shell);
		
		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttAASAggregatorObserver.TOPIC_UPDATEAAS, listener.lastTopic);
	}
	
	@Test
	public void testDeleteAAS() {
		observedAPI.deleteAAS(AASIDENTIFIER);

		assertEquals(AASID, listener.lastPayload);
		assertEquals(MqttAASAggregatorObserver.TOPIC_DELETEAAS, listener.lastTopic);
	}
}
