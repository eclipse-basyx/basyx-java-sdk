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

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorObserver;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.observing.ObservableSubmodelAggregator;
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
 * Tests events emitting with the MqttSubmodelAggregatorObserver
 *
 * @author fischer, jungjan
 *
 */
public class TestMqttSubmodelAggregatorObserver {
	protected Submodel submodel;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_IDSHORT);

	private static Server mqttBroker;
	private static ObservableSubmodelAggregator observedSubmodelAggregator;
	private static MqttSubmodelAggregatorObserver mqttSubmodelAggregatorObserver;
	private MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and ObservableSubmodelAggregator for tests
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
		observedSubmodelAggregator = new ObservableSubmodelAggregator(submodelAggregator);

		// Create mqtt as an observer
		mqttSubmodelAggregatorObserver = new MqttSubmodelAggregatorObserver("tcp://localhost:1884", "testClient");
		observedSubmodelAggregator.addObserver(mqttSubmodelAggregatorObserver);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		observedSubmodelAggregator.createSubmodel(submodel);
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
		observedSubmodelAggregator.createSubmodel(newSubmodel);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, newSubmodelId), listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testUpdateSubmodel() {
		submodel.setCategory("newCategory");
		observedSubmodelAggregator.updateSubmodel(submodel);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_IDSHORT), listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelByIdentifier() {
		observedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_IDSHORT), listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelByIdShort() {
		observedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);

		assertEquals(MqttSubmodelAggregatorHelper.getCombinedMessage(null, SUBMODEL_IDSHORT), listener.lastPayload);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
	}
}
