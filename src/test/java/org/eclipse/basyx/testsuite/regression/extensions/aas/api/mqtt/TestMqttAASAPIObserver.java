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
 * Tests for MqttAASAPIObserver
 * 
 * @author fried
 *
 */
public class TestMqttAASAPIObserver extends MqttBrokerSuite {
	private static final String SHELL_ID = "testAASId";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final Asset SHELL_ASSET = new Asset("shellAsset", new Identifier(IdentifierType.IRI, "shellAsset"), AssetKind.INSTANCE);

	private static final String SUBMODEL_ID = "testSubmodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private static AssetAdministrationShell shell;
	private static Submodel submodel;

	private static Server mqttBroker;
	private static IAASAPI observableAPI;
	private static MqttTestListener listener;
	private static MqttClient client;

	/**
	 * Sets up the MQTT broker and aasAPI for tests
	 */
	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		mqttBroker = createAndStartMqttBroker();
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);

		client = new MqttClient("tcp://localhost:1884", "testClient");
		client.connect();

		observableAPI = createObservableAASAPI();
		observableAPI.addSubmodel(submodel.getReference());
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
		String smIdShort = "testAddProp";
		Identifier smIdentifier = new Identifier(IdentifierType.IRI, smIdShort);
		Submodel sm = new Submodel(smIdShort, smIdentifier);
		observableAPI.addSubmodel(sm.getReference());

		assertEquals(MqttAASAPIObserver.getCombinedMessage(shell.getIdShort(), smIdShort), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_ADDSUBMODEL));
	}

	@Test
	public void submodelDeletionEventSent() {
		String idShort = submodel.getIdShort();
		observableAPI.removeSubmodel(idShort);

		assertEquals(MqttAASAPIObserver.getCombinedMessage(SHELL_ID, idShort), listener.lastPayload);
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL));
	}

	private boolean listenerHasTopic(String expectedTopic) {
		return listener.getTopics().stream().anyMatch(t -> t.equals(expectedTopic));
	}
}
