/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.mqtt;

import static org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIHelper.getAASId;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.mqtt.MqttDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class MqttSubmodelAPIHelper {
	private static final String CLIENT_ID = "testClient";
	private static final String SERVER_URI = "tcp://localhost:1884";
	private static final String SUBMODELID = "testsubmodelid";

	private static ISubmodelAPI observableAPI;

	private static Submodel submodel;
	private static Server mqttBroker;
	private static ObservableSubmodelAPI observedApi;

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

		observableAPI = createObservableSubmodelAPI();
		observedApi = new ObservableSubmodelAPI(observableAPI);
	}

	private static ISubmodelAPI createObservableSubmodelAPI() throws MqttException {
		return new MqttDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory(), new MqttClient(SERVER_URI, CLIENT_ID, new MqttDefaultFilePersistence())).getSubmodelAPI(submodel);
	}

	@Test
	public void getAASIdReturnsNull() {
		assertNull(getAASId(observedApi));
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}
}
