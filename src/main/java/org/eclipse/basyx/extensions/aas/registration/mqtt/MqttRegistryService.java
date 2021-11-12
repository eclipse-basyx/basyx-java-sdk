/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import java.util.List;

import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation variant for the AASRegistryService that triggers MQTT events
 * for different operations on the registry. Has to be based on a backend
 * implementation of the IAASRegistryService to forward its method calls.
 *
 * @author haque
 *
 */
public class MqttRegistryService extends MqttEventService implements IRegistry {
	private static Logger logger = LoggerFactory.getLogger(MqttRegistryService.class);

	// The underlying AASRegistryService
	protected IRegistry observedRegistryService;

	/**
	 * Constructor for adding this MQTT extension on top of an AASRegistryService
	 *
	 * @param observedRegistryService
	 *            the underlying registry service
	 * @param serverEndpoint
	 *            endpoint of mqtt broker
	 * @param clientId
	 *            unique client identifier
	 * @throws MqttException
	 */
	public MqttRegistryService(IRegistry observedRegistryService, String serverEndpoint, String clientId) throws MqttException {
		super(serverEndpoint, clientId);
		logger.info("Create new MQTT AAS Registry Service for endpoint " + serverEndpoint);
		this.observedRegistryService = observedRegistryService;
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASRegistryService
	 *
	 * @param observedRegistryService
	 *            the underlying registry service
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
	public MqttRegistryService(IRegistry observedRegistryService, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
		super(serverEndpoint, clientId, user, pw);
		logger.info("Create new MQTT AAS Registry Service for endpoint " + serverEndpoint);
		this.observedRegistryService = observedRegistryService;
	}

	/**
	 * Constructor for adding this MQTT extension on top of an AASRegistryService
	 *
	 * @param observedRegistryService
	 *            the underlying registry service
	 * @param client
	 *            already configured client
	 * @throws MqttException
	 */
	public MqttRegistryService(IRegistry observedRegistryService, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT AAS Registry Service for endpoint " + client.getServerURI());
		this.observedRegistryService = observedRegistryService;
	}

	@Override
	public void register(AASDescriptor shellDescriptor) throws ProviderException {
		this.observedRegistryService.register(shellDescriptor);
		sendMqttMessage(TOPIC_REGISTERAAS, shellDescriptor.getIdentifier().getId());
	}

	@Override
	public void register(SubmodelDescriptor submodelDescriptor) throws ProviderException {
		this.observedRegistryService.register(submodelDescriptor);
		sendMqttMessage(TOPIC_REGISTERSUBMODEL, submodelDescriptor.getIdentifier().getId());
	}

	@Override
	public void updateShell(IIdentifier shellIdentifier, AASDescriptor shellDescriptor) throws ProviderException {
		this.observedRegistryService.updateShell(shellIdentifier, shellDescriptor);
		sendMqttMessage(TOPIC_UPDATEAAS, shellDescriptor.getIdentifier().getId());
	}

	@Override
	public void updateSubmodel(IIdentifier submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		this.observedRegistryService.updateSubmodel(submodelIdentifier, submodelDescriptor);
		sendMqttMessage(TOPIC_UPDATESUBMODEL, submodelDescriptor.getIdentifier().getId());
	}

	@Override
	public void registerSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		this.observedRegistryService.registerSubmodelForShell(shellIdentifier, submodelDescriptor);
		sendMqttMessage(TOPIC_REGISTERSUBMODEL, concatAasSmId(shellIdentifier, submodelDescriptor.getIdentifier()));
	}

	@Override
	public void updateSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		this.observedRegistryService.updateSubmodelForShell(shellIdentifier, submodelDescriptor);
		sendMqttMessage(TOPIC_UPDATESUBMODEL, concatAasSmId(shellIdentifier, submodelDescriptor.getIdentifier()));
	}

	@Override
	public void deleteModel(IIdentifier shellIdentifier) throws ProviderException {
		this.observedRegistryService.deleteModel(shellIdentifier);
		sendMqttMessage(TOPIC_DELETEAAS, shellIdentifier.getId());
	}

	@Override
	public void deleteSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		this.observedRegistryService.deleteModel(submodelIdentifier);
		sendMqttMessage(TOPIC_DELETESUBMODEL, submodelIdentifier.getId());
	}

	@Override
	public void deleteSubmodelFromShell(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		this.observedRegistryService.deleteSubmodelFromShell(shellIdentifier, submodelIdentifier);
		sendMqttMessage(TOPIC_DELETESUBMODEL, concatAasSmId(shellIdentifier, submodelIdentifier));
	}

	@Override
	public AASDescriptor lookupShell(IIdentifier shellIdentifier) throws ProviderException {
		return this.observedRegistryService.lookupShell(shellIdentifier);
	}

	@Override
	public List<AASDescriptor> lookupAllShells() throws ProviderException {
		return this.observedRegistryService.lookupAllShells();
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodelsForShell(IIdentifier shellIdentifier) throws ProviderException {
		return this.observedRegistryService.lookupAllSubmodelsForShell(shellIdentifier);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		return this.observedRegistryService.lookupSubmodel(shellIdentifier, submodelIdentifier);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier submodelIdentifier) throws ProviderException {
		return this.observedRegistryService.lookupSubmodel(submodelIdentifier);
	}

	@Override
	public List<SubmodelDescriptor> lookupAllSubmodels() throws ProviderException {
		return this.observedRegistryService.lookupAllSubmodels();
	}

}
