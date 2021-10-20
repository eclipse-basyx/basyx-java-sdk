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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link AnnotatedRelationshipElement} for correctness
 * 
 * @author conradi
 *
 */
public class TestAnnotatedRelationshipElement {
	private static final Reference FIRST = new Reference(new Key(KeyElements.ASSET, true, "firstValue", IdentifierType.IRI));
	private static final Reference SECOND = new Reference(new Identifier(IdentifierType.CUSTOM, "secondId"), KeyElements.BLOB, false);
	
	private AnnotatedRelationshipElement element;
	
	@Before
	public void buildElement() {
		element = new AnnotatedRelationshipElement("testId", FIRST, SECOND);
		Property property = new Property("PropertyId", 10);
		List<IDataElement> annotations = new ArrayList<>();
		annotations.add(property);
		element.setAnnotation(annotations);
	} 
	
	@Test
	public void testConstructor() {
		assertEquals(FIRST, element.getFirst());
		assertEquals(SECOND, element.getSecond());
	} 
	
	@Test
	public void testSetFirst() {
		Reference newFirst = new Reference(new Key(KeyElements.CAPABILITY, false, "newFirst", IdentifierType.IRI));
		element.setFirst(newFirst);
		assertEquals(newFirst, element.getFirst());
	} 

	@Test
	public void testSetSecond() {
		Reference newSecond = new Reference(new Key(KeyElements.CAPABILITY, false, "newFirst", IdentifierType.IRI));
		element.setSecond(newSecond);
		assertEquals(newSecond, element.getSecond());
	}
	
	@Test
	public void testGetValue() {
		RelationshipElementValue value = element.getValue();
		assertEquals(FIRST.getKeys().get(0).getValue(), value.getFirst().getKeys().get(0).getValue());
		assertEquals(SECOND.getKeys().get(0).getValue(), value.getSecond().getKeys().get(0).getValue());
	}
	
	@Test
	public void testGetAnnotations() {
		Collection<IDataElement> annotations = element.getValue().getAnnotations();
		assertEquals(1, annotations.size());
		
		// Check the element
		for (IDataElement element: annotations) {
			assertEquals("PropertyId", element.getIdShort());
		}
	}
}
