/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.extensions.submodel.aggregator.mqtt;

import java.security.ProviderException;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.observing.ObservableSubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Factory decorating SubmodelAggregator with MQTT events by wrapping an
 * ISubmodelAggregatorFactory
 * 
 * @author fried
 */
public class MqttDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	private ISubmodelAggregatorFactory submodelAggregatorFactory;
	private MqttClient mqttClient;

	private ObservableSubmodelAggregator observedSubmodelAggregator;
	protected MqttSubmodelAggregatorObserver observer;

	public MqttDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory, MqttClient mqttClient) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.mqttClient = mqttClient;
	}

	@Override
	public ISubmodelAggregator create() {
		try {
			ISubmodelAggregator aggregator = submodelAggregatorFactory.create();
			observedSubmodelAggregator = new ObservableSubmodelAggregator(aggregator);
			observer = new MqttSubmodelAggregatorObserver(mqttClient);
			observedSubmodelAggregator.addObserver(observer);
			return observedSubmodelAggregator;
		} catch (MqttException e) {
			throw new ProviderException(e);
		}
	}
	@Override
	public ISubmodelAggregator create(IIdentifier aasIdentifier) {
		try {
			ISubmodelAggregator aggregator = submodelAggregatorFactory.create(aasIdentifier);
			observedSubmodelAggregator = new ObservableSubmodelAggregator(aggregator);
			observer = new MqttSubmodelAggregatorObserver(mqttClient);
			observedSubmodelAggregator.addObserver(observer);
			return observedSubmodelAggregator;
		} catch (MqttException e) {
			throw new ProviderException(e);
		}
	}

}
