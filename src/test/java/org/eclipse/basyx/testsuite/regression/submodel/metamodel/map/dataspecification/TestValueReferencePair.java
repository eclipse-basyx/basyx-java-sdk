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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.dataspecification;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.ValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link ValueReferencePair} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestValueReferencePair {
	private static final String VALUE = "testValue";
	private static final IReference VALUE_ID = new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRDI));

	private ValueReferencePair valueReferencePair;

	@Before
	public void buildValueReferencePair() {
		valueReferencePair = new ValueReferencePair(VALUE, VALUE_ID);
	}

	@Test
	public void testConstructor() {
		assertEquals(VALUE, valueReferencePair.getValue());
		assertEquals(VALUE_ID, valueReferencePair.getValueId());
	}

	@Test
	public void testSetValue() {
		String newValue = "testValue1";
		valueReferencePair.setValue(newValue);
		assertEquals(newValue, valueReferencePair.getValue());
	}

	@Test
	public void testSetValueId() {
		IReference newValueId = new Reference(new Key(KeyElements.BLOB, false, "testValueNew", IdentifierType.IRI));
		valueReferencePair.setValueId(newValueId);
		assertEquals(newValueId, valueReferencePair.getValueId());
	}
}
