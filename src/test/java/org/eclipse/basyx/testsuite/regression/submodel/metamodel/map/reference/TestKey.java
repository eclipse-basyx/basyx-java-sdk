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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Key} for their correctness
 * 
 * @author haque
 *
 */
public class TestKey {
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final KeyType KEY_TYPE = KeyType.CUSTOM;
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;

	private Key key;

	@Before
	public void buildKey() {
		key = new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, KEY_TYPE);
	}

	@Test
	public void testConstructor1() {
		assertEquals(KEY_ELEMENTS, key.getType());
		assertTrue(!key.isLocal());
		assertEquals(VALUE, key.getValue());
		assertEquals(KEY_TYPE, key.getIdType());
	}

	@Test
	public void testConstructor2() {
		assertEquals(KEY_ELEMENTS, key.getType());
		assertTrue(!key.isLocal());
		assertEquals(VALUE, key.getValue());
		assertEquals(KeyType.fromString(ID_TYPE.toString()), key.getIdType());
	}

	@Test
	public void testIsLocal() {
		assertTrue(!key.isLocal());

	}

	@Test
	public void testSetType() {
		KeyElements type = KeyElements.ENTITY;
		key.setType(type);
		assertEquals(type, key.getType());
	}

	@Test
	public void testSetLocal() {
		key.setLocal(true);
		assertTrue(key.isLocal());
	}

	@Test
	public void testSetValue() {
		key.setValue(null);
		assertNull(key.getValue());

		String newValue = "test";
		key.setValue(newValue);
		assertEquals(newValue, key.getValue());
	}

	@Test
	public void testSetIdType() {
		KeyType type = KeyType.IRI;
		key.setIdType(type);
		assertEquals(type, key.getIdType());
	}

	@Test
	public void testIsKey() {
		assertTrue(Key.isKey(key));

		key.put(Key.IDTYPE, "nonsense");

		assertFalse(Key.isKey(key));
	}
}
