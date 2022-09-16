/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryService;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServicePayloadParser;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
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
 * Tests the Payload Parser for Payloads sent by MqttAASRegistryService
 * 
 * @author fried
 *
 */
public class TestMqttAASRegistryServicePayloadParser {
	private static Server mqttBroker;
	private static MqttTestListener listener;
	private static MqttAASRegistryService registryService;

	private AssetAdministrationShell shell;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		startMqttBrokerAndAddListener();

		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		registryService = new MqttAASRegistryService(new InMemoryRegistry(), client);
	}

	@Before
	public void setUp() {
		shell = createShell();
		AASDescriptor shellDescriptor = createAASDescriptor(shell);
		registryService.register(shellDescriptor);
	}

	@Test
	public void registeredAASPayloadIsCorrectlyParsed() {
		MqttAASRegistryServicePayloadParser parser = createPayloadParser(listener.lastPayload);

		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
	}

	@Test
	public void registeredSubmodelPayloadIsCorrectlyParsed() {
		Submodel submodel = createSubmodel();
		registerSubmodel(submodel);

		MqttAASRegistryServicePayloadParser parser = createPayloadParser(listener.lastPayload);
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
	}

	@Test
	public void deletedAASPayloadIsCorrectlyParsed() {
		registryService.delete(shell.getIdentification());

		MqttAASRegistryServicePayloadParser parser = createPayloadParser(listener.lastPayload);
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
	}

	@Test
	public void deletedSubmodelPayloadIsCorrectlyParsed() {
		Submodel submodel = createSubmodel();
		registerSubmodel(submodel);

		registryService.delete(shell.getIdentification(), submodel.getIdentification());
		MqttAASRegistryServicePayloadParser parser = createPayloadParser(listener.lastPayload);
		assertEquals(parser.extractShellId(), shell.getIdentification().getId());
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
	}

	@Test(expected = ProviderException.class)
	public void extractedSubmodelIdWithWrongTopicThrowsException() {
		MqttAASRegistryServicePayloadParser parser = createPayloadParser(listener.lastPayload);
		parser.extractSubmodelId();
	}

	private void registerSubmodel(Submodel submodel) {
		SubmodelDescriptor smDescriptor = createSubmodelDescriptor(submodel);

		registryService.register(shell.getIdentification(), smDescriptor);
	}

	private Submodel createSubmodel() {
		return new Submodel("smIdShort", new Identifier(IdentifierType.CUSTOM, "smIdentifier"));
	}

	private SubmodelDescriptor createSubmodelDescriptor(Submodel submodel) {
		return new SubmodelDescriptor(submodel, "http://example.com/submodelServer");
	}

	private AssetAdministrationShell createShell() {
		return new AssetAdministrationShell("shellIdShort", new Identifier(IdentifierType.CUSTOM, "shellIdentifier"), new Asset("assetIdShort", new Identifier(IdentifierType.CUSTOM, "assetIdentifier"), AssetKind.INSTANCE));
	}

	private AASDescriptor createAASDescriptor(AssetAdministrationShell shell) {

		return new AASDescriptor(shell, "http://example.com/shellServer");
	}

	private MqttAASRegistryServicePayloadParser createPayloadParser(String payload) {
		return new MqttAASRegistryServicePayloadParser(payload);
	}

	private static void startMqttBrokerAndAddListener() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

	}

	@AfterClass
	public static void stopMqttBroker() {
		mqttBroker.stopServer();
	}
}
