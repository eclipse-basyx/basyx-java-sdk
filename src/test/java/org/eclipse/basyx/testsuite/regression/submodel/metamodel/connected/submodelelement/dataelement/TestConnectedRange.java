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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.dataelement;

import static org.junit.Assert.assertEquals;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedRange;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedRange can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedRange {
	
	ConnectedRange connectedRange;
	Range range;
	
	@Before
	public void build() {
		range = new Range(ValueType.Integer, new Integer(1), new Integer(10));
		range.setIdShort("testIdShort");
		
		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(range);

		connectedRange = new ConnectedRange(elementProxy);
	}
	
	/**
	 * Tests if getValueType() returns the correct value
	 */
	@Test
	public void testGetValueType() {
		assertEquals(range.getValueType(), connectedRange.getValueType());
	}
	
	/**
	 * Tests if getMin() returns the correct value
	 */
	@Test
	public void testGetMin() {
		assertEquals(range.getMin(), connectedRange.getMin());
	}
	
	/**
	 * Tests if getMax() returns the correct value
	 */
	@Test
	public void testGetMax() {
		assertEquals(range.getMax(), connectedRange.getMax());
	}
	
	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		RangeValue rv = connectedRange.getValue();
		assertEquals(range.getMin(), rv.getMin());
		assertEquals(range.getMax(), rv.getMax());
	}
	
	/**
	 * Tests if setValue() sets the correct value.
	 */
	@Test
	public void testSetValue() {
		RangeValue value = new RangeValue(2, 8);
		
		connectedRange.setValue(value);
		
		assertEquals(2, connectedRange.getMin());
		assertEquals(8, connectedRange.getMax());
		assertEquals(range.getValueType(), connectedRange.getValueType());
	}
	
	@Test
	public void setValueUpdatesValueCorrectly() {
		triggerCachingOfSubmodelElement();

		RangeValue expected = new RangeValue(10, 20);
		
		connectedRange.setValue(expected);
		
		assertEquals(expected, connectedRange.getValue());
	}

	private void triggerCachingOfSubmodelElement() {
		connectedRange.getElem();
	}
}
