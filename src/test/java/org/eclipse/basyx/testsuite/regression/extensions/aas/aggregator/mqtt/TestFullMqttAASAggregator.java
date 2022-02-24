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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.AASAPIFactory;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregator;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIFactory;
import org.eclipse.basyx.extensions.aas.api.mqtt.MqttAASAPIHelper;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.aggregator.mqtt.MqttSubmodelAggregatorHelper;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
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
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class TestFullMqttAASAggregator {
	protected static Server mqttBroker;
	protected static MqttClient client;
	protected static IAASAggregator aggregator;
	protected static MqttTestListener listener;

	protected static IAASRegistry aasRegistry;
	protected static ConnectedAssetAdministrationShellManager manager;
	protected static IIdentifier shellIdentifier = new CustomId("shellId");
	protected static IIdentifier submodelIdentifier = new CustomId("submodelId");

	@BeforeClass
	public static void setUp() throws IOException, MqttSecurityException, MqttException {
		mqttBroker = createAndStartMqttBroker();
		client = createMqttClient();
		client.connect();

		aggregator = new MqttAASAggregator(new AASAggregator(new MqttAASAPIFactory(new AASAPIFactory(), client), new MqttDecoratingSubmodelAggregatorFactory(new SubmodelAggregatorFactory(), client)), client);

		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();

		// Create ConnectedAASManager
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, new IConnectorFactory() {

			@Override
			public IModelProvider getConnector(String addr) {
				return new AASAggregatorProvider(aggregator);
			}
		});
	}

	private static MqttClient createMqttClient() throws MqttException {
		return new MqttClient("tcp://localhost:1884", "testClient", new MqttDefaultFilePersistence());
	}

	protected static Server createAndStartMqttBroker() throws IOException {
		Server mqttBroker = new Server();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		mqttBroker.startServer(classPathConfig);

		listener = new MqttTestListener();
		mqttBroker.addInterceptHandler(listener);
		return mqttBroker;
	}

	@Test
	public void shellLifeCycle() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, getURL());

		assertEquals(MqttAASAggregatorHelper.TOPIC_CREATEAAS, listener.lastTopic);
		assertEquals(shell.getIdShort(), manager.retrieveAAS(shellIdentifier).getIdShort());

		manager.deleteAAS(shellIdentifier);

		assertEquals(MqttAASAggregatorHelper.TOPIC_DELETEAAS, listener.lastTopic);

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

		assertEquals(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, listener.lastTopic);
		assertEquals(submodel.getIdShort(), manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier).getIdShort());

		manager.deleteSubmodel(shellIdentifierForSubmodel, submodelIdentifier);
		assertEquals(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, listener.lastTopic);
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
}
