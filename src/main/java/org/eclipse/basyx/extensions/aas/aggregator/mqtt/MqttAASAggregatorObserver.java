/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.observing.IAASAggregatorObserver;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the AASAggregator that triggers MQTT events for
 * different operations on the aggregator. 
 * 
 * @author haque
 *
 */
public class MqttAASAggregatorObserver extends MqttEventService implements IAASAggregatorObserver {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAggregatorObserver.class);
	
	/**
	 * Constructor for adding this MQTT extension as an AAS Aggregator Observer
	 *  
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @throws MqttException
	 */
	public MqttAASAggregatorObserver(String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT AAS Aggregator Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Aggregator Observer
	 * 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @param user username for authentication with broker
	 * @param pw password for authentication with broker
	 * @throws MqttException
	 */
	public MqttAASAggregatorObserver(IAASAggregator observedAASAggregator, String serverEndpoint, String clientId, String user, char[] pw)
			throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AAS Aggregator Observer for endpoint " + serverEndpoint);
	}
	
	/**
	 * Constructor for adding this MQTT extension as an AAS Aggregator Observer
	 * 
	 * @param client already configured client
	 * @throws MqttException
	 */
	public MqttAASAggregatorObserver(IAASAggregator observedAASAggregator, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT AAS Aggregator Observer for endpoint " + client.getServerURI());
	}

	@Override
	public void aasCreated(String aasId) {
		sendMqttMessage(MqttAASAggregatorHelper.TOPIC_CREATEAAS, aasId);
	}

	@Override
	public void aasUpdated(String aasId) {
		sendMqttMessage(MqttAASAggregatorHelper.TOPIC_UPDATEAAS, aasId);
	}

	@Override
	public void aasDeleted(String aasId) {
		sendMqttMessage(MqttAASAggregatorHelper.TOPIC_DELETEAAS, aasId);
	}
}
