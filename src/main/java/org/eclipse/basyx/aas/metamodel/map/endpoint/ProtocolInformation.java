/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.aas.metamodel.map.endpoint;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * ProtocolInformation class
 *
 * @author fischer, fried
 *
 */
public class ProtocolInformation extends VABModelMap<Object> {
	public static final String ENDPOINT_ADDRESS = "endpointAddress";
	public static final String ENDPOINT_PROTOCOL = "endpointProtocol";
	public static final String ENDPOINT_PROTOCOL_VERSION = "endpointProtocolVersion";
	public static final String SUBPROTOCOL = "subprotocol";
	public static final String SUBPROTOCOL_BODY = "subprotocolBody";
	public static final String SUBPROTOCOL_BODY_ENCODING = "subprotocolBodyEncoding";

	private ProtocolInformation() {
	}

	/**
	 * Constructor accepting only an address. Other parameters are null.
	 *
	 * @param endpointAddress
	 */
	public ProtocolInformation(String endpointAddress) {
		setEndpointAddress(endpointAddress);
	}

	/**
	 * Constructor for all parameters of protocolInformation.
	 *
	 * @param endpointAddress
	 * @param endpointProtocol
	 * @param endpointProtocolVersion
	 * @param subprotocol
	 * @param subprotocolBody
	 * @param subprotocolBodyEncoding
	 */
	public ProtocolInformation(String endpointAddress, String endpointProtocol, String endpointProtocolVersion, String subprotocol, String subprotocolBody, String subprotocolBodyEncoding) {
		setEndpointAddress(endpointAddress);
		setEndpointProtocol(endpointProtocol);
		setEndpointProtocolVersion(endpointProtocolVersion);
		setSubprotocol(subprotocol);
		setSubprotocolBody(subprotocolBody);
		setSubprotocolBodyEncoding(subprotocolBodyEncoding);
	}

	/**
	 * Creates a protocolInformation object from a map
	 *
	 * @param obj
	 *            a protocolInformation object as raw map
	 * @return a protocolInformation object, that behaves like a facade for the
	 *         given map
	 */
	public static ProtocolInformation createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		ProtocolInformation facade = new ProtocolInformation();
		facade.setMap(obj);
		return facade;
	}

	public String getEndpointAddress() {
		return (String) get(ENDPOINT_ADDRESS);
	}

	public void setEndpointAddress(String endpointAddress) {
		// TODO: check if string is not empty
		put(ENDPOINT_ADDRESS, endpointAddress);
	}

	public String getEndpointProtocol() {
		return (String) get(ENDPOINT_PROTOCOL);
	}

	public void setEndpointProtocol(String endpointProtocol) {
		put(ENDPOINT_PROTOCOL, endpointProtocol);
	}

	public String getEndpointProtocolVersion() {
		return (String) get(ENDPOINT_PROTOCOL_VERSION);
	}

	public void setEndpointProtocolVersion(String endpointProtocolVersion) {
		put(ENDPOINT_PROTOCOL_VERSION, endpointProtocolVersion);
	}

	public String getSubprotocol() {
		return (String) get(SUBPROTOCOL);
	}

	public void setSubprotocol(String subprotocol) {
		put(SUBPROTOCOL, subprotocol);
	}

	public String getSubprotocolBody() {
		return (String) get(SUBPROTOCOL_BODY);
	}

	public void setSubprotocolBody(String subprotocolBody) {
		put(SUBPROTOCOL_BODY, subprotocolBody);
	}

	public String getSubprotocolBodyEncoding() {
		return (String) get(SUBPROTOCOL_BODY_ENCODING);
	}

	public void setSubprotocolBodyEncoding(String subprotocolBodyEncoding) {
		put(SUBPROTOCOL_BODY_ENCODING, subprotocolBodyEncoding);
	}
}
