/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregatorV2;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Factory decorating AASAggregator with MQTT events by wrapping an
 * IAASAggregatorFactory
 * 
 * @author fried, siebert
 */
public class MqttV2DecoratingAASAggregatorFactory implements IAASAggregatorFactory {
	private IAASAggregatorFactory apiFactory;
	private MqttClient client;
	private String aasServerId;

	public MqttV2DecoratingAASAggregatorFactory(IAASAggregatorFactory factoryToBeDecorated, MqttClient client, String aasServerId) {
		this.apiFactory = factoryToBeDecorated;
		this.client = client;
		this.aasServerId = aasServerId;
	}

	@Override
	public IAASAggregator create() {
		try {
			IAASAggregator aggregator = apiFactory.create();
			ObservableAASAggregatorV2 observedAASAggregator = new ObservableAASAggregatorV2(aggregator, this.aasServerId);
			MqttV2AASAggregatorObserver observer = new MqttV2AASAggregatorObserver(client);
			observedAASAggregator.addObserver(observer);
			return observedAASAggregator;
		} catch (MqttException exception) {
			throw new ProviderException(exception);
		}
	}

}
