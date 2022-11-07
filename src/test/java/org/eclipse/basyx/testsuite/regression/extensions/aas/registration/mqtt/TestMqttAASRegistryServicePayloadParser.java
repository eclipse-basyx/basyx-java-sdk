/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.mqtt;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryHelper;
import org.eclipse.basyx.extensions.aas.registration.mqtt.MqttAASRegistryServicePayloadParser;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.junit.Test;

/**
 * Tests the Payload Parser for Payloads sent by MqttAASRegistryService
 * 
 * @author fried
 *
 */
public class TestMqttAASRegistryServicePayloadParser {
	private static final String AAS_ID = "aas001";
	private static final String SUBMODEL_ID = "submodel001";
	private static IIdentifier aasId = new CustomId(AAS_ID);
	private static IIdentifier submodelId = new CustomId(SUBMODEL_ID);

	@Test
	public void registeredAASPayloadIsCorrectlyParsed() {
		String payload = MqttAASRegistryHelper.createRegisteredAASChangedPayload(AAS_ID);
		MqttAASRegistryServicePayloadParser parser = createPayloadParser(payload);

		assertEquals(parser.extractShellId(), AAS_ID);
	}

	@Test
	public void registeredSubmodelPayloadIsCorrectlyParsed() {
		String payload = MqttAASRegistryHelper.createSubmodelDescriptorOfAASChangedPayload(aasId, submodelId);

		MqttAASRegistryServicePayloadParser parser = createPayloadParser(payload);
		assertEquals(parser.extractShellId(), AAS_ID);
		assertEquals(parser.extractSubmodelId(), SUBMODEL_ID);
	}


	private MqttAASRegistryServicePayloadParser createPayloadParser(String payload) {
		return new MqttAASRegistryServicePayloadParser(payload);
	}
}