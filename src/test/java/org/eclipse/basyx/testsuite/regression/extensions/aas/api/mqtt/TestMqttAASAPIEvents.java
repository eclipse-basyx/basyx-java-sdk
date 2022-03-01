/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPI;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIObserver;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.aas.MqttBrokerSuite;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;

/**
 * Tests events emitting with the MqttAASAPI
 * 
 * @author fried
 *
 */
public class TestMqttAASAPIEvents extends MqttBrokerSuite {
	private static final String SHELL_ID = "shel_one";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);

	private static final String ASSET_ID = "asset_one";
	private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
	private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);

	private static final String SUBMODEL_ID = "submodel_1";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static AssetAdministrationShell shell;
	private static Submodel submodel;

	private static Server mqttBroker;
	private static MqttAASAPI eventAPI;
	private static MqttTestListener listener;

	/**
	 * Sets up the MQTT broker and submodelAPI for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = createAndStartMqttBroker();
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);

		VABAASAPI vabAPI = new VABAASAPI(new VABMapProvider(shell));
		eventAPI = new MqttAASAPI(vabAPI, "tcp://localhost:1884", "testClient");
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);

		eventAPI.addSubmodel(submodel.getReference());
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void submodelCreationEventSent() throws InterruptedException {
		String submodelId = "submodel_2";
		Identifier submodelIdentifier = new Identifier(IdentifierType.IRI, submodelId);
		Submodel submodel = new Submodel(submodelId, submodelIdentifier);
		eventAPI.addSubmodel(submodel.getReference());

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELL_ID, submodelId), listener.lastPayload);
		assertEquals(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, listener.lastTopic);
	}

	@Test
	public void submodelDeletionEventSent() {
		eventAPI.removeSubmodel(SUBMODEL_ID);

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELL_ID, SUBMODEL_ID), listener.lastPayload);
		assertEquals(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL, listener.lastTopic);
	}

}
