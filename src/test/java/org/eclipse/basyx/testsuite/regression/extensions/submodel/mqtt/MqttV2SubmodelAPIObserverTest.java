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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.extensions.shared.encoding.Base64URLEncoder;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttV2DecoratingSubmodelAPIFactory;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttV2SubmodelAPITopicFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPIV2Helper;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
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
 * @author espen, conradi, danish, siebert
 *
 */
public class MqttV2SubmodelAPIObserverTest {
	private static final String CLIENT_ID = "testClient";
	private static final String SERVER_URI = "tcp://localhost:1884";
	private static final String AASID = "testaasid";
	private static final String SUBMODELID = "testsubmodelid";
	private static final String AASREPOID = "aas-server";

	private static Server mqttBroker;
	private static ISubmodelAPI observableAPI;
	private MqttTestListener listener;

	private static Submodel submodel;

	private static MqttV2SubmodelAPITopicFactory payloadFactory = new MqttV2SubmodelAPITopicFactory(new Base64URLEncoder());

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
		submodel = new Submodel(SUBMODELID, new Identifier(IdentifierType.CUSTOM, SUBMODELID));
		Reference parentRef = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AASID, IdentifierType.IRDI));
		submodel.setParent(parentRef);

		observableAPI = createObservableSubmodelAPI();
	}

	private static ISubmodelAPI createObservableSubmodelAPI() throws MqttException {
		return new MqttV2DecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory(), new MqttClient(SERVER_URI, CLIENT_ID, new MqttDefaultFilePersistence()), AASREPOID, payloadFactory).getSubmodelAPI(submodel);
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

		assertEquals(setValueNull(prop), deserializePayload(listener.lastPayload));
		assertEquals(payloadFactory.createCreateSubmodelElementTopic(AASID, SUBMODELID, elemIdShort, AASREPOID), listener.lastTopic);
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

		assertEquals(setValueNull(prop), deserializePayload(listener.lastPayload));
		assertEquals(payloadFactory.createCreateSubmodelElementTopic(AASID, SUBMODELID, idShortPath, AASREPOID), listener.lastTopic);
	}

	@Test
	public void testSetSubmodelElementValue() {
		String elemIdShort = "testAddProp";
		Property prop = new Property(elemIdShort, "");
		observableAPI.addSubmodelElement(prop);

		String expected = "testVal";

		observableAPI.updateSubmodelElement(elemIdShort, expected);

		assertEquals(expected, deserializePayload(listener.lastPayload));
		assertEquals(payloadFactory.createSubmodelElementValueTopic(AASID, SUBMODELID, elemIdShort, AASREPOID), listener.lastTopic);
	}

	@Test
	public void testDeleteSubmodelElement() {
		String idShortPath = "/testDeleteProp";
		Property prop = new Property(true);
		prop.setIdShort("testDeleteProp");
		observableAPI.addSubmodelElement(prop);
		observableAPI.deleteSubmodelElement(idShortPath);

		assertEquals(setValueNull(prop), deserializePayload(listener.lastPayload));
		assertEquals(payloadFactory.createDeleteSubmodelElementTopic(AASID, SUBMODELID, idShortPath, AASREPOID), listener.lastTopic);
	}

	@Test
	public void testUpdateSubmodelElement() {
		String idShortPath = "testUpdateProp";
		Property prop = new Property(true);
		prop.setIdShort(idShortPath);

		observableAPI.addSubmodelElement(prop);

		// There is no explicit replace function, thus the element has to be re-added
		observableAPI.addSubmodelElement(prop);

		assertEquals(setValueNull(prop), deserializePayload(listener.lastPayload));
		assertEquals(payloadFactory.createUpdateSubmodelElementTopic(AASID, SUBMODELID, idShortPath, AASREPOID), listener.lastTopic);
	}

	@Test
	public void updateSubmodelElementWithEnabledEmptyValueEventQualifier() {
		boolean emptyValueShouldBeSend = true;
		String propIdShort = "hugeValue";

		triggerUpdateEventOnPropertyWithEmptyValueEventQualifier(propIdShort, emptyValueShouldBeSend);

		assertEquals("", listener.lastPayload);
		assertEquals(payloadFactory.createSubmodelElementValueTopic(AASID, SUBMODELID, propIdShort, AASREPOID), listener.lastTopic);
	}

	@Test
	public void updateSubmodelElementWithDisabledEmptyValueEventQualifier() {
		boolean emptyValueShouldBeSend = false;
		String propIdShort = "hugeValue";

		triggerUpdateEventOnPropertyWithEmptyValueEventQualifier(propIdShort, emptyValueShouldBeSend);

		assertEquals("\"aHugeValue\"", listener.lastPayload);
		assertEquals(payloadFactory.createSubmodelElementValueTopic(AASID, SUBMODELID, propIdShort, AASREPOID), listener.lastTopic);
	}

	private void triggerUpdateEventOnPropertyWithEmptyValueEventQualifier(String propIdShort, boolean emptyValueShouldBeSend) {
		Property prop = new Property(propIdShort, "123");
		prop.setQualifiers(createEmptyValueEventQualifier(emptyValueShouldBeSend));

		observableAPI.addSubmodelElement(prop);
		observableAPI.updateSubmodelElement(prop.getIdShort(), "aHugeValue");
	}

	private Collection<IConstraint> createEmptyValueEventQualifier(boolean shouldIgnore) {
		IQualifier qualifier;

		if (shouldIgnore) {
			qualifier = ObservableSubmodelAPIV2Helper.createEmptyValueEventEnabledQualifier();
		} else {
			qualifier = ObservableSubmodelAPIV2Helper.createEmptyValueEventDisabledQualifier();
		}

		return Collections.singleton(qualifier);
	}

	private Object deserializePayload(String payload) {
		GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);

		return tools.deserialize(payload);
	}

	private SubmodelElement setValueNull(SubmodelElement submodelElement) {
		submodelElement.setValue(null);

		return submodelElement;
	}
}
