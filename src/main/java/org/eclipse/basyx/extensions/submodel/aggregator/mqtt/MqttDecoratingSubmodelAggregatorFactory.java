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

public class MqttDecoratingSubmodelAggregatorFactory implements ISubmodelAggregatorFactory {
	private MqttClient mqttClient;

	private ISubmodelAggregatorFactory submodelAggregatorFactory;

	public MqttDecoratingSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory,
			MqttClient mqttClient) {
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.mqttClient = mqttClient;
	}

	@Override
	public ISubmodelAggregator create() {
		return new MqttSubmodelAggregatorFactory(submodelAggregatorFactory.create(), mqttClient).create();
	}

}
