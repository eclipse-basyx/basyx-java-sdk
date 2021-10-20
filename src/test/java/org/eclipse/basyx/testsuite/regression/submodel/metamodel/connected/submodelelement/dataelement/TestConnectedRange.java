/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.dataelement;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedRange;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;
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
		VABConnectionManagerStub manager = new VABConnectionManagerStub(
				new SubmodelElementProvider(new TypeDestroyingProvider(new VABLambdaProvider(range))));

		connectedRange = new ConnectedRange(manager.connectToVABElement(""));
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
}
