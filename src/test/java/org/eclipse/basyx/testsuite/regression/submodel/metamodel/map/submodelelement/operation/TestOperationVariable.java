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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link OperationVariable} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestOperationVariable {
	private static final Property PROPERTY = new Property("testIdShort", "testOpVariable");
	
	private OperationVariable operationVariable;
	@Before
	public void buildOperationVariable() {
		PROPERTY.setKind(ModelingKind.TEMPLATE);
		operationVariable = new OperationVariable(PROPERTY);
	} 
	
	@Test
	public void testConstructor() {
		assertEquals(PROPERTY, operationVariable.getValue());
	} 
	
	@Test
	public void testSetValue() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setKind(ModelingKind.TEMPLATE);
		operationVariable.setValue(property);
		assertEquals(property, operationVariable.getValue());
	}
	
	@Test
	// TODO: Change with 1.0 Release when ModelingKind.Template is obligatory for OperationVariables
	public void testSetValueChangedModelingKind() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setKind(ModelingKind.INSTANCE);
		operationVariable.setValue(property);
		assertEquals(ModelingKind.TEMPLATE, operationVariable.getValue().getKind());
	}
}
