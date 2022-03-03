/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.mqtt;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.observing.ObservableAASAPI;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Factory decorating AASAPI with MQTT events by wrapping an IAASAPIFactory
 * 
 * @author fried
 */
public class MqttDecoratingAASAPIFactory implements IAASAPIFactory {
	private IAASAPIFactory apiFactory;
	private MqttClient client;

	public MqttDecoratingAASAPIFactory(IAASAPIFactory factoryToBeDecorated, MqttClient client) throws MqttException {
		this.apiFactory = factoryToBeDecorated;
		this.client = client;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		try {
			ObservableAASAPI observedAPI = new ObservableAASAPI(apiFactory.getAASApi(aas));
			MqttAASAPIObserver observer = new MqttAASAPIObserver(observedAPI, client);
			return observedAPI;
		} catch (MqttException e) {
			throw new ProviderException(e);
		}
	}
}