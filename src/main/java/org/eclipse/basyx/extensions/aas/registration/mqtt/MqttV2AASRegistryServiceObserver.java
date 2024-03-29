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
import org.eclipse.basyx.vab.coder.json.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASRegistryServiceObserver that triggers MQTT
 * events for different operations on the registry.
 * 
 * @author haque, siebert
 *
 */
public class MqttV2AASRegistryServiceObserver extends MqttEventService implements IAASRegistryServiceObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttV2AASRegistryServiceObserver.class);
	private MqttV2AASRegistryTopicFactory topicFactory;
	private Serializer payloadSerializer;

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer
	 * 
	 * @param client
	 *            already configured client
	 * @param topicFactory
	 * @throws MqttException
	 */
	public MqttV2AASRegistryServiceObserver(MqttClient client, MqttV2AASRegistryTopicFactory topicFactory) throws MqttException {
		this(client, topicFactory, createGSONTools());
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Registry Observer
	 * 
	 * @param client
	 *            already configured client
	 * @param topicFactory
	 * @param payloadSerializer
	 * @throws MqttException
	 */
	public MqttV2AASRegistryServiceObserver(MqttClient client, MqttV2AASRegistryTopicFactory topicFactory, Serializer payloadSerializer) throws MqttException {
		super(client);
		this.topicFactory = topicFactory;
		this.payloadSerializer = payloadSerializer;
		logger.info("Create new MQTT AAS Registry Service Observer for endpoint " + client.getServerURI());
	}

	@Override
	public void aasRegistered(AASDescriptor aasDescriptor, String registryId) {		
		sendMqttMessage(topicFactory.createCreateAASTopic(registryId), serializePayload(aasDescriptor));
	}

	@Override
	public void submodelRegistered(IIdentifier aasId, SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(topicFactory.createCreateSubmodelTopic(registryId), serializePayload(smDescriptor));
		sendMqttMessage(topicFactory.createCreateSubmodelTopicWithAASId(aasId.getId(), registryId), serializePayload(smDescriptor));
	}
	@Override
	public void aasUpdated(AASDescriptor aasDescriptor, String registryId) {
		sendMqttMessage(topicFactory.createUpdateAASTopic(registryId), serializePayload(aasDescriptor));
	}
	
	@Override
	public void submodelUpdated(IIdentifier aasId, SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(topicFactory.createUpdateSubmodelTopic(registryId), serializePayload(smDescriptor));
		sendMqttMessage(topicFactory.createUpdateSubmodelTopicWithAASId(aasId.getId(), registryId), serializePayload(smDescriptor));
	}

	@Override
	public void aasDeleted(AASDescriptor aasDescriptor, String registryId) {
		sendMqttMessage(topicFactory.createDeleteAASTopic(registryId), serializePayload(aasDescriptor));
	}

	@Override
	public void submodelDeleted(IIdentifier aasId, SubmodelDescriptor smDescriptor, String registryId) {
		sendMqttMessage(topicFactory.createDeleteSubmodelTopic(registryId), serializePayload(smDescriptor));
		sendMqttMessage(topicFactory.createDeleteSubmodelTopicWithAASId(aasId.getId(), registryId), serializePayload(smDescriptor));
	}
	
	private String serializePayload(ModelDescriptor descriptor) {
		return payloadSerializer.serialize(descriptor);
	}

	private static GSONTools createGSONTools() {
		return new GSONTools(new DefaultTypeFactory(), false, false);
	}
}
