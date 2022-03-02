/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.extensions.submodel.aggregator.mqtt;

import java.security.ProviderException;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.observing.ObservableSubmodelAggregator;
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

	public MqttDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory,
			MqttClient mqttClient) {
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

}
