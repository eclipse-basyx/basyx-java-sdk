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

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.observing.ObservableSubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAggregator that triggers MQTT events
 * for different operations on the aggregator. Has to be based on a backend
 * implementation of the ISubmodelAggregator to forward its method calls.
 *
 * @author fischer, jungjan, fried
 *
 */
public class MqttSubmodelAggregator implements ISubmodelAggregator {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAggregator.class);

	protected ObservableSubmodelAggregator observedSubmodelAggregator;
	private MqttSubmodelAggregatorObserver observer;

	/**
	 * Constructor for adding MQTT extension on top of a SubmodelAggregator
	 * 
	 * @param observedSubmodelAggregator
	 * @param serverEndpoint
	 * @param clientId
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator observedSubmodelAggregator, String serverEndpoint, String clientId) throws MqttException {
		this(observedSubmodelAggregator, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding MQTT extension on top of a SubmodelAggregator
	 * 
	 * @param submodelAggregatorToBeObserved
	 * @param serverEndpoint
	 * @param clientId
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator submodelAggregatorToBeObserved, String serverEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		observedSubmodelAggregator = new ObservableSubmodelAggregator(submodelAggregatorToBeObserved);
		observer = new MqttSubmodelAggregatorObserver(observedSubmodelAggregator, serverEndpoint, clientId, persistence);
	}

	/**
	 * Constructor for adding MQTT extension on top of a SubmodelAggregator
	 * 
	 * @param submodelAggregatorToBeObserved
	 * @param serverEndpoint
	 * @param clientId
	 * @param user
	 * @param pw
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator submodelAggregatorToBeObserved, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(submodelAggregatorToBeObserved, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * 
	 * @param submodelAggregatorToBeObserved
	 * @param serverEndpoint
	 * @param clientId
	 * @param user
	 * @param pw
	 * @param persistence
	 * @throws MqttException
	 */
	public MqttSubmodelAggregator(ISubmodelAggregator submodelAggregatorToBeObserved, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		observedSubmodelAggregator = new ObservableSubmodelAggregator(submodelAggregatorToBeObserved);
		observer = new MqttSubmodelAggregatorObserver(observedSubmodelAggregator, serverEndpoint, clientId, user, pw, persistence);
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
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		observedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier submodelIdentifier) {
		observedSubmodelAggregator.deleteSubmodelByIdentifier(submodelIdentifier);
	}

	@Override
	public void deleteSubmodelByIdShort(String submodelIdShort) {
		observedSubmodelAggregator.deleteSubmodelByIdShort(submodelIdShort);
	}
}
