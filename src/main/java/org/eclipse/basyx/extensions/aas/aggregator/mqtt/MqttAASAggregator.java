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
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASAggregator that triggers MQTT events for
 * different operations on the aggregator. Has to be based on a backend
 * implementation of the IAASAggregator to forward its method calls.
 *
 * @author haque, jungjan, fischer
 *
 */
public class MqttAASAggregator implements IAASAggregator {
	private static Logger logger = LoggerFactory.getLogger(MqttAASAggregator.class);

	protected ObservableAASAggregator observedAASAggregator;
	private MqttAASAggregatorObserver observer;

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedAASAggregator the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator observedAASAggregator, String serverEndpoint, String clientId) throws MqttException {
		this(observedAASAggregator, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedAASAggregator the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @param user username for authentication with broker
	 * @param pw password for authentication with broker
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, String serverEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		observedAASAggregator = new ObservableAASAggregator(aasAggregatorToBeObserved);
		observer = new MqttAASAggregatorObserver(serverEndpoint, clientId, persistence);
		observedAASAggregator.addObserver(observer);
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param observedAASAggregator the underlying AAS Aggregator 
	 * @param client already configured client
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(aasAggregatorToBeObserved, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 *
	 * @param aasAggregatorToBeObserved
	 * @param serverEndpoint
	 * @param clientId
	 * @param user
	 * @param pw
	 * @param persistence
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		observedAASAggregator = new ObservableAASAggregator(aasAggregatorToBeObserved);
		observer = new MqttAASAggregatorObserver(serverEndpoint, clientId, user, pw, persistence);
		observedAASAggregator.addObserver(observer);
	}

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return observedAASAggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier shellId) throws ResourceNotFoundException {
		return observedAASAggregator.getAAS(shellId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier shellId) throws ResourceNotFoundException {
		return observedAASAggregator.getAASProvider(shellId);
	}

	@Override
	public void createAAS(AssetAdministrationShell shell) {
		observedAASAggregator.createAAS(shell);
	}

	@Override
	public void updateAAS(AssetAdministrationShell shell) throws ResourceNotFoundException {
		observedAASAggregator.updateAAS(shell);
	}

	@Override
	public void deleteAAS(IIdentifier shellId) {
		observedAASAggregator.deleteAAS(shellId);
	}
}
