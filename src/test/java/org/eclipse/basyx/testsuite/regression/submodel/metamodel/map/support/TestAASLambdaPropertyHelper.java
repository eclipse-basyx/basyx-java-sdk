/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
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
		temperature.setIdShort("temperature");
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
