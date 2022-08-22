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
import org.eclipse.basyx.submodel.aggregator.observing.ISubmodelAggregatorObserverV2;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
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
public class MqttSubmodelAggregatorObserverV2 extends MqttEventService implements ISubmodelAggregatorObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAggregatorObserverV2.class);

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 *
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @throws MqttException
	 */
	public MqttSubmodelAggregatorObserverV2(String serverEndpoint, String clientId) throws MqttException {
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
	public MqttSubmodelAggregatorObserverV2(String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
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
	public MqttSubmodelAggregatorObserverV2(MqttClient client) throws MqttException {
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
	public MqttSubmodelAggregatorObserverV2(String serverEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
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
	public MqttSubmodelAggregatorObserverV2(String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		super(serverEndpoint, clientId, user, pw, persistence);
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + serverEndpoint);
	}

	@Override
	public void submodelCreated(String shellId, ISubmodel submodel, String repoId) {
		sendMqttMessage(MqttSubmodelAggregatorHelperV2.createCreateSubmodelTopic(shellId, repoId), serializePayload(submodel));
	}

	@Override
	public void submodelUpdated(String shellId, ISubmodel submodel, String repoId) {
		sendMqttMessage(MqttSubmodelAggregatorHelperV2.createUpdateSubmodelTopic(shellId, repoId), serializePayload(submodel));
	}

	@Override
	public void submodelDeleted(String shellId, ISubmodel submodel, String repoId) {
		sendMqttMessage(MqttSubmodelAggregatorHelperV2.createDeleteSubmodelTopic(shellId, repoId), serializePayload(submodel));
	}
	
	private String serializePayload(ISubmodel submodel) {
		GSONTools tools = new GSONTools(new DefaultTypeFactory(), false, false);
		
		return tools.serialize(submodel);
	}
}
