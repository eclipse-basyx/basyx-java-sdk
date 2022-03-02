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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.restapi.AASAPIFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIObserver;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttDecoratingAASAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.aas.MqttBrokerSuite;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
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
	private static final String SHELL_ID = "shellId";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);

	private static final String ASSET_ID = "assetId";
	private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
	private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);

	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static AssetAdministrationShell shell;
	private static Submodel submodel;

	private static Server mqttBroker;
	private static IAASAPI eventAPI;
	private static MqttTestListener listener;
	private static MqttClient client;

	/**
	 * Sets up the MQTT broker and submodelAPI for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = createAndStartMqttBroker();
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);

		eventAPI = createObservableAASAPI();
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);

		eventAPI.addSubmodel(submodel.getReference());
	}

	private static IAASAPI createObservableAASAPI() throws MqttException {
		return new MqttDecoratingAASAPIFactory(new AASAPIFactory(), client).getAASApi(shell);
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	@Test
	public void submodelCreationEventSent() throws InterruptedException {
		String submodelId = "newSubmodelId";
		Identifier submodelIdentifier = new Identifier(IdentifierType.IRI, submodelId);
		Submodel submodel = new Submodel(submodelId, submodelIdentifier);
		eventAPI.addSubmodel(submodel.getReference());

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELL_ID, submodelId), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_ADDSUBMODEL));
	}

	@Test
	public void submodelDeletionEventSent() {
		eventAPI.removeSubmodel(SUBMODEL_ID);

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELL_ID, SUBMODEL_ID), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL));
	}

	private boolean listenerHasTopic(String expectedTopic) {
		return listener.getTopics().stream().anyMatch(t -> t.equals(expectedTopic));
	}
}
