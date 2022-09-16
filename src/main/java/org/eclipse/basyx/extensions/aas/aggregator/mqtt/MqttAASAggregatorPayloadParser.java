/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.mqtt;

import org.eclipse.basyx.extensions.shared.mqtt.PayloadParser;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Parser for Payloads sent by MqttAASAggregator
 * 
 * @author fried
 *
 */
public class MqttAASAggregatorPayloadParser extends PayloadParser {

	public MqttAASAggregatorPayloadParser(String payload) {
		super(payload);
	}

	public String extractShellId() {
		if (payload.startsWith("(")) {
			throw new ProviderException("The payload '" + payload + "' is invalid for Payloads sent by MqttAASAggregator");
		}
		return payload;
	}

}
