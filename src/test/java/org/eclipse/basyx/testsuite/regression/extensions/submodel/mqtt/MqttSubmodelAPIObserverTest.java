/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		new MqttSubmodelAPIObserver(observableAPI, "tcp://localhost:1884", "testClient");
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
