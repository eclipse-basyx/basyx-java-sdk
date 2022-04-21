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
 * @author fried, danish
 *
 */
public class MqttAASAPIObserver extends MqttEventService implements IAASAPIObserver {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAPIObserver.class);
	
	private String aasIdShort;
	
	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param client
	 *            An already connected mqtt client
	 * @param aasIdShort
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(MqttClient client, String aasIdShort, ObservableAASAPI observedAPI) throws MqttException {
		super(client);
		
		connectMqttClientIfRequired();
		
		this.aasIdShort = aasIdShort;
		
		observedAPI.addObserver(this);
	}
	
	/**
	 * Constructor for adding this MQTT extension on top of another aasAPI with
	 * credentials
	 * 
	 * @param clientId
	 * @param aasIdShort 
	 * @param user 
	 * @param password
	 * @param serverEndpoint
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPIObserver(String clientId, String aasIdShort, String user, char[] password, String serverEndpoint, ObservableAASAPI observedAPI) throws MqttException {
		super(serverEndpoint, clientId, user, password);
		
		this.aasIdShort = aasIdShort;
		
		observedAPI.addObserver(this);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 * 
	 * @deprecated This constructor is deprecated please use {@link #MqttAASAPIObserver(MqttClient, String, ObservableAASAPI)} instead.
	 */
	@Deprecated
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with a
	 * custom persistence strategy
	 * 
	 * @deprecated This constructor is deprecated please use {@link #MqttAASAPIObserver(MqttClient, String, ObservableAASAPI)} instead.
	 */
	@Deprecated
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String brokerEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		this(new MqttClient(brokerEndpoint, clientId, persistence), getAASIdShort(observedAPI), observedAPI);
		logger.info("Create new MQTT AASAPI for endpoint " + brokerEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 * 
	 * @deprecated This constructor is deprecated please use {@link #MqttAASAPIObserver(String, String, String, char[], String, ObservableAASAPI)} instead.
	 */
	@Deprecated
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with
	 * credentials and persistency strategy
	 * 
	 * @deprecated This constructor is deprecated please use {@link #MqttAASAPIObserver(String, String, String, char[], String, ObservableAASAPI)} instead.
	 */
	@Deprecated
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		this(clientId, getAASIdShort(observedAPI), user, pw, serverEndpoint, observedAPI);
		logger.info("Create new MQTT AASAPI for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI.
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @param client
	 *            An already connected mqtt client
	 * @throws MqttException
	 * 
	 * @deprecated This constructor is deprecated please use {@link #MqttAASAPIObserver(MqttClient, String, ObservableAASAPI)} instead.
	 */
	@Deprecated
	public MqttAASAPIObserver(ObservableAASAPI observedAPI, MqttClient client) throws MqttException {
		this(client, getAASIdShort(observedAPI), observedAPI);
	}
	
	private void connectMqttClientIfRequired() throws MqttException {
		if(!mqttClient.isConnected()) {
			mqttClient.connect();
		}
	}

	@Override
	public void submodelAdded(IReference submodelReference) {
		for (IKey key : submodelReference.getKeys()) {
			if (key.getType().name().equalsIgnoreCase("Submodel")) {
				String id = key.getValue();
				sendMqttMessage(MqttAASAPIHelper.TOPIC_ADDSUBMODEL, getCombinedMessage(aasIdShort, id));
			}
		}
	}

	@Override
	public void submodelRemoved(String id) {
		sendMqttMessage(MqttAASAPIHelper.TOPIC_REMOVESUBMODEL, getCombinedMessage(aasIdShort, id));
	}

	public static String getCombinedMessage(String aasId, String idShort) {
		return "(" + aasId + "," + idShort + ")";
	}
	
	private static String getAASIdShort(ObservableAASAPI observedAPI) {
		return observedAPI.getAAS().getIdShort();
	}

}
