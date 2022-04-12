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
package org.eclipse.basyx.extensions.shared.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of common parts of MQTT event propagation services.
 * Extend this class to make a service MQTT extendable
 *  
 * @author haque
 *
 */
public class MqttEventService {
	private static Logger logger = LoggerFactory.getLogger(MqttEventService.class);

	// The MQTTClient
	protected MqttClient mqttClient;

	// QoS for MQTT messages (1, 2 or 3).
	protected int qos = 1;
	
	/**
	 * Constructor for creating an MqttClient (no authentication)
	 * @param serverEndpoint
	 * @param clientId
	 * @throws MqttException
	 */
	public MqttEventService(String serverEndpoint, String clientId) throws MqttException {
		this(serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}
	
	/**
	 * Constructor for creating an MqttClient (with no authentication and a custom
	 * persistence strategy)
	 */
	public MqttEventService(String serverEndpoint, String clientId, MqttClientPersistence mqttPersistence) throws MqttException {
		this.mqttClient = new MqttClient(serverEndpoint, clientId, mqttPersistence);
		mqttClient.connect();
	}

	/**
	 * Constructor for creating an MqttClient with authentication and a custom
	 * persistence strategy
	 */
	public MqttEventService(String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence mqttPersistence) throws MqttException {
		this.mqttClient = new MqttClient(serverEndpoint, clientId, mqttPersistence);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(user);
		options.setPassword(pw);
		mqttClient.connect(options);
	}

	/**
	 * Constructor for creating an MqttClient with authentication
	 * 
	 * @param serverEndpoint
	 * @param clientId
	 * @param user
	 * @param pw
	 * @throws MqttException
	 */
	public MqttEventService(String serverEndpoint, String clientId, String user, char[] pw)
			throws MqttException {
		this(serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}
	
	/**
	 * Constructor for creating an MqttClient with existing client
	 * 
	 * @param client
	 * @throws MqttException
	 */
	public MqttEventService(MqttClient client) throws MqttException {
		this.mqttClient = client;
	}

	/**
	 * Sets the QoS for MQTT messages
	 * 
	 * @param qos
	 */
	public void setQoS(int qos) {
		if (qos >= 0 && qos <= 3) {
			this.qos = qos;
		} else {
			throw new IllegalArgumentException("Invalid QoS: " + qos);
		}
	}

	/**
	 * Gets the QoS for MQTT messages
	 */
	public int getQoS() {
		return this.qos;
	}
	
	/**
	 * Sends MQTT message to connected broker
	 * @param topic in which the message will be published
	 * @param payload the actual message
	 */
	protected void sendMqttMessage(String topic, String payload) {
		MqttMessage msg = new MqttMessage(payload.getBytes());
		if (this.qos != 1) {
			msg.setQos(this.qos);
		}
		try {
			logger.debug("Send MQTT message to " + topic + ": " + payload);
			mqttClient.publish(topic, msg);
		} catch (MqttPersistenceException e) {
			logger.error("Could not persist mqtt message", e);
		} catch (MqttException e) {
			logger.error("Could not send mqtt message", e);
		}
	}
}
