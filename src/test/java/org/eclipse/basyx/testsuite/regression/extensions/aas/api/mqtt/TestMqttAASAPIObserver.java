/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.api.mqtt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.restapi.observing.ObservableAASAPI;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
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
 * Tests for MqttAASAPIObserver
 * 
 * @author fried
 *
 */
public class TestMqttAASAPIObserver {
	private static final String SHELLID = "testaasid";
	private static final Identifier SHELLIDENTIFIER = new Identifier(IdentifierType.IRI, SHELLID);
	private static final Asset SHELLASSET = new Asset("shellAsset", new Identifier(IdentifierType.IRI, "shellAsset"), AssetKind.INSTANCE);

	private static final String SUBMODELID = "testsubmodelid";
	private static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);

	private static AssetAdministrationShell shell;
	private static Submodel submodel;

	private static Server mqttBroker;
	private static ObservableAASAPI observableAPI;
	private MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and aasAPI for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		// Start MQTT broker
		mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		shell = new AssetAdministrationShell(SHELLID, SHELLIDENTIFIER, SHELLASSET);
		submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);

		VABAASAPI vabAPI = new VABAASAPI(new VABMapProvider(shell));
		observableAPI = new ObservableAASAPI(vabAPI);
		new MqttAASAPIObserver(observableAPI, "tcp://localhost:1884", "testClient");

		observableAPI.addSubmodel(submodel.getReference());
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
	public void submodelCreationEventSent() throws InterruptedException {
		String smIdShort = "testAddProp";
		Identifier smIdentifier = new Identifier(IdentifierType.IRI, smIdShort);
		Submodel sm = new Submodel(smIdShort, smIdentifier);
		observableAPI.addSubmodel(sm.getReference());

		assertEquals(MqttAASAPIObserver.getCombinedMessage(shell.getIdShort(), smIdShort), listener.lastPayload);
		assertEquals(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, listener.lastTopic);
	}

	@Test
	public void submodelDeletionEventSent() {
		String idShort = submodel.getIdShort();
		observableAPI.removeSubmodel(idShort);

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELLID, idShort), listener.lastPayload);
		assertEquals(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL, listener.lastTopic);
	}
}
