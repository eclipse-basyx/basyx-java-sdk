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
