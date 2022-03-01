/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.mqtt;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Api provider for constructing a new Submodel API that emits MQTT events
 * 
 * @author fried
 */
public class MqttDecoratingSubmodelAPIFactory implements ISubmodelAPIFactory {
	private ISubmodelAPIFactory apiFactory;
	private MqttClient client;

	public MqttDecoratingSubmodelAPIFactory(ISubmodelAPIFactory factoryToBeDecorated, MqttClient client) throws MqttException {
		this.apiFactory = factoryToBeDecorated;
		this.client = client;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		try {
			return new MqttSubmodelAPI(apiFactory.getSubmodelAPI(submodel), client);
		} catch (MqttException e) {
			throw new ProviderException(e);
		}
	}

}
