/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.mqtt;

import java.util.Collection;
import java.util.Set;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 * Implementation variant for the SubmodelAPI that triggers MQTT events for
 * different CRUD operations on the submodel. Has to be based on a backend
 * implementation of the ISubmodelAPI to forward its method calls.
 * 
 * 
 * @author espen
 *
 */
public class MqttSubmodelAPI implements ISubmodelAPI {

	// List of topics
	public static final String TOPIC_CREATESUBMODEL = "BaSyxSubmodel_createdSubmodel";
	public static final String TOPIC_ADDELEMENT = "BaSyxSubmodel_addedSubmodelElement";
	public static final String TOPIC_DELETEELEMENT = "BaSyxSubmodel_removedSubmodelElement";
	public static final String TOPIC_UPDATEELEMENT = "BaSyxSubmodel_updatedSubmodelElement";

	// The underlying SubmodelAPI
	protected ObservableSubmodelAPI observedAPI;
	
	private MqttSubmodelAPIObserver observer;


	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @throws MqttException
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		this(observedAPI, serverEndpoint, clientId, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI with
	 * a custom persistence strategy
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String brokerEndpoint, String clientId, MqttClientPersistence persistence) throws MqttException {
		this.observedAPI = new ObservableSubmodelAPI(observedAPI);
		observer = new MqttSubmodelAPIObserver(this.observedAPI, brokerEndpoint, clientId, persistence);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @throws MqttException
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw)
			throws MqttException {
		this(observedAPI, serverEndpoint, clientId, user, pw, new MqttDefaultFilePersistence());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI.
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw, MqttClientPersistence persistence) throws MqttException {
		this.observedAPI = new ObservableSubmodelAPI(observedAPI);
		observer = new MqttSubmodelAPIObserver(this.observedAPI, serverEndpoint, clientId, user, pw, persistence);
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI.
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @param client      An already connected mqtt client
	 * @throws MqttException 
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, MqttClient client) throws MqttException {
		this.observedAPI = new ObservableSubmodelAPI(observedAPI);
		observer = new MqttSubmodelAPIObserver(this.observedAPI, client);
	}

	/**
	 * Adds a submodel element to the filter whitelist. Can also be a path for nested submodel elements.
	 * 
	 * @param shortId
	 */
	public void observeSubmodelElement(String shortId) {
		observer.observeSubmodelElement(VABPathTools.stripSlashes(shortId));
	}

	/**
	 * Sets a new filter whitelist.
	 * 
	 * @param shortIds
	 */
	public void setWhitelist(Set<String> shortIds) {
		observer.setWhitelist(shortIds);
	}

	/**
	 * Disables the submodel element filter whitelist
	 */
	public void disableWhitelist() {
		observer.disableWhitelist();
	}

	/**
	 * Enables the submodel element filter whitelist
	 */
	public void enableWhitelist() {
		observer.enableWhitelist();
	}

	@Override
	public ISubmodel getSubmodel() {
		return observedAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		observedAPI.addSubmodelElement(elem);
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		observedAPI.addSubmodelElement(idShortPath, elem);
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return observedAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		observedAPI.deleteSubmodelElement(idShortPath);
	}

	@Override
	public Collection<IOperation> getOperations() {
		return observedAPI.getOperations();
	}

	@Override
	public Collection<ISubmodelElement> getSubmodelElements() {
		return observedAPI.getSubmodelElements();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		observedAPI.updateSubmodelElement(idShortPath, newValue);
	}

	@Override
	public Object getSubmodelElementValue(String idShortPath) {
		return observedAPI.getSubmodelElementValue(idShortPath);
	}

	@Override
	public Object invokeOperation(String idShortPath, Object... params) {
		return observedAPI.invokeOperation(idShortPath, params);
	}

	@Override
	public Object invokeAsync(String idShortPath, Object... params) {
		return observedAPI.invokeAsync(idShortPath, params);
	}

	@Override
	public Object getOperationResult(String idShort, String requestId) {
		return observedAPI.getOperationResult(idShort, requestId);
	}
	
	public static String getCombinedMessage(String aasId, String submodelId, String elementPart) {
		elementPart = VABPathTools.stripSlashes(elementPart);
		return "(" + aasId + "," + submodelId + "," + elementPart + ")";
	}
}
