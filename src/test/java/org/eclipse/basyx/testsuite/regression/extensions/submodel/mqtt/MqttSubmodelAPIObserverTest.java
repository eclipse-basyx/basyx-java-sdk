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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.mqtt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIHelper;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
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
 * Test for MqttSubmodelAPIObserver
 * 
 * @author espen, conradi
 *
 */
public class MqttSubmodelAPIObserverTest {
	private static final String AASID = "testaasid";
	private static final String SUBMODELID = "testsubmodelid";

	private static Server mqttBroker;
	private static ObservableSubmodelAPI observableAPI;
	private MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and submodelAPI for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		// Create submodel
		Submodel sm = new Submodel(SUBMODELID, new Identifier(IdentifierType.CUSTOM, SUBMODELID));
		Reference parentRef = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AASID, IdentifierType.IRDI));
		sm.setParent(parentRef);

		VABSubmodelAPI vabAPI = new VABSubmodelAPI(new VABMapProvider(sm));
		observableAPI = new ObservableSubmodelAPI(vabAPI);
		new MqttSubmodelAPIObserver(new MqttClient("tcp://localhost:1884", "testClient", new MqttDefaultFilePersistence()), MqttSubmodelAPIHelper.getAASId(observableAPI) ,MqttSubmodelAPIHelper.getSubmodelId(observableAPI), observableAPI);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}

	@Before
	public void setUp() {
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
	}

	@After
	public void tearDown() {
		mqttBroker.removeInterceptHandler(listener);
	}

	@Test
	public void testAddSubmodelElement() throws InterruptedException {
		String elemIdShort = "testAddProp";
		Property prop = new Property(true);
		prop.setIdShort(elemIdShort);
		observableAPI.addSubmodelElement(prop);

		assertEquals(MqttSubmodelAPIObserver.getCombinedMessage(AASID, SUBMODELID, elemIdShort), listener.lastPayload);
		assertEquals(MqttSubmodelAPIHelper.TOPIC_ADDELEMENT, listener.lastTopic);
	}

	@Test
	public void testAddNestedSubmodelElement() {
		String idShortPath = "/testColl/testAddProp/";
		SubmodelElementCollection coll = new SubmodelElementCollection();
		coll.setIdShort("testColl");
		observableAPI.addSubmodelElement(coll);

		Property prop = new Property(true);
		prop.setIdShort("testAddProp");
		observableAPI.addSubmodelElement(idShortPath, prop);

		assertEquals(MqttSubmodelAPIObserver.getCombinedMessage(AASID, SUBMODELID, idShortPath), listener.lastPayload);
		assertEquals(MqttSubmodelAPIHelper.TOPIC_ADDELEMENT, listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelElement() {
		String idShortPath = "/testDeleteProp";
		Property prop = new Property(true);
		prop.setIdShort("testDeleteProp");
		observableAPI.addSubmodelElement(prop);
		observableAPI.deleteSubmodelElement(idShortPath);

		assertEquals(MqttSubmodelAPIObserver.getCombinedMessage(AASID, SUBMODELID, idShortPath), listener.lastPayload);
		assertEquals(MqttSubmodelAPIHelper.TOPIC_DELETEELEMENT, listener.lastTopic);
	}

	@Test
	public void testUpdateSubmodelElement() {
		String idShortPath = "testUpdateProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);
		observableAPI.addSubmodelElement(prop);
		observableAPI.updateSubmodelElement(idShortPath, false);

		assertFalse((boolean) observableAPI.getSubmodelElementValue(idShortPath));
		assertEquals(MqttSubmodelAPIObserver.getCombinedMessage(AASID, SUBMODELID, idShortPath), listener.lastPayload);
		assertEquals(MqttSubmodelAPIHelper.TOPIC_UPDATEELEMENT, listener.lastTopic);
	}
}
