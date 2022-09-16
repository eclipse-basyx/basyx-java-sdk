/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.mqtt;

import org.eclipse.basyx.extensions.shared.mqtt.PayloadParser;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Parser for Payloads sent by MqttAASRegistryService
 * 
 * @author fried
 *
 */
public class MqttAASRegistryServicePayloadParser extends PayloadParser {
	public MqttAASRegistryServicePayloadParser(String payload) {
		super(payload);
	}

	public String extractShellId() {
		if (this.payload.startsWith("(")) {
			return getShellIdFromPayload();
		}
		return payload;
	}

	public String extractSubmodelId() {
		if (!this.payload.startsWith("(")) {
			throw new ProviderException("The payload '" + payload + "' does not contain a Submodel ID");
		}
		return getSubmodelIdFromPayload();
	}

	private String getShellIdFromPayload() {
		return extractIds(payload)[0];
	}

	private String getSubmodelIdFromPayload() {
		return extractIds(payload)[1];
	}
}
