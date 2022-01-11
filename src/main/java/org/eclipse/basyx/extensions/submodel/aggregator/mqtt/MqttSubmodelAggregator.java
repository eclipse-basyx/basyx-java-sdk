/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.mqtt;

import java.util.Collection;

import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that triggers MQTT events
 * for different operations on the aggregator. Has to be based on a backend
 * implementation of the ISubmodelAggregator to forward its method calls.
 *
 * @author fischer, jungjan
 *
 */
public class MqttSubmodelAggregator extends MqttEventService implements ISubmodelAggregator {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAggregator.class);

	protected ISubmodelAggregator observedSubmodelAggregator;

	/**
	 * Constructor for adding this MQTT extension on top of an SubmodelAggregator
	 *
	 * @param observedSubmodelAggregator
	 *            the underlying Submodel Aggregator
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator observedSubmodelAggregator, String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT Submodel Aggregator for endpoint " + serverEndpoint);
		this.observedSubmodelAggregator = observedSubmodelAggregator;
	}

	/**
	 * Constructor for adding this MQTT extension on top of an SubmodelAggregator
	 *
	 * @param observedSubmodelAggregator
	 *            the underlying Submodel Aggregator
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
	public MqttSubmodelAggregator(ISubmodelAggregator observedSubmodelAggregator, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT Submodel Aggregator for endpoint " + serverEndpoint);
		this.observedSubmodelAggregator = observedSubmodelAggregator;
	}

	/**
	 * Constructor for adding this MQTT extension on top of an SubmodelAggregator
	 *
	 * @param observedSubmodelAggregator
	 *            the underlying Submodel Aggregator
	 * @param client
	 *            already configured client
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator observedSubmodelAggregator, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT Submodel Aggregator for endpoint " + client.getServerURI());
		this.observedSubmodelAggregator = observedSubmodelAggregator;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return observedSubmodelAggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier submodelIdentifier) throws ResourceNotFoundException {
		return observedSubmodelAggregator.getSubmodel(submodelIdentifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String submodelIdShort) throws ResourceNotFoundException {
		return observedSubmodelAggregator.getSubmodelbyIdShort(submodelIdShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier submodelIdentifier) throws ResourceNotFoundException {
		return observedSubmodelAggregator.getSubmodelAPIById(submodelIdentifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String submodelIdShort) throws ResourceNotFoundException {
		return observedSubmodelAggregator.getSubmodelAPIByIdShort(submodelIdShort);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		observedSubmodelAggregator.createSubmodel(submodel);
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_CREATESUBMODEL, submodel.getIdentification().getId());
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		observedSubmodelAggregator.updateSubmodel(submodel);
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_UPDATESUBMODEL, submodel.getIdentification().getId());
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier submodelIdentifier) {
		observedSubmodelAggregator.deleteSubmodelByIdentifier(submodelIdentifier);
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, submodelIdentifier.getId());
	}

	@Override
	public void deleteSubmodelByIdShort(String submodelIdShort) {
		ISubmodel submodelToDelete = observedSubmodelAggregator.getSubmodelbyIdShort(submodelIdShort);
		String submodelId = submodelToDelete.getIdentification().getId();

		observedSubmodelAggregator.deleteSubmodelByIdShort(submodelIdShort);
		sendMqttMessage(MqttSubmodelAggregatorHelper.TOPIC_DELETESUBMODEL, submodelId);
	}
}
