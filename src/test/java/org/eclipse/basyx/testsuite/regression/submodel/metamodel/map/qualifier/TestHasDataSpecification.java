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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link HasDataSpecification} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestHasDataSpecification {
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, VALUE);
	private static final Reference REFERENCE = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	
	private HasDataSpecification specification;
	
	@Before
	public void buildHasDataSpecification() {
		specification = new HasDataSpecification(new ArrayList<>(), Collections.singleton(REFERENCE));
	}
	
	@Test
	public void testConstructor() {
		Collection<IReference> references = Collections.singleton(REFERENCE);
		assertEquals(references, specification.getDataSpecificationReferences());
	}
	
	@Test
	public void testSetDataSpecificationReferences() {
		IdentifierType identifierType = IdentifierType.IRDI;
		String idString = "testId";
		Identifier identifier = new Identifier(identifierType, idString);
		KeyElements keyElements = KeyElements.BLOB;
		boolean isLocal = true;
		Reference reference = new Reference(identifier, keyElements, isLocal);
		Collection<IReference> references = Collections.singleton(reference);
		
		specification.setDataSpecificationReferences(references);
		assertEquals(references, specification.getDataSpecificationReferences());
	}
}
