/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
import org.eclipse.basyx.extensions.aas.aggregator.MqttAASAggregatorPayloadParser;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttDecoratingAASAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
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
 * Tests the Payload Parser for Payloads sent by MqttAASAggregator
 * 
 * @author fried
 *
 */
public class TestMqttAASAggregatorPayloadParser {
	private static Server mqttBroker;
	private static MqttTestListener listener;

	private static AssetAdministrationShell shell;
	private static IAASAggregator aggregator;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		startMqttBrokerAndAddListener();

		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();
		aggregator = new MqttDecoratingAASAggregatorFactory(new AASAggregatorFactory(), client).create();
	}

	@Before
	public void setUp() {
		shell = new AssetAdministrationShell("shellIdShort", new Identifier(IdentifierType.CUSTOM, "shellIdentifier"), new Asset("assetIdShort", new Identifier(IdentifierType.CUSTOM, "assetIdentifier"), AssetKind.INSTANCE));
		aggregator.createAAS(shell);
	}

	@Test
	public void createdAASPayloadIsCorrectlyParsed() {
		MqttAASAggregatorPayloadParser parser = createPayloadParser(listener.lastPayload);
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
	}

	@Test
	public void updatedAASPayloadIsCorrectlyParsed() {
		MqttAASAggregatorPayloadParser parser = createPayloadParser(listener.lastPayload);
		aggregator.updateAAS(shell);
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
	}

	@Test
	public void deletedAASPayloadIsCorrectlyParsed() {
		MqttAASAggregatorPayloadParser parser = createPayloadParser(listener.lastPayload);
		aggregator.deleteAAS(shell.getIdentification());
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
	}

	@Test(expected = ProviderException.class)
	public void povidedWrongPayload() {
		MqttAASAggregatorPayloadParser parser = createPayloadParser("('test')");
		parser.extractShellId();
	}

	private static void startMqttBrokerAndAddListener() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

	}

	private MqttAASAggregatorPayloadParser createPayloadParser(String payload) {
		MqttAASAggregatorPayloadParser parser = new MqttAASAggregatorPayloadParser(payload);
		return parser;
	}

	@AfterClass
	public static void stopMqttBroker() {
		mqttBroker.stopServer();
	}

}