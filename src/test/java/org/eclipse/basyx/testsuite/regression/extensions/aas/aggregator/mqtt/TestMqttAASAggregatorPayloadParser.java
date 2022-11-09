/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorHelper;
import org.eclipse.basyx.extensions.aas.aggregator.mqtt.MqttAASAggregatorPayloadParser;
import org.junit.Test;

/**
 * Tests the Payload Parser for Payloads sent by MqttAASAggregator
 * 
 * @author fried
 *
 */
public class TestMqttAASAggregatorPayloadParser {
	private static final String AAS_ID = "aas001";

	@Test
	public void checkAASPayloadIsCorrectlyParsed() {
		String payload = MqttAASAggregatorHelper.createAASChangedPayload(AAS_ID);
		MqttAASAggregatorPayloadParser parser = createPayloadParser(payload);
		assertEquals(parser.extractShellId(), AAS_ID);
	}


	private MqttAASAggregatorPayloadParser createPayloadParser(String payload) {
		MqttAASAggregatorPayloadParser parser = new MqttAASAggregatorPayloadParser(payload);
		return parser;
	}

}