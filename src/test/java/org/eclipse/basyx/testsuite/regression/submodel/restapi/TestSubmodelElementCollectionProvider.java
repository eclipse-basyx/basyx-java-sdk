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

package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelElementCollectionProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.junit.Before;
import org.junit.Test;

public class TestSubmodelElementCollectionProvider {
	private static final String PROP_ID1 = "prop1";
	private static final String PROP_VALUE1 = "value1";
	private static final String PROP_ID2 = "prop2";
	private static final String PROP_VALUE2 = "value2";
	private static final String OP_ID1 = "op1";
	private static final String COL_ID1 = "col1";
	
	private static SubmodelElementCollectionProvider colProvider;
	
	@Before
	public void setup() {
		Property prop1 = new Property(PROP_ID1, PROP_VALUE1);
		prop1.setModelingKind(ModelingKind.TEMPLATE);
		prop1.setValueType(ValueType.String);
		
		Property prop2 = new Property(PROP_ID2, PROP_VALUE2);
		prop2.setModelingKind(ModelingKind.TEMPLATE);
		prop2.setValueType(ValueType.String);
		
		Operation op = new Operation(OP_ID1);
		
		SubmodelElementCollection smCol = new SubmodelElementCollection(COL_ID1);
		smCol.addSubmodelElement(prop1);
		smCol.addSubmodelElement(prop2);
		smCol.addSubmodelElement(op);
		
		colProvider = new SubmodelElementCollectionProvider(new VABLambdaProvider(smCol));		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValuesByEmptyPath() {
		Map<String, Object> values = (Map<String, Object>) colProvider.getValue("");
		SubmodelElementCollection retrievedCol = SubmodelElementCollection.createAsFacade(values);
		assertEquals(COL_ID1, retrievedCol.getIdShort());
		assertEquals(1, retrievedCol.getOperations().size());
		assertEquals(2, retrievedCol.getProperties().size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValuesByValuesCall() {
		Map<String, Object> values = (Map<String, Object>) colProvider.getValue("/" + SubmodelProvider.VALUES + "/");
		assertEquals(2, values.size());
		assertTrue(values.containsKey(PROP_ID1));
		assertTrue(values.containsKey(PROP_ID2));
		assertEquals(PROP_VALUE1, values.get(PROP_ID1));
		assertEquals(PROP_VALUE2, values.get(PROP_ID2));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValuesByValueCall() {
		Collection<Map<String, Object>> colElements = (Collection<Map<String, Object>>) colProvider.getValue("/" + MultiSubmodelElementProvider.VALUE + "/");
		assertEquals(3, colElements.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValuesByIdShortCall() {
		Map<String, Object> elemMap = (Map<String, Object>) colProvider.getValue("/" + PROP_ID1);
		Property propElem1 = Property.createAsFacade(elemMap);
		assertEquals(PROP_ID1, propElem1.getIdShort());
		assertEquals(PROP_VALUE1, propElem1.getValue());
		
		elemMap = (Map<String, Object>) colProvider.getValue("/" + PROP_ID2);
		Property propElem2 = Property.createAsFacade(elemMap);
		assertEquals(PROP_ID2, propElem2.getIdShort());
		assertEquals(PROP_VALUE2, propElem2.getValue());
		
		elemMap = (Map<String, Object>) colProvider.getValue("/" + OP_ID1);
		Operation opElem = Operation.createAsFacade(elemMap);
		assertEquals(OP_ID1, opElem.getIdShort());	
	}
}
