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
package org.eclipse.basyx.extensions.aas.api.mqtt;

import org.eclipse.basyx.aas.restapi.observing.IAASAPIObserver;
import org.eclipse.basyx.aas.restapi.observing.ObservableAASAPI;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IAASAPIObserver} Triggers MQTT events for different
 * operations on the AAS.
 * 
 * @author fried
 *
 */
public class MqttAASAPIObserver extends MqttEventService implements IAASAPIObserver {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAPIObserver.class);

	// The underlying AASAPI
	protected ObservableAASAPI observedAPI;

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with a
	 * custom persistence strategy
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String brokerEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		super(brokerEndpoint, clientId, persistence);
		logger.info("Create new MQTT AASAPI for endpoint " + brokerEndpoint);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with
	 * credentials and persistency strategy
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AASAPI for endpoint " + serverEndpoint);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI.
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @param client
	 *            An already connected mqtt client
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, MqttClient client) throws MqttException {
		super(client);
		this.observedAPI = observedAPI;
		observedAPI.addObserver(this);
	}

	@Override
	public void submodelAdded(IReference submodelReference) {
		for (IKey key : submodelReference.getKeys()) {
			if (key.getType().name().equalsIgnoreCase("Submodel")) {
				String id = key.getValue();
				sendMqttMessage(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, getCombinedMessage(observedAPI.getAAS().getIdShort(), id));
			}
		}
	}

	@Override
	public void submodelRemoved(String id) {
		sendMqttMessage(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL, getCombinedMessage(observedAPI.getAAS().getIdShort(), id));
	}

	public static String getCombinedMessage(String aasId, String idShort) {
		return "(" + aasId + "," + idShort + ")";
	}

}
