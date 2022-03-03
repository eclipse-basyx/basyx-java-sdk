/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregator;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Factory decorating AASAggregator with MQTT events by wrapping an
 * IAASAggregatorFactory
 * 
 * @author fried
 */
public class MqttDecoratingAASAggregatorFactory implements IAASAggregatorFactory {
	private IAASAggregatorFactory apiFactory;
	private MqttClient client;

	public MqttDecoratingAASAggregatorFactory(IAASAggregatorFactory factoryToBeDecorated, MqttClient client) throws MqttException {
		this.apiFactory = factoryToBeDecorated;
		this.client = client;
	}

	@Override
	public IAASAggregator create() {
		try {
			IAASAggregator aggregator = apiFactory.create();
			ObservableAASAggregator observedAASAggregator = new ObservableAASAggregator(aggregator);
			MqttAASAggregatorObserver observer = new MqttAASAggregatorObserver(client);
			observedAASAggregator.addObserver(observer);
			return observedAASAggregator;
		} catch (MqttException exception) {
			throw new ProviderException(exception);
		}
	}

}
