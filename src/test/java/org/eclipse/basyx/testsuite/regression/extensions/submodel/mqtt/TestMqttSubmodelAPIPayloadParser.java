/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIHelper;
import org.eclipse.basyx.extensions.submodel.mqtt.MqttSubmodelAPIPayloadParser;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

/**
 * Tests the Payload Parser for Payloads sent by MqttSubmodelAPI
 * 
 * @author fried
 *
 */
public class TestMqttSubmodelAPIPayloadParser {
	private static final String AAS_ID = "aas001";
	private static final String SUBMODEL_ID = "submodel001";
	private static final String SUBMODEL_ELEMENT_ID = "submodelElement001";


	@Test
	public void createdSubmodelPayloadIsCorrectlyParsed() throws MqttException {
		String payload = MqttSubmodelAPIHelper.createChangedSubmodelPayload(SUBMODEL_ID);
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(payload);
		assertEquals(parser.extractSubmodelId(), SUBMODEL_ID);
	}

	@Test
	public void addedSubmodelElementPayloadIsCorrectlyParsed() throws MqttException {
		String payload = MqttSubmodelAPIHelper.createChangedSubmodelElementPayload(AAS_ID, SUBMODEL_ID, SUBMODEL_ELEMENT_ID);
		MqttSubmodelAPIPayloadParser parser = createPayloadParser(payload);

		assertEquals(parser.extractShellId(), AAS_ID);
		assertEquals(parser.extractSubmodelId(), SUBMODEL_ID);
		assertEquals(parser.extractSubmodelElementIdShort(), SUBMODEL_ELEMENT_ID);
	}

	private MqttSubmodelAPIPayloadParser createPayloadParser(String payload) {
		return new MqttSubmodelAPIPayloadParser(payload);
	}
}