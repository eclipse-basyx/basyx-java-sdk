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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedContainerProperty can be created and used correctly
 * 
 * @author schnicke
 *
 */
public class TestConnectedSubmodelElementCollection {
	// String constants used in this test case
	private static final String PROP = "prop";
	private static final String OPERATION = "sum";

	ConnectedSubmodelElementCollection prop;

	@Before 
	public void build() {
		// Create PropertySingleValued containing the collection
		Property propertyMeta = new Property(4);
		propertyMeta.setIdShort(PROP);

		// Create operation
		Operation operation = new Operation(arr -> {
			return (int) arr[0] + (int) arr[1];
		});
		Property aProp = new Property("a", 1);
		aProp.setKind(ModelingKind.TEMPLATE);
		Property bProp = new Property("b", 2);
		bProp.setKind(ModelingKind.TEMPLATE);
		Property rProp = new Property("r", 3);
		rProp.setKind(ModelingKind.TEMPLATE);
		OperationVariable a = new OperationVariable(aProp);
		OperationVariable b = new OperationVariable(bProp);
		OperationVariable r = new OperationVariable(rProp);
		operation.setInputVariables(Arrays.asList(a, b));
		operation.setOutputVariables(Collections.singletonList(r));
		operation.setIdShort(OPERATION);

		// Create ComplexDataProperty containing the created operation and property
		SubmodelElementCollection complex = new SubmodelElementCollection("SubmodelCollectionId");
		complex.addSubmodelElement(propertyMeta);
		complex.addSubmodelElement(operation);
		complex.setIdShort("CollectionId");

		Submodel sm = new Submodel("submodelId", new ModelUrn("testUrn"));
		sm.addSubmodelElement(complex);

		Map<String, Object> destroyType = TypeDestroyer.destroyType(sm);
		// Create a dummy connection manager containing the created ContainerProperty map
		// The model is wrapped in the corresponding ModelProvider that implements the API access
		VABConnectionManagerStub manager = new VABConnectionManagerStub(
				new SubmodelProvider(new VABLambdaProvider(destroyType)));

		// Retrieve the ConnectedContainerProperty
		prop = new ConnectedSubmodelElementCollection(manager.connectToVABElement("").getDeepProxy("/submodel/submodelElements/" + complex.getIdShort()));
	}

	/**
	 * Tests retrieving a contained property
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProperty() throws Exception {
		// Get contained properties
		Map<String, IProperty> props = prop.getProperties();

		// Check number of properties
		assertEquals(1, props.size());

		// Retrieves collection property
		IProperty prop = props.get(PROP);

		// Check contained values
		assertEquals(4, prop.getValue());
	}

	/**
	 * Tests retrieving a contained operation
	 */
	@Test
	public void testOperation() throws Exception {
		// Get contained operations
		Map<String, IOperation> ops = prop.getOperations();

		// Check number of operations
		assertEquals(1, ops.size());

		// Retrieves operations
		IOperation sum = ops.get(OPERATION);

		// Check operation invocation
		assertEquals(5, sum.invokeSimple(2, 3));
	}
	
	@Test
	public void testSetValue() {
		Property property = new Property("testProperty");
		property.setIdShort(PROP);
		
		
		Collection<ISubmodelElement> newValue = new ArrayList<>();
		newValue.add(property);
		
		prop.setValue(newValue);
		
		Map<String, ISubmodelElement> value = prop.getSubmodelElements();
		IProperty property2 = (IProperty) value.get(PROP);
		
		assertEquals("testProperty", property2.getValue());
	}

	@Test
	public void testGetSubmodelElement() {
		ISubmodelElement element = prop.getSubmodelElement(PROP);
		assertEquals(PROP, element.getIdShort());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteSubmodelElement() {
		prop.deleteSubmodelElement(PROP);
		prop.getSubmodelElement(PROP);
	}
	
	@Test
	public void testAddSubmodelElement() {
		String newId = "abc";
		Property newProp = new Property(6);
		newProp.setIdShort(newId);
		prop.addSubmodelElement(newProp);
		ISubmodelElement element = prop.getSubmodelElement(newId);
		assertEquals(newId, element.getIdShort());
	}
	
	@Test
	public void testGetValues() {
		Map<String, Object> values = prop.getValues();
		assertEquals(1, values.size());
		assertTrue(values.containsKey(PROP));
		assertEquals(4, values.get(PROP));
		
		String newKey = "newKey";
		String newValue = "newValue";
		
		Property newProp = new Property(newKey, newValue);
		newProp.setValueType(ValueType.String);
		prop.addSubmodelElement(newProp);
		
		values = prop.getValues();
		assertEquals(2, values.size());
		assertTrue(values.containsKey(newKey));
		assertEquals(newValue, values.get(newKey));
	}
}
