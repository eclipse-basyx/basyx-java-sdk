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

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {

	ISubmodelAggregator submodelAggregatorToBeObserved;
	MqttClient client;

	public MqttSubmodelAggregatorFactory(ISubmodelAggregator submodelAggregatorToBeObserved, MqttClient client) {
		this.submodelAggregatorToBeObserved = submodelAggregatorToBeObserved;
		this.client = client;
	}

	@Override
	public ISubmodelAggregator create() {
		try {
			return new MqttSubmodelAggregator(submodelAggregatorToBeObserved, client);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

}
