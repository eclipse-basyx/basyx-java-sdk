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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the SubmodelAPI that triggers MQTT events for
 * different CRUD operations on the submodel. Has to be based on a backend
 * implementation of the ISubmodelAPI to forward its method calls.
 * 
 * @author espen
 *
 */
public class MqttSubmodelAPI extends MqttEventService implements ISubmodelAPI {
	private static Logger logger = LoggerFactory.getLogger(MqttSubmodelAPI.class);

	// List of topics
	public static final String TOPIC_CREATESUBMODEL = "BaSyxSubmodel_createdSubmodel";
	public static final String TOPIC_ADDELEMENT = "BaSyxSubmodel_addedSubmodelElement";
	public static final String TOPIC_DELETEELEMENT = "BaSyxSubmodel_removedSubmodelElement";
	public static final String TOPIC_UPDATEELEMENT = "BaSyxSubmodel_updatedSubmodelElement";

	// The underlying SubmodelAPI
	protected ISubmodelAPI observedAPI;

	// Submodel Element whitelist for filtering
	protected boolean useWhitelist = false;
	protected Set<String> whitelist = new HashSet<>();

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @throws MqttException
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT submodel for endpoint " + serverEndpoint);
		this.observedAPI = observedAPI;
		sendMqttMessage(TOPIC_CREATESUBMODEL, observedAPI.getSubmodel().getIdentification().getId());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @throws MqttException
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, String serverEndpoint, String clientId, String user, char[] pw)
			throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT submodel for endpoint " + serverEndpoint);
		this.observedAPI = observedAPI;
		sendMqttMessage(TOPIC_CREATESUBMODEL, observedAPI.getSubmodel().getIdentification().getId());
	}

	/**
	 * Constructor for adding this MQTT extension on top of another SubmodelAPI.
	 * 
	 * @param observedAPI The underlying submodelAPI
	 * @param client      An already connected mqtt client
	 * @throws MqttException 
	 */
	public MqttSubmodelAPI(ISubmodelAPI observedAPI, MqttClient client) throws MqttException {
		super(client);
		this.observedAPI = observedAPI;
		sendMqttMessage(TOPIC_CREATESUBMODEL, observedAPI.getSubmodel().getIdentification().getId());
	}

	/**
	 * Adds a submodel element to the filter whitelist. Can also be a path for nested submodel elements.
	 * 
	 * @param shortId
	 */
	public void observeSubmodelElement(String shortId) {
		whitelist.add(VABPathTools.stripSlashes(shortId));
	}

	/**
	 * Sets a new filter whitelist.
	 * 
	 * @param shortIds
	 */
	public void setWhitelist(Set<String> shortIds) {
		this.whitelist.clear();
		for (String entry : shortIds) {
			this.whitelist.add(VABPathTools.stripSlashes(entry));
		}
	}

	/**
	 * Disables the submodel element filter whitelist
	 */
	public void disableWhitelist() {
		useWhitelist = false;
	}

	/**
	 * Enables the submodel element filter whitelist
	 */
	public void enableWhitelist() {
		useWhitelist = true;
	}

	@Override
	public ISubmodel getSubmodel() {
		return observedAPI.getSubmodel();
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		observedAPI.addSubmodelElement(elem);
		if (filter(elem.getIdShort())) {
			sendMqttMessage(TOPIC_ADDELEMENT, getCombinedMessage(getAASId(), getSubmodelId(), elem.getIdShort()));
		}
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		observedAPI.addSubmodelElement(idShortPath, elem);
		if (filter(idShortPath)) {
			sendMqttMessage(TOPIC_ADDELEMENT, getCombinedMessage(getAASId(), getSubmodelId(), idShortPath));
		}
	}

	@Override
	public ISubmodelElement getSubmodelElement(String idShortPath) {
		return observedAPI.getSubmodelElement(idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		observedAPI.deleteSubmodelElement(idShortPath);
		if (filter(idShortPath)) {
			sendMqttMessage(TOPIC_DELETEELEMENT, getCombinedMessage(getAASId(), getSubmodelId(), idShortPath));
		}
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
		if (filter(idShortPath)) {
			sendMqttMessage(TOPIC_UPDATEELEMENT, getCombinedMessage(getAASId(), getSubmodelId(), idShortPath));
		}
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

	private boolean filter(String idShort) {
		idShort = VABPathTools.stripSlashes(idShort);
		return !useWhitelist || whitelist.contains(idShort);
	}
	
	private String getSubmodelId() {
		ISubmodel submodel = getSubmodel();
		return submodel.getIdentification().getId();
	}
	
	private String getAASId() {
		ISubmodel submodel = getSubmodel();
		IReference parentReference = submodel.getParent();
		if (parentReference != null) {
			List<IKey> keys = parentReference.getKeys();
			if (keys != null && keys.size() > 0) {
				return keys.get(0).getValue();
			}
		}
		return null;
	}
}
