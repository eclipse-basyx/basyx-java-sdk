/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement.property.valuetype;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Tests the ValueTypeHelper class
 * 
 * @author schnicke
 *
 */
public class TestValueTypeHelper {

	@Test
	public void NonStandardUpperCaseHandling() {
		List<String> testList = List.of("integer", "Integer", "Float", "dateTime", "base64Binary", "dateTimeStamp",
				"INTEGER");
		List<String> correctList = List.of("integer", "integer", "float", "dateTime", "base64Binary", "dateTimeStamp",
				"integer");

		Iterator<String> testsIterator = testList.iterator();
		Iterator<String> correctsIterator = correctList.iterator();

		while (testsIterator.hasNext() && correctsIterator.hasNext()) {
			ValueType type = ValueTypeHelper.fromName(testsIterator.next());
			ValueType expected = ValueTypeHelper.fromName(correctsIterator.next());
			assertEquals(expected, type);

		}
	}

	@Test
	public void dateFromString() throws DatatypeConfigurationException {
		String date = "2002-09-24";
		Object javaObject = ValueTypeHelper.getJavaObject(date, ValueType.Date);

		XMLGregorianCalendar expected = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
		assertEquals(expected, javaObject);
	}

	@Test
	public void decimalDataType() {
		String data = "10.10";
		Object javaObject = ValueTypeHelper.getJavaObject(data, ValueType.Decimal);

		BigDecimal expected = new BigDecimal(data);
		assertEquals(expected, javaObject);
	}

	@Test
	public void decimalDataTypeNaN() {
		String data = "";
		Object javaObject = ValueTypeHelper.getJavaObject(data, ValueType.Decimal);

		BigDecimal expected = new BigDecimal("0");
		assertEquals(expected, javaObject);
	}
}
