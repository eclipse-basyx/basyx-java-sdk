/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.support;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.junit.Test;

/**
 * Ensures correct behaviour of {@link AASLambdaPropertyHelper}
 * 
 * @author schnicke
 *
 */
public class TestAASLambdaPropertyHelper {

	// Used as data container for lambda property
	private double testValue;

	@Test
	public void testSettingLambdaValue() throws Exception {
		// Build property
		Property temperature = new Property();
		AASLambdaPropertyHelper.setLambdaValue(temperature, () -> testValue, v -> {
			testValue = (double) v;
		});
		
		// Wrap in provider
		SubmodelElementProvider provider = new SubmodelElementProvider(new VABLambdaProvider(temperature));
		ConnectedProperty connectedProperty = new ConnectedProperty(new VABElementProxy("", provider));

		// Check correct property type
		ValueType expectedType = ValueType.Double;
		assertEquals(expectedType, connectedProperty.getValueType());
		
		// Check value is correctly retrievable by property
		testValue = 10;
		assertEquals(testValue, connectedProperty.getValue());
		
		// Check value is correctly written by property
		double expectedValue = 2.1;
		connectedProperty.setValue(expectedValue);
		assertEquals(expectedValue, connectedProperty.getValue());
		assertEquals(expectedValue, testValue, 0);
	}
}
