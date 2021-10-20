/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement.range;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for Range
 * 
 * @author conradi
 *
 */
public class TestRange {

	private static final int MIN = 0;
	private static final int MAX = 10;
	private Range range;
	
	@Before
	public void buildRange() {
		range = new Range(ValueType.Integer, MIN, MAX);
	}
	
	@Test
	public void testGetValue() {
		assertEquals(MIN, range.getMin());
		assertEquals(MAX, range.getMax());
		
		RangeValue value = range.getValue();
		assertEquals(MIN, value.getMin());
		assertEquals(MAX, value.getMax());
	} 
	
}
