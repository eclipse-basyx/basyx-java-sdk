/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.mqtt;

import org.eclipse.basyx.extensions.shared.mqtt.PayloadParser;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Parser for Payloads sent by MqttSubmodelAPI
 * 
 * @author fried
 *
 */
public class MqttSubmodelAPIPayloadParser extends PayloadParser {

	public MqttSubmodelAPIPayloadParser(String payload) {
		super(payload);
	}

	public String extractSubmodelId() {
		if (!this.payload.startsWith("("))
			return payload;
		return getSubmodelIdFromPayload();
	}

	public String extractShellId() {
		if (!this.payload.startsWith("(")) {
			throw new ProviderException("Payload '" + payload + "' does not conatin a shellId.");
		}
		return getShellIdFromPayload();
	}

	public String extractSubmodelElementIdShort() {
		if (!this.payload.startsWith("(")) {
			throw new ProviderException("Payload '" + payload + "' does not conatin a Submodel Element ID short.");
		}
		return getSubmodelElementIdShortFromPayload();
	}

	private String getShellIdFromPayload() {
		return extractIds(payload)[0];
	}

	private String getSubmodelIdFromPayload() {
		return extractIds(payload)[1];
	}

	private String getSubmodelElementIdShortFromPayload() {
		return extractIds(payload)[2];
	}

}