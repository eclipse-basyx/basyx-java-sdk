/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.registry.descriptor.parts;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Endpoint class
 *
 * @author fischer, fried
 *
 */
public class Endpoint extends VABModelMap<Object> {
	public static final String ENDPOINT_INTERFACE = "endpointInterface";
	public static final String PROTOCOL_INFORMATION = "protocolInformation";
	public static final String DEFAULT_INTERFACE = "AAS-1.0";

	private Endpoint() {
	}

	/**
	 * Creates an endpoint object from an interface and a protocolInformation object
	 *
	 * @param endpointInterface
	 * @param protocolInformation
	 */
	public Endpoint(String endpointInterface, ProtocolInformation protocolInformation) {
		setEndpointInterface(endpointInterface);
		setProtocolInformation(protocolInformation);
	}

	/**
	 * Creates an endpoint object from an interface and a endpointAddress. The
	 * endpointAddress is capsuled in a protcolInformation object.
	 *
	 * @param endpointInterface
	 * @param endpointAddress
	 */
	public Endpoint(String endpointInterface, String endpointAddress) {
		ProtocolInformation protocolInformation = new ProtocolInformation(endpointAddress);
		setEndpointInterface(endpointInterface);
		setProtocolInformation(protocolInformation);
	}

	/**
	 * Creates an endpoint object from an interface and a endpointAddress. The
	 * endpointAddress is capsuled in a protcolInformation object.
	 *
	 * @param endpointAddress
	 */
	public Endpoint(String endpointAddress) {
		ProtocolInformation protocolInformation = new ProtocolInformation(endpointAddress);
		setEndpointInterface(DEFAULT_INTERFACE);
		setProtocolInformation(protocolInformation);
	}

	/**
	 * Creates an endpoint object from a map
	 *
	 * @param obj
	 *            an endpoint object as raw map
	 * @return an endpoint object, that behaves like a facade for the given map
	 */
	public static Endpoint createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		Endpoint facade = new Endpoint();
		facade.setMap(obj);
		return facade;
	}

	public String getEndpointInterface() {
		return (String) get(ENDPOINT_INTERFACE);
	}

	public void setEndpointInterface(String endpointInterface) {
		put(ENDPOINT_INTERFACE, endpointInterface);
	}

	@SuppressWarnings("unchecked")
	public ProtocolInformation getProtocolInformation() {
		return ProtocolInformation.createAsFacade((Map<String, Object>) get(PROTOCOL_INFORMATION));
	}

	public void setProtocolInformation(ProtocolInformation protocolInformation) {
		put(PROTOCOL_INFORMATION, protocolInformation);
	}
}
