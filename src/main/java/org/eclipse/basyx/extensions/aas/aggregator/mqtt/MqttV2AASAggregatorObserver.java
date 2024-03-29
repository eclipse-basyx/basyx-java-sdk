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
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.aas.aggregator.observing.IAASAggregatorObserverV2;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.coder.json.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer for the AASAggregator that triggers MQTT events for different
 * operations on the aggregator.
 *
 * @author haque, jungjan, fischer, siebert
 *
 */
public class MqttV2AASAggregatorObserver extends MqttEventService implements IAASAggregatorObserverV2 {
	private static Logger logger = LoggerFactory.getLogger(MqttV2AASAggregatorObserver.class);
	private MqttV2AASAggregatorTopicFactory topicFactory;
	private Serializer payloadSerializer;

	/**
	 * Constructor for adding this MQTT extension as an AAS Aggregator Observer
	 *
	 * @param client
	 *            already configured client
	 * @param topicFactory
	 * @throws MqttException
	 */
	public MqttV2AASAggregatorObserver(MqttClient client, MqttV2AASAggregatorTopicFactory topicFactory) throws MqttException {
		this(client, topicFactory, createGSONTools());
	}

	/**
	 * Constructor for adding this MQTT extension as an AAS Aggregator Observer
	 * 
	 * @param client
	 * @param topicFactory
	 * @param payloadSerializer
	 * @throws MqttException
	 */
	public MqttV2AASAggregatorObserver(MqttClient client, MqttV2AASAggregatorTopicFactory topicFactory, Serializer payloadSerializer) throws MqttException {
		super(client);
		this.topicFactory = topicFactory;
		this.payloadSerializer = payloadSerializer;
		logger.info("Create new MQTT AAS Aggregator Observer for endpoint " + client.getServerURI());
	}

	private static GSONTools createGSONTools() {
		return new GSONTools(new DefaultTypeFactory(), false, false);
	}

	@Override
	public void aasCreated(AssetAdministrationShell shell, String repoId) {
		if (shell instanceof Map<?, ?>) {
			IAssetAdministrationShell copy = removeConceptDictionaries(shell);		
			sendMqttMessage(topicFactory.createCreateAASTopic(repoId), serializePayload(copy));
		} else {
			sendMqttMessage(topicFactory.createCreateAASTopic(repoId), serializePayload(shell));
		}
	}

	@Override
	public void aasUpdated(AssetAdministrationShell shell, String repoId) {
		if (shell instanceof Map<?, ?>) {
			IAssetAdministrationShell copy = removeConceptDictionaries(shell);
			sendMqttMessage(topicFactory.createUpdateAASTopic(repoId), serializePayload(copy));
		} else {
			sendMqttMessage(topicFactory.createUpdateAASTopic(repoId), serializePayload(shell));
		}
	}

	@Override
	public void aasDeleted(IAssetAdministrationShell shell, String repoId) {
		if (shell instanceof Map<?, ?>) {
			IAssetAdministrationShell copy = removeConceptDictionaries(shell);
			sendMqttMessage(topicFactory.createDeleteAASTopic(repoId), serializePayload(copy));
		} else {			
			sendMqttMessage(topicFactory.createDeleteAASTopic(repoId), serializePayload(shell));
		}
	}
	
	@SuppressWarnings("unchecked")
	private IAssetAdministrationShell removeConceptDictionaries(IAssetAdministrationShell shell) {
		Map<String, Object> map = (Map<String, Object>) shell;
		Map<String, Object> copy = new LinkedHashMap<>(map);
		AssetAdministrationShell aas = AssetAdministrationShell.createAsFacade(copy);
		aas.setConceptDictionary(Collections.emptyList());
		
		return aas;
	}
	
	private String serializePayload(IAssetAdministrationShell shell) {
		return payloadSerializer.serialize(shell);
	}
}
