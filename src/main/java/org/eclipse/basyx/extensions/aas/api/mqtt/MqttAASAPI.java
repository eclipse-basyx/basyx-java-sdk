/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.mqtt;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.observing.ObservableAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Implementation variant for the AASAPI that triggers MQTT events for different
 * operations on the AAS. Has to be based on a backend implementation of the
 * IAASAPI to forward its method calls.
 * 
 * 
 * @author fried
 *
 */
public class MqttAASAPI implements IAASAPI {

	protected ObservableAASAPI observedAPI;

	private MqttAASAPIObserver observer;

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPI(IAASAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI with a
	 * custom persistence strategy
	 */
	public MqttAASAPI(IAASAPI observedAPI, String brokerEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		this.observedAPI = new ObservableAASAPI(observedAPI);
		observer = new MqttAASAPIObserver(this.observedAPI, brokerEndpoint, clientId, persistence);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI
	 * 
	 * @param observedAPI
	 *            The underlying aasAPI
	 * @throws MqttException
	 */
	public MqttAASAPI(IAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another AASAPI.
	 */
	public MqttAASAPI(IAASAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		this.observedAPI = new ObservableAASAPI(observedAPI);
		observer = new MqttAASAPIObserver(this.observedAPI, serverEndpoint, clientId, user, pw, persistence);
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
	public MqttAASAPI(IAASAPI observedAPI, MqttClient client) throws MqttException {
		this.observedAPI = new ObservableAASAPI(observedAPI);
		observer = new MqttAASAPIObserver(this.observedAPI, client);
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		return observedAPI.getAAS();
	}

	@Override
	public void addSubmodel(IReference submodelReference) {
		observedAPI.addSubmodel(submodelReference);
	}

	@Override
	public void removeSubmodel(String id) {
		observedAPI.removeSubmodel(id);
	}
}
