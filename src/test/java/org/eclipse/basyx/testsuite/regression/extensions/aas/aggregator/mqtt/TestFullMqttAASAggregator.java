/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.mqtt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.basyx.aas.aggregator.AASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.AASAPIFactory;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttDecoratingAASAggregatorFactory;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttDecoratingAASAPIFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.testsuite.regression.extensions.aas.MqttBrokerSuite;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.MqttTestListener;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;

public class TestFullMqttAASAggregator extends MqttBrokerSuite {
	protected static Server mqttBroker;
	protected static MqttClient client;
	protected static IAASAggregator aggregator;
	protected static MqttTestListener listener;

	protected static ConnectedAssetAdministrationShellManager manager;
	protected static IIdentifier shellIdentifier = new CustomId("shellId");
	protected static IIdentifier submodelIdentifier = new CustomId("submodelId");

	@BeforeClass
	public static void setUp() throws IOException, MqttSecurityException, MqttException {
		mqttBroker = createAndStartMqttBroker();
		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);

		client = createMqttClient();
		client.connect();

		IAASRegistry aasRegistry = new InMemoryRegistry();

		aggregator = createAggregator(aasRegistry);

		manager = createConnectedAASManager(aasRegistry);
	}

	private static ConnectedAssetAdministrationShellManager createConnectedAASManager(IAASRegistry aasRegistry) {
		return new ConnectedAssetAdministrationShellManager(aasRegistry, new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				return new AASAggregatorProvider(aggregator);
			}
		});
	}

	private static IAASAggregator createAggregator(IAASRegistry aasRegistry) throws MqttException {
		return new MqttDecoratingAASAggregatorFactory(
				new AASAggregatorFactory(new MqttDecoratingAASAPIFactory(new AASAPIFactory(), client), new MqttDecoratingSubmodelAggregatorFactory(new SubmodelAggregatorFactory(new VABSubmodelAPIFactory()), client), aasRegistry), client)
						.create();
	}

	private static MqttClient createMqttClient() throws MqttException {
		return new MqttClient("tcp://localhost:1884", "testClient", new MqttDefaultFilePersistence());
	}

	@Test
	public void shellLifeCycle() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, getURL());

		assertTrue(listenerHasTopic(MqttAASAggregatorHelper.TOPIC_CREATEAAS));
		assertEquals(shell.getIdShort(), manager.retrieveAAS(shellIdentifier).getIdShort());

		manager.deleteAAS(shellIdentifier);

		assertTrue(listenerHasTopic(MqttAASAggregatorHelper.TOPIC_DELETEAAS));

		try {
			manager.retrieveAAS(shellIdentifier);
			fail();
		} catch (ResourceNotFoundException e) {
			// ResourceNotFoundException expected
		}
	}

	@Test
	public void submodelLifeCycle() {
		IIdentifier shellIdentifierForSubmodel = new CustomId("shellSubmodelId");

		AssetAdministrationShell shell = createShell(shellIdentifierForSubmodel.getId(), shellIdentifierForSubmodel);
		manager.createAAS(shell, getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);

		manager.createSubmodel(shellIdentifierForSubmodel, submodel);

		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL));
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_ADDSUBMODEL));
		assertEquals(submodel.getIdShort(), manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier).getIdShort());

		manager.deleteSubmodel(shellIdentifierForSubmodel, submodelIdentifier);
		assertTrue(listenerHasTopic(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL));
		assertTrue(listenerHasTopic(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL));
		try {
			manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier);
			fail();
		} catch (ResourceNotFoundException e) {
			// ResourceNotFoundException expected
		}
		manager.deleteAAS(shellIdentifierForSubmodel);
	}

	private String getURL() {
		return "";
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.removeInterceptHandler(listener);
		mqttBroker.stopServer();
	}

	protected AssetAdministrationShell createShell(String idShort, IIdentifier identifier) {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(identifier);
		shell.setIdShort(idShort);
		return shell;
	}

	protected Submodel createSubmodel(String idShort, IIdentifier submodelIdentifier) {
		Submodel submodel = new Submodel();
		submodel.setIdentification(submodelIdentifier);
		submodel.setIdShort(idShort);
		return submodel;
	}

	private boolean listenerHasTopic(String expectedTopic) {
		return listener.getTopics().stream().anyMatch(t -> t.equals(expectedTopic));
	}
}
