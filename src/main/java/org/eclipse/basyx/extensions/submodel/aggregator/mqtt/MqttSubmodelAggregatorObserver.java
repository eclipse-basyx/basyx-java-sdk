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
import org.eclipse.basyx.submodel.aggregator.observing.ISubmodelAggregatorObserver;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the SubmodelAggregator that triggers MQTT events for different
 * operations on the aggregator.
 *
 * @author fischer, jungjan, fried
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
	public MqttSubmodelAggregatorObserver(String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
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
	public MqttSubmodelAggregatorObserver(MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + client.getServerURI());
	}

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 *
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @param persistence
	 *            custom persistence strategy
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserver(String serverEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(serverEndpoint, clientId, persistence);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 *
	 * @param serverEndpoint
	 *            endpoint of MQTT broker
	 * @param clientId
	 *            unique client identifier
	 * @param user
	 *            MQTT user
	 * @param pw
	 *            MQTT password
	 * @param persistence
	 *            custom persistence strategy
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserver(String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		super(serverEndpoint, clientId, user, pw, persistence);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + serverEndpoint);
	}

	@Override
	public void submodelCreated(String shellId, String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL, MqttSubmodelAggregatorHelper.getCombinedMessage(shellId, submodelId));
	}

	@Override
	public void submodelUpdated(String shellId, String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL, MqttSubmodelAggregatorHelper.getCombinedMessage(shellId, submodelId));
	}

	@Override
	public void submodelDeleted(String shellId, String submodelId) {
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, MqttSubmodelAggregatorHelper.getCombinedMessage(shellId, submodelId));
	}
}
