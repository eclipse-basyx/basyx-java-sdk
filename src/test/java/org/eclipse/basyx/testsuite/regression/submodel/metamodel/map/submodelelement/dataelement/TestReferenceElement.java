/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link ReferenceElement} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestReferenceElement {
	private static final Reference REFERENCE = new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRI));
	
	private ReferenceElement referenceElement;
	
	@Before
	public void buildReferenceElement() {
		referenceElement = new ReferenceElement(REFERENCE);
	} 
	
	@Test
	public void testConstructor() {
		assertEquals(REFERENCE, referenceElement.getValue());
		assertEquals(ReferenceElement.MODELTYPE, referenceElement.getModelType());
	} 
	
	@Test
	public void testSetValue() {
		Reference newReference = new Reference(new Identifier(IdentifierType.IRI, "testId"), KeyElements.ASSET, true);
		referenceElement.setValue(newReference);
		assertEquals(newReference, referenceElement.getValue());
	} 
}
