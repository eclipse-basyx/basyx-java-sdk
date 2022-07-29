/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.PropertyProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.junit.Test;

public class TestPropertyProvider {

	@Test
	public void valueTypeNotOverwrittenIfPreviouslySet() {
		PropertyProvider provider = createPropertyProviderWithLongProperty();

		setNewValueWithBooleanDataType(provider);

		assertValueTypeDidNotChange(provider);
	}

	@Test
	public void valueIsConvertedCorrectly() {
		PropertyProvider provider = createPropertyProviderWithLongProperty();

		Object longExpected = provider.getValue(Property.VALUE);

		assertTrue(longExpected instanceof Long);
	}

	private void setNewValueWithBooleanDataType(PropertyProvider provider) {
		provider.setValue(Property.VALUE, true);
	}

	private void assertValueTypeDidNotChange(PropertyProvider provider) {
		assertEquals(ValueType.Int64, getPropertyFromProvider(provider).getValueType());
	}

	private PropertyProvider createPropertyProviderWithLongProperty() {
		Property prop = new Property("propIdShort", ValueType.Int64);
		prop.setValue(100000000);

		PropertyProvider provider = new PropertyProvider(new VABLambdaProvider(prop));
		return provider;
	}

	@SuppressWarnings("unchecked")
	private Property getPropertyFromProvider(PropertyProvider provider) {
		return Property.createAsFacade((Map<String, Object>) provider.getValue(""));
	}
}
