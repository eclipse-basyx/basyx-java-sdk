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

import static org.eclipse.basyx.extensions.shared.mqtt.PayloadParserHelper.extractIds;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Parser for Payloads sent by MqttAASRegistryService
 * 
 * @author fried
 *
 */
public class MqttAASRegistryServicePayloadParser {
	private String payload;
	private String submodelId;
	private String shellId;

	public MqttAASRegistryServicePayloadParser(String payload) {
		this.payload = payload;
		if (this.payload.startsWith("(")) {
			this.shellId = getShellIdFromPayload();
			this.submodelId = getSubmodelIdFromPayload();
			return;
		}
		this.shellId = payload;
	}

	public String extractShellId() {
		return this.shellId;
	}

	public String extractSubmodelId() {
		if (!this.payload.startsWith("(")) {
			throw new ProviderException("The payload '" + payload + "' does not contain a Submodel ID");
		}
		return this.submodelId;
	}

	private String getShellIdFromPayload() {
		return extractIds(payload)[0];
	}

	private String getSubmodelIdFromPayload() {
		return extractIds(payload)[1];
	}
}
