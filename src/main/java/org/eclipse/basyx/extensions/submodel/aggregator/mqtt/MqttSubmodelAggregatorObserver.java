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

import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.observing.ISubmodelAggregatorObserver;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the SubmodelAggregator that triggers MQTT events for different
 * operations on the aggregator.
 * 
 * @author fischer, jungjan
 *
 */
public class MqttSubmodelAggregatorObserver extends MqttEventService implements ISubmodelAggregatorObserver {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAggregatorObserver.class);

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 * 
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserver(String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 * 
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @param user
	 *            username for authentication with broker
	 * @param pw
	 *            password for authentication with broker
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserver(ISubmodelAggregator observedSubmodelAggregator, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 * 
	 * @param client
	 *            already configured client
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserver(ISubmodelAggregator observedSubmodelAggregator, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + client.getServerURI());
	}

	@Override
	public void submodelCreated(String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL, submodelId);
	}

	@Override
	public void submodelUpdated(String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL, submodelId);

	}

	@Override
	public void submodelDeleted(String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, submodelId);

	}

}
