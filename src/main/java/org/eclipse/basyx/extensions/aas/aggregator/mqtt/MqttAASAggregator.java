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

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Implementation variant for the AASAggregator that triggers MQTT events for
 * different operations on the aggregator. Has to be based on a backend
 * implementation of the IAASAggregator to forward its method calls.
 *
 * @author haque, jungjan, fischer
 * 
 * @deprecated Deprecated, please use {@link MqttDecoratingAASAggregatorFactory}
 *
 */
@Deprecated
public class MqttAASAggregator implements IAASAggregator {

	protected ObservableAASAggregator observedAASAggregator;
	private MqttAASAggregatorObserver observer;

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param aasAggregatorToBeObserved the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, String serverEndpoint, String clientId) throws MqttException {
		this(aasAggregatorToBeObserved, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, MqttClient client) throws MqttException {
		observedAASAggregator = new ObservableAASAggregator(aasAggregatorToBeObserved);
		observer = new MqttAASAggregatorObserver(client);
		observedAASAggregator.addObserver(observer);
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASAggregator
	 * 
	 * @param aasAggregatorToBeObserved the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier

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
	 * @param aasAggregatorToBeObserved the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @param user username for authentication with broker
	 * @param pw password for authentication with broker
 	 * @param pw password for authentication with broker
	 * @throws MqttException
	 */
	public MqttAASAggregator(IAASAggregator aasAggregatorToBeObserved, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		this(aasAggregatorToBeObserved, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 *
	 * @param aasAggregatorToBeObserved the underlying AAS Aggregator 
	 * @param serverEndpoint endpoint of mqtt broker
	 * @param clientId unique client identifier
	 * @param user username for authentication with broker
	 * @param persistence persistence level
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
