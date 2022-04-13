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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.aggregator.mqtt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
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
 * Tests events emitting with the MqttSubmodelAggregator
 *
 * @author fischer, jungjan, fried
 *
 */
public class TestMqttSubmodelAggregator {
	protected Submodel submodel;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static Server mqttBroker;
	private static ISubmodelAggregator mqttSubmodelAggregator;
	private static MqttTestListener listener;

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

		MqttClient client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
		mqttSubmodelAggregator = new MqttDecoratingSubmodelAggregatorFactory(new SubmodelAggregatorFactory(), client).create();
	}

	@Before
	public void setUp() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		mqttSubmodelAggregator.createSubmodel(submodel);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void testCreateSubmodel() {
		String newSubmodelIdShort = "newSubmodelIdShort";
		String newSubmodelId = "newSubmodelId";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRDI, newSubmodelId);
		Submodel newSubmodel = new Submodel(newSubmodelIdShort, newSubmodelIdentifier);
		mqttSubmodelAggregator.createSubmodel(newSubmodel);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, newSubmodelId), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL));
	}

	@Test
	public void testUpdateSubmodel() {
		submodel.setCategory("newCategory");
		mqttSubmodelAggregator.updateSubmodel(submodel);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_ID), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL));
	}

	@Test
	public void testDeleteSubmodelByIdentifier() {
		mqttSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_ID), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL));
	}

	@Test
	public void testDeleteSubmodelByIdShort() {
		mqttSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_ID), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL));
	}

	private boolean listenerHasTopic(String expectedTopic) {
		return listener.getTopics().stream().anyMatch(t -> t.equals(expectedTopic));
	}
}
