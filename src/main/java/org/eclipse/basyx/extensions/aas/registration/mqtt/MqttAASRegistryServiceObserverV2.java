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
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.observing.IAASRegistryServiceObserverV2;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASRegistryServiceObserver that triggers MQTT
 * events for different operations on the registry.
 * 
 * @author haque
 *
 */
public class MqttAASRegistryServiceObserverV2 extends MqttEventService implements IAASRegistryServiceObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttAASRegistryServiceObserverV2.class);

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer
	 * 
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @throws MqttException
	 */
	public MqttAASRegistryServiceObserverV2(String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT AAS Registry Service Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer with a
	 * custom mqtt client persistence
	 * 
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @param clientId
	 *            unique client identifier
	 * @param mqttPersistence
	 *            custom mqtt persistence strategy
	 * @throws MqttException
	 */
	public MqttAASRegistryServiceObserverV2(String serverEndpoint, String clientId, MqttClientPersistence mqttPersistence) throws MqttException {
		super(serverEndpoint, clientId, mqttPersistence);
		logger.info("Create new MQTT AAS Registry Service Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for creating an MqttClient with authentication and a custom
	 * persistence strategy
	 *
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @param user
	 *            username for authentication with broker
	 * @param pw
	 *            password for authentication with broker
	 * @param mqttPersistence
	 *            custom mqtt persistence strategy
	 */
	public MqttAASRegistryServiceObserverV2(String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence mqttPersistence) throws MqttException {
		super(serverEndpoint, clientId, user, pw, mqttPersistence);
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer
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
	public MqttAASRegistryServiceObserverV2(String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AAS Registry Service Observer for endpoint " + serverEndpoint);
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer
	 * 
	 * @param client
	 *            already configured client
	 * @throws MqttException
	 */
	public MqttAASRegistryServiceObserverV2(MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT AAS Registry Service Observer for endpoint " + client.getServerURI());
	}

	@Override
	public void aasRegistered(AASDescriptor aasDescriptor, String registryId) {		
		sendMqttMessage(MqttAASRegistryHelperV2.createCreateAASTopic(registryId), serializePayload(aasDescriptor));
	}

	@Override
	public void submodelRegistered(SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(MqttAASRegistryHelperV2.createCreateSubmodelTopic(registryId), serializePayload(smDescriptor));
	}
	@Override
	public void aasUpdated(AASDescriptor aasDescriptor, String registryId) {
		sendMqttMessage(MqttAASRegistryHelperV2.createUpdateAASTopic(registryId), serializePayload(aasDescriptor));
	}
	
	public void submodelUpdated(SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(MqttAASRegistryHelperV2.createUpdateSubmodelTopic(registryId), serializePayload(smDescriptor));
	}

	@Override
	public void aasDeleted(AASDescriptor aasDescriptor, String registryId) {
		sendMqttMessage(MqttAASRegistryHelperV2.createDeleteAASTopic(registryId), serializePayload(aasDescriptor));
	}

	@Override
	public void submodelDeleted(SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(MqttAASRegistryHelperV2.createDeleteSubmodelTopic(registryId), serializePayload(smDescriptor));
	}
	
	private String serializePayload(ModelDescriptor descriptor) {
		GSONTools gsonTools = new GSONTools(new DefaultTypeFactory(), false, false);
		
		return gsonTools.serialize(descriptor);
	}
}
