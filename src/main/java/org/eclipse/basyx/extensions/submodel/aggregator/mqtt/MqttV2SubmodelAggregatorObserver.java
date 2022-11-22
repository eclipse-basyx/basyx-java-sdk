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

import java.util.Map;

import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.aggregator.observing.ISubmodelAggregatorObserverV2;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.coder.json.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the SubmodelAggregator that triggers MQTT events for different
 * operations on the aggregator.
 *
 * @author fischer, jungjan, fried, siebert
 *
 */
public class MqttV2SubmodelAggregatorObserver extends MqttEventService implements ISubmodelAggregatorObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttV2SubmodelAggregatorObserver.class);

	private MqttV2SubmodelAggregatorTopicFactory topicFactory;

	private Serializer payloadSerializer;

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 *
	 * @param client
	 *            already configured client
	 * @param MqttV2SubmodelAggregatorTopicFactory
	 * @throws MqttException
	 */
	public MqttV2SubmodelAggregatorObserver(MqttClient client, MqttV2SubmodelAggregatorTopicFactory topicFactory) throws MqttException {
		this(client, topicFactory, createGSONTools());
	}

	/**
	 * Constructor for adding this MQTT extension as an Submodel Aggregator Observer
	 *
	 * @param client
	 *            already configured client
	 * @param MqttV2SubmodelAggregatorTopicFactory
	 * @param payloadSerializer
	 * @throws MqttException
	 */
	public MqttV2SubmodelAggregatorObserver(MqttClient client, MqttV2SubmodelAggregatorTopicFactory topicFactory, Serializer payloadSerializer) throws MqttException {
		super(client);
		this.topicFactory = topicFactory;
		this.payloadSerializer = payloadSerializer;
		logger.info("Create new MQTT Submodel Aggregator Observer for endpoint " + client.getServerURI());
	}

	private static GSONTools createGSONTools() {
		return new GSONTools(new DefaultTypeFactory(), false, false);
	}


	@Override
	public void submodelCreated(String shellId, ISubmodel submodel, String repoId) {
		if (submodel instanceof Map<?, ?>) {
			ISubmodel copy = removeSubmodelElements(submodel);
			sendMqttMessage(topicFactory.createCreateSubmodelTopic(shellId, repoId), serializePayload(copy));
		} else {			
			sendMqttMessage(topicFactory.createCreateSubmodelTopic(shellId, repoId), serializePayload(submodel));
		}
		
	}

	@Override
	public void submodelUpdated(String shellId, ISubmodel submodel, String repoId) {
		if (submodel instanceof Map<?, ?>) {
			ISubmodel copy = removeSubmodelElements(submodel);
			sendMqttMessage(topicFactory.createUpdateSubmodelTopic(shellId, repoId), serializePayload(copy));
		} else {			
			sendMqttMessage(topicFactory.createUpdateSubmodelTopic(shellId, repoId), serializePayload(submodel));
		}
	}

	@Override
	public void submodelDeleted(String shellId, ISubmodel submodel, String repoId) {
		if (submodel instanceof Map<?, ?>) {
			ISubmodel copy = removeSubmodelElements(submodel);
			sendMqttMessage(topicFactory.createDeleteSubmodelTopic(shellId, repoId), serializePayload(copy));
		} else {			
			sendMqttMessage(topicFactory.createDeleteSubmodelTopic(shellId, repoId), serializePayload(submodel));
		}
	}
	
	private ISubmodel removeSubmodelElements(ISubmodel submodel) {
		Map<String, Object> map = SubmodelElementMapCollectionConverter.smToMap((Submodel) submodel);
		Submodel copy =  Submodel.createAsFacade(map);
			
		for (ISubmodelElement sme: submodel.getSubmodelElements().values()) {
			copy.deleteSubmodelElement(sme.getIdShort());
		}
		
		return copy;
	}
	
	private String serializePayload(ISubmodel submodel) {
		return payloadSerializer.serialize(submodel);
	}
}
