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

import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttRegistryService;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Tests events emitting with the MqttAASRegistryService
 *
 * @author haque
 *
 */
public class TestMqttRegistryService extends TestMqttRegistrySuit {

	@Override
	protected IRegistry getAPI() throws MqttException {
		// Create underlying registry service
		IRegistry registryService = new InMemoryRegistry();
		return new MqttRegistryService(registryService, "tcp://localhost:1884", "testClient");
	}

}
