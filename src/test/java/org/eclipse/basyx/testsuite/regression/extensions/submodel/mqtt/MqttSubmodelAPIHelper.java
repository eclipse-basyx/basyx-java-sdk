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

import java.io.IOException;

import org.eclipse.basyx.extensions.submodel.mqtt.MqttDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.AfterClass;
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

	private static Submodel submodel;
	private static Server mqttBroker;

	@Test
	public void getAASIdReturnsNull() {
		mqttBroker = startMqttBroker();
		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);
		try {
			mqttBroker.startServer(classPathConfig);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		submodel = createSubmodel();

		try {
			createObservableSubmodelAPI();
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	private static ISubmodelAPI createObservableSubmodelAPI() throws MqttException {
		return new MqttDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory(), new MqttClient(SERVER_URI, CLIENT_ID, new MqttDefaultFilePersistence())).getSubmodelAPI(submodel);
	}

	private Submodel createSubmodel() {
		return new Submodel(SUBMODELID, new Identifier(IdentifierType.CUSTOM, SUBMODELID));
	}

	private Server startMqttBroker() {
		return new Server();
	}

	@AfterClass
	public static void tearDownClass() {
		mqttBroker.stopServer();
	}
}
