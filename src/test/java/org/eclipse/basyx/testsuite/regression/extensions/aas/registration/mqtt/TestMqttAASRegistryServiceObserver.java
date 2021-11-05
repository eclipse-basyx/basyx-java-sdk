/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.mqtt;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryService;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServiceObserver;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Tests events emitting with the MqttAASRegistryServiceObserver
 *
 * @author haque
 *
 */
public class TestMqttAASRegistryServiceObserver extends TestMqttAASRegistrySuit {
	private static ObservableAASRegistryService observedAPI;
	private static MqttAASRegistryServiceObserver mqttObserver;

	@Override
	protected IAASRegistry getAPI() throws MqttException {
		IAASRegistry registryService = new InMemoryRegistry();
		observedAPI = new ObservableAASRegistryService(registryService);

		mqttObserver = new MqttAASRegistryServiceObserver("tcp://localhost:1884", "testClient");
		observedAPI.addObserver(mqttObserver);

		return observedAPI;
	}
}
