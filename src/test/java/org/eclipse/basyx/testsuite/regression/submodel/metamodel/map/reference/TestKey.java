/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
 * Tests constructor, setter and getter of {@link Key} for their
 * correctness
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
