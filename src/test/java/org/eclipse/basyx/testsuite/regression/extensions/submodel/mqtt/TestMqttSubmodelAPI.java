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
import org.junit.BeforeClass;
import org.junit.Test;

import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;

public class TestMqttSubmodelAPI {
	private static Server mqttBroker;

	@BeforeClass
	public static void startMqttBroker() throws IOException {
		mqttBroker = new Server();

		IResourceLoader classpathLoader = new ClasspathResourceLoader();
		final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

		mqttBroker.startServer(classPathConfig);
	}

	@Test
	public void submodelWithEmptyParentReferenceDoesNotCrashServer() throws MqttException {
		Submodel submodelWithEmptyParentReference = createSubmodelWithEmptyParentReference();

		createMqttSubmodelAPI(submodelWithEmptyParentReference);
	}

	private static ISubmodelAPI createMqttSubmodelAPI(Submodel submodel) throws MqttException {
		return new MqttDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory(), new MqttClient("tcp://localhost:1884", "testClient", new MqttDefaultFilePersistence())).getSubmodelAPI(submodel);
	}

	private Submodel createSubmodelWithEmptyParentReference() {
		Submodel sm = new Submodel("testsubmodelid", new Identifier(IdentifierType.CUSTOM, "testsubmodelid"));
		sm.setParent(null);
		return sm;
	}

	@AfterClass
	public static void stopMqttBroker() {
		if (mqttBroker == null)
			return;

		mqttBroker.stopServer();
	}
}
