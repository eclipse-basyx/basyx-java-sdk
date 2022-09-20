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

/**
 * Parser for Payloads sent by MqttAASAggregator
 * 
 * @author fried
 *
 */
public class MqttAASAggregatorPayloadParser {
	private String payload;

	public MqttAASAggregatorPayloadParser(String payload) {
		this.payload = payload;
	}

	public String extractShellId() {
		return payload;
	}
}
