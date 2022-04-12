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
