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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement.range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.junit.Test;

/**
 * Test for Range
 * 
 * @author conradi
 *
 */
public class TestRange {
	@Test
	public void getValue() {
		int MIN = 0;
		int MAX = 10;
		Range range = new Range(ValueType.Int32, MIN, MAX);
		assertEquals(MIN, range.getMin());
		assertEquals(MAX, range.getMax());

		RangeValue value = range.getValue();
		assertEquals(MIN, value.getMin());
		assertEquals(MAX, value.getMax());
	}
	
	@Test
	public void getNullValue() {
		Range rangeOpenUpperLimit = new Range(ValueType.String, "a", "");
		assertNull(rangeOpenUpperLimit.getMax());

		Range rangeOpenLowerLimit = new Range(ValueType.String, "  ", "z");
		assertNull(rangeOpenLowerLimit.getMin());
	}

}
