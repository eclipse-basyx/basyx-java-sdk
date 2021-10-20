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

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASAggregator that triggers MQTT events for
 * different operations on the aggregator. Has to be based on a backend
 * implementation of the IAASAggregator to forward its method calls.
 * 
 * @author haque
 *
 */
public class MqttAASAggregator extends MqttEventService implements IAASAggregator {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAggregator.class);

	// List of topics
	public static final String TOPIC_CREATEAAS = "BaSyxAggregator_createdAAS";
	public static final String TOPIC_DELETEAAS = "BaSyxAggregator_deletedAAS";
	public static final String TOPIC_UPDATEAAS = "BaSyxAggregator_updatedAAS";

	// The underlying AASAggregator
	protected IAASAggregator observedAASAggregator;
	
	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedRegistryService the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator observedAASAggregator, String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT AAS Aggregator for endpoint " + serverEndpoint);
		this.observedAASAggregator = observedAASAggregator;
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedRegistryService the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @param user username for authentication with broker
	 * @param pw password for authentication with broker
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator observedAASAggregator, String serverEndpoint, String clientId, String user, char[] pw)
			throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AAS Aggregator for endpoint " + serverEndpoint);
		this.observedAASAggregator = observedAASAggregator;
	}
	
	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedRegistryService the underlying AAS Aggregator 
	 * @param client already configured client
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator observedAASAggregator, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT AAS Aggregator for endpoint " + client.getServerURI());
		this.observedAASAggregator = observedAASAggregator;
	}
	
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return this.observedAASAggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) throws ResourceNotFoundException {
		return this.observedAASAggregator.getAAS(aasId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) throws ResourceNotFoundException {
		return this.observedAASAggregator.getAASProvider(aasId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		this.observedAASAggregator.createAAS(aas);
		sendMqttMessage(TOPIC_CREATEAAS, aas.getIdentification().getId());
		
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) throws ResourceNotFoundException {
		this.observedAASAggregator.updateAAS(aas);
		sendMqttMessage(TOPIC_UPDATEAAS, aas.getIdentification().getId());
		
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		this.observedAASAggregator.deleteAAS(aasId);
		sendMqttMessage(TOPIC_DELETEAAS, aasId.getId());
	}
}
