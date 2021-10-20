/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
