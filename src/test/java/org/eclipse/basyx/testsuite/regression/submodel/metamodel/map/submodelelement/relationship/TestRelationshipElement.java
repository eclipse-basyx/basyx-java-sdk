/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.relationship;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link RelationshipElement} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestRelationshipElement {
	private static final Reference FIRST = new Reference(new Key(KeyElements.ASSET, true, "firstValue", IdentifierType.IRI));
	private static final Reference SECOND = new Reference(new Identifier(IdentifierType.CUSTOM, "secondId"), KeyElements.BLOB, false);
	
	private RelationshipElement relationshipElement;
	
	@Before
	public void buildRelationshipElement() {
		relationshipElement = new RelationshipElement(FIRST, SECOND);
	} 
	
	@Test
	public void testConstructor() {
		assertEquals(FIRST, relationshipElement.getFirst());
		assertEquals(SECOND, relationshipElement.getSecond());
	} 
	
	@Test
	public void testSetFirst() {
		Reference newFirst = new Reference(new Key(KeyElements.CAPABILITY, false, "newFirst", IdentifierType.IRI));
		relationshipElement.setFirst(newFirst);
		assertEquals(newFirst, relationshipElement.getFirst());
	} 

	@Test
	public void testSetSecond() {
		Reference newSecond = new Reference(new Key(KeyElements.CAPABILITY, false, "newFirst", IdentifierType.IRI));
		relationshipElement.setSecond(newSecond);
		assertEquals(newSecond, relationshipElement.getSecond());
	}
	
	@Test
	public void testGetValue() {
		RelationshipElementValue value = relationshipElement.getValue();
		assertEquals(FIRST.getKeys().get(0).getValue(), value.getFirst().getKeys().get(0).getValue());
		assertEquals(SECOND.getKeys().get(0).getValue(), value.getSecond().getKeys().get(0).getValue());
	}
	
}
