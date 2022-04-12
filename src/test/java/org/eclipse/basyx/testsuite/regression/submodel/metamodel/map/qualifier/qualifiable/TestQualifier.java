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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier.qualifiable;

import static org.junit.Assert.assertEquals;
import java.util.LinkedHashMap;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Qualifier} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestQualifier {
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final String TYPE = "testType";
	private static final String VALUE_TYPE = "anyType";
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, VALUE);
	private static final Reference VALUE_ID = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	
	private Qualifier qualifier;
	
	@Before
	public void buildQualifier() {
		qualifier = new Qualifier(TYPE, VALUE, VALUE_TYPE, VALUE_ID);	
	}
	
	@Test
	public void testConstructor() {
		assertEquals(TYPE, qualifier.getType());
		assertEquals(VALUE, qualifier.getValue());
		assertEquals(ValueTypeHelper.fromName(VALUE_TYPE), qualifier.getValueType());
		assertEquals(VALUE_ID, qualifier.getValueId());
	}
	
	@Test
	public void testSetType() {
		String newTypeString = "newType";
		qualifier.setType(newTypeString);
		assertEquals(newTypeString, qualifier.getType());
	}
	
	@Test
	public void testSetValue() {
		String newValueString = "newValue";
		qualifier.setValue(newValueString);
		assertEquals(newValueString, qualifier.getValue());
	}
	
	@Test
	public void testSetValueId() {
		Reference reference = new Reference(new Identifier(IdentifierType.IRI, "newId"), KeyElements.BLOB, true);
		qualifier.setValueId(reference);
		assertEquals(reference, qualifier.getValueId());
	}
	
	@Test
	public void testSetValueType() {
		ValueType newValueTypeString = ValueType.AnyType;
		qualifier.setValueType(newValueTypeString);
		assertEquals(newValueTypeString, qualifier.getValueType());
	}
	
	// Tests if the valueType is correctly converted to a string
	@Test
	public void testSetValueCorrectValueType() {
		Qualifier emptyQualifier = new Qualifier();
		emptyQualifier.setValue("Test");
		assertEquals(ValueType.String.toString(), emptyQualifier.get(Qualifier.VALUETYPE));
	}

	@Test
	public void testSetSemanticID() {
		Reference reference = new Reference(new Identifier(IdentifierType.IRI, "newId"), KeyElements.BLOB, true);
		qualifier.setSemanticId(reference);
		assertEquals(reference, qualifier.getSemanticId());
	}
	
	@SuppressWarnings("unchecked")
    @Test
    public void testModelType() {
	    LinkedHashMap<String, Object> modelType = (LinkedHashMap<String, Object>)qualifier.get(ModelType.MODELTYPE);
        assertEquals(Qualifier.MODELTYPE, modelType.get(ModelType.NAME));
    }
}
