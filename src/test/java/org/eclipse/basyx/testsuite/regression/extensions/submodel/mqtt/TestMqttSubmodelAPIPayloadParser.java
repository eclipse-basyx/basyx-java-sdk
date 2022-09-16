/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.mqtt.MqttDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIPayloadParser;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
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
 * Tests the Payload Parser for Payloads sent by MqttSubmodelAPI
 * 
 * @author fried
 *
 */
public class TestMqttSubmodelAPIPayloadParser {
	private static Server mqttBroker;

	private static MqttTestListener listener;

	private static Identifier submodelIdentifier = new Identifier(IdentifierType.CUSTOM, "smIdentifier");
	private static String submodelIdShort = "smIdShort";
	private static MqttDecoratingSubmodelAPIFactory smApiFactory;
	private static Submodel submodel;

	private static String submodelElementIdShort = "submodelElementIdShort";
	private static Property element;
	private static Identifier shellIdentifier;

	private ISubmodelAPI eventAPI;

	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		startMqttBrokerAndAddListener();

		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		smApiFactory = new MqttDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory(), client);
		submodel = createTestSubmodel();

	}

	@Before
	public void createEventAPI() {
		eventAPI = smApiFactory.create(submodel);
	}

	@Test
	public void createdSubmodelPayloadIsCorrectlyParsed() throws MqttException {
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
	}

	@Test
	public void addedSubmodelElementPayloadIsCorrectlyParsed() throws MqttException {
		eventAPI.addSubmodelElement(submodel.getSubmodelElement(submodelElementIdShort));

		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);

		assertEquals(parser.extractShellId(), shellIdentifier.getId());
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
		assertEquals(parser.extractSubmodelElementIdShort(), element.getIdShort());
	}

	@Test
	public void removedSubmodelElementPayloadIsCorrectlyParsed() throws MqttException {
		eventAPI.addSubmodelElement(submodel.getSubmodelElement(submodelElementIdShort));
		eventAPI.deleteSubmodelElement(submodelElementIdShort);
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);

		assertEquals(parser.extractShellId(), shellIdentifier.getId());
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
		assertEquals(parser.extractSubmodelElementIdShort(), element.getIdShort());
	}

	@Test
	public void updatedSubmodelElementPayloadIsCorrectlyParsed() throws MqttException {
		eventAPI.addSubmodelElement(submodel.getSubmodelElement(submodelElementIdShort));
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);

		eventAPI.updateSubmodelElement(submodelElementIdShort, "newTestValue");

		assertEquals(parser.extractShellId(), shellIdentifier.getId());
		assertEquals(parser.extractSubmodelId(), submodel.getIdentification().getId());
		assertEquals(parser.extractSubmodelElementIdShort(), element.getIdShort());
	}

	@Test(expected = ProviderException.class)
	public void extractShellIdWithWrongTopicThrowsError() throws MqttException {
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);
		parser.extractShellId();
	}

	@Test(expected = ProviderException.class)
	public void extractSubmodelElementIDWithWrongTopicThrowsError() throws MqttException {
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(listener.lastPayload);
		parser.extractSubmodelElementIdShort();
	}

	private static Submodel createTestSubmodel() throws MqttException {
		Submodel submodel = new Submodel(submodelIdShort, submodelIdentifier);

		shellIdentifier = new Identifier(IdentifierType.CUSTOM, "testValue");
		Reference parentReference = new Reference(shellIdentifier, KeyElements.ACCESSPERMISSIONRULE, false);

		submodel.setParent(parentReference);
		element = new Property(submodelElementIdShort, "propertyValue");
		submodel.addSubmodelElement(element);
		return submodel;
	}

	private static void startMqttBrokerAndAddListener() throws IOException {
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	private MqttSubmodelAPIPayloadParser createPayloadParser(String payload) {
		return new MqttSubmodelAPIPayloadParser(payload);
	}

	@AfterClass
	public static void stopMqttBroker() {
		mqttBroker.stopServer();
	}
}
