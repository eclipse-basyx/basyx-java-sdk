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
