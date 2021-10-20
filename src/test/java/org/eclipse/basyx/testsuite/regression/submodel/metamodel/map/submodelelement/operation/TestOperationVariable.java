/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		PROPERTY.setModelingKind(ModelingKind.TEMPLATE);
		operationVariable = new OperationVariable(PROPERTY);
	} 
	
	@Test
	public void testConstructor() {
		assertEquals(PROPERTY, operationVariable.getValue());
	} 
	
	@Test
	public void testSetValue() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setModelingKind(ModelingKind.TEMPLATE);
		operationVariable.setValue(property);
		assertEquals(property, operationVariable.getValue());
	}
	
	@Test
	// TODO: Change with 1.0 Release when ModelingKind.Template is obligatory for OperationVariables
	public void testSetValueChangedModelingKind() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setModelingKind(ModelingKind.INSTANCE);
		operationVariable.setValue(property);
		assertEquals(ModelingKind.TEMPLATE, operationVariable.getValue().getModelingKind());
	}
}
