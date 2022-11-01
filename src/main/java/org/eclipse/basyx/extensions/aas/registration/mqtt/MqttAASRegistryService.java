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
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.shared.mqtt.MqttEventService;
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
public class MqttAASRegistryService extends MqttEventService implements IAASRegistry {
	private static Logger logger = LoggerFactory.getLogger(MqttAASRegistryService.class);

	// The underlying AASRegistryService
	protected IAASRegistry observedRegistryService;

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
	public MqttAASRegistryService(IAASRegistry observedRegistryService, String serverEndpoint, String clientId) throws MqttException {
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
	public MqttAASRegistryService(IAASRegistry observedRegistryService, String serverEndpoint, String clientId, String user, char[] pw) throws MqttException {
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
	public MqttAASRegistryService(IAASRegistry observedRegistryService, MqttClient client) throws MqttException {
		super(client);
		logger.info("Create new MQTT AAS Registry Service for endpoint " + client.getServerURI());
		this.observedRegistryService = observedRegistryService;
	}

	@Override
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException {
		this.observedRegistryService.register(deviceAASDescriptor);
		sendMqttMessage(MqttAASRegistryHelper.TOPIC_REGISTERAAS, deviceAASDescriptor.getIdentifier().getId());
	}

	@Override
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException {
		this.observedRegistryService.register(aas, smDescriptor);
		sendMqttMessage(MqttAASRegistryHelper.TOPIC_REGISTERSUBMODEL, MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(aas.getId(), smDescriptor.getIdentifier().getId()));
	}

	@Override
	public void delete(IIdentifier aasId) throws ProviderException {
		this.observedRegistryService.delete(aasId);
		sendMqttMessage(MqttAASRegistryHelper.TOPIC_DELETEAAS, aasId.getId());
	}

	@Override
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		this.observedRegistryService.delete(aasId, smId);
		sendMqttMessage(MqttAASRegistryHelper.TOPIC_DELETESUBMODEL, MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(aasId.getId(), smId.getId()));
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException {
		return this.observedRegistryService.lookupAAS(aasId);
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		return this.observedRegistryService.lookupAll();
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException {
		return this.observedRegistryService.lookupSubmodels(aasId);
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException {
		return this.observedRegistryService.lookupSubmodel(aasId, smId);
	}
}
