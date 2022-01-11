/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.aggregator.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregator;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
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
 * Tests events emitting with the MqttSubmodelAggregator
 *
 * @author fischer, jungjan
 *
 */
public class TestMqttSubmodelAggregator {
	protected Submodel submodel;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static Server mqttBroker;
	private static MqttSubmodelAggregator mqttSubmodelAggregator;
	private MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and SubmodelAggregator for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		// Create underlying submodel aggregator
		ISubmodelAggregator submodelAggregator = new SubmodelAggregator();

		mqttSubmodelAggregator = new MqttSubmodelAggregator(submodelAggregator, "tcp://localhost:1884", "testClient");
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		mqttSubmodelAggregator.createSubmodel(submodel);

		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	@After
	public void tearDown() {
		mqttBroker.removeInterceptHandler(listener);
	}

	@Test
	public void testCreateSubmodel() {
		String newSubmodelIdShort = "newSubmodelIdShort";
		String newSubmodelId = "newSubmodelId";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRDI, newSubmodelId);
		Submodel newSubmodel = new Submodel(newSubmodelIdShort, newSubmodelIdentifier);
		mqttSubmodelAggregator.createSubmodel(newSubmodel);

		assertEquals(newSubmodelId, listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testUpdateSubmodel() {
		submodel.setCategory("newCategory");
		mqttSubmodelAggregator.updateSubmodel(submodel);

		assertEquals(SUBMODEL_ID, listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelByIdentifier() {
		mqttSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);

		assertEquals(SUBMODEL_ID, listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelByIdShort() {
		mqttSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);

		assertEquals(SUBMODEL_ID, listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}
}
