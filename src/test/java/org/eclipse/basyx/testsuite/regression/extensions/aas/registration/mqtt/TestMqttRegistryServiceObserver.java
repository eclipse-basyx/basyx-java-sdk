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

import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttRegistryServiceObserver;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.basyx.registry.observing.ObservableRegistryService;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Tests events emitting with the MqttAASRegistryServiceObserver
 *
 * @author haque
 *
 */
public class TestMqttRegistryServiceObserver extends TestMqttRegistrySuit {
	private static ObservableRegistryService observedAPI;
	private static MqttRegistryServiceObserver mqttObserver;

	@Override
	protected IRegistry getAPI() throws MqttException {
		IRegistry registryService = new InMemoryRegistry();
		observedAPI = new ObservableRegistryService(registryService);

		mqttObserver = new MqttRegistryServiceObserver("tcp://localhost:1884", "testClient");
		observedAPI.addObserver(mqttObserver);

		return observedAPI;
	}
}
