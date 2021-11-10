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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Test;


/**
 * Tests constructor, setter and getter of {@link Reference} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestReference {
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final KeyType KEY_TYPE = KeyType.IRDI;
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	
	@Test
	public void testConstructor1() {	
		Identifiable identifiable = new Identifiable("2.0", "5", "testIDShort", "testCategory", new LangStrings("Eng", "test"), IdentifierType.IRI, "newId");
		Collection<IKey> keys = Collections.singletonList(new Key(KEY_ELEMENTS, IS_LOCAL, identifiable.getIdentification().getId(), identifiable.getIdentification().getIdType()));
		Reference reference = new Reference(identifiable, KEY_ELEMENTS, IS_LOCAL);
		assertEquals(keys, reference.getKeys());
	}
	
	@Test
	public void testConstructor2() {
		Identifier identifier = new Identifier(ID_TYPE, VALUE);
		Reference reference = new Reference(identifier, KEY_ELEMENTS, IS_LOCAL);
		Collection<IKey> keys = Collections.singletonList(new Key(KEY_ELEMENTS, IS_LOCAL, identifier.getId(), identifier.getIdType()));
		assertEquals(keys, reference.getKeys());
	}
	
	@Test
	public void testConstructor3() {
		List<IKey> keysList = new ArrayList<IKey>();
		keysList.add(new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, ID_TYPE));
		keysList.add(new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, KEY_TYPE));
		
		Reference reference = new Reference(keysList);
		assertEquals(keysList, reference.getKeys());
	}
	
	@Test
	public void testConstructor4() {
		Key key = new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, ID_TYPE);
		
		Reference reference = new Reference(key);
		assertEquals(Collections.singletonList(key), reference.getKeys());
	}
	
	@Test
	public void testSetKeys() {
		List<IKey> keysList = new ArrayList<IKey>();
		keysList.add(new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, ID_TYPE));
		keysList.add(new Key(KEY_ELEMENTS, IS_LOCAL, VALUE, KEY_TYPE));
		
		Reference reference = new Reference(new Identifier(ID_TYPE, VALUE), KEY_ELEMENTS, IS_LOCAL);
 		reference.setKeys(keysList);
		assertEquals(keysList, reference.getKeys());
	}
	
	@Test
	public void testIsReference() {
		Identifiable identifiable = new Identifiable("2.0", "5", "testIDShort", "testCategory", new LangStrings("Eng", "test"), IdentifierType.IRI, "newId");
		Reference reference = new Reference(identifiable, KEY_ELEMENTS, IS_LOCAL);
		assertTrue(Reference.isReference(reference));
		assertTrue(Reference.isReference(TypeDestroyer.destroyType(reference)));
		reference.put(Reference.KEY, "nonsense");
		assertFalse(Reference.isReference(reference));
		assertFalse(Reference.isReference(TypeDestroyer.destroyType(reference)));
	}
}
