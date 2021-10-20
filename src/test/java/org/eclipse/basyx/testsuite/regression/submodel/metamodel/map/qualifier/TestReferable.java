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
import static org.junit.Assert.assertNull;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Referable} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestReferable {
	private static final String CATE_STRING = "testCategory";
	private static final String ID_SHORT_STRING = "testIdShort";
	private static final LangStrings DESCRIPTION = new LangStrings("Eng", "test");
	
	private Referable referable;
	
	@Before
	public void buildReferable() {
		referable = new Referable(ID_SHORT_STRING, CATE_STRING, DESCRIPTION);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(CATE_STRING, referable.getCategory());
		assertEquals(ID_SHORT_STRING, referable.getIdShort());
		assertEquals(DESCRIPTION, referable.getDescription());
		assertNull(referable.getParent());
	}
	
	@Test
	public void testSetIdShort() {
		String newIdString = "newId";
		referable.setIdShort(newIdString);
		assertEquals(newIdString, referable.getIdShort());
	}
	
	@Test
	public void testSetCategory() {
		String newCategoryString = "newCategory";
		referable.setCategory(newCategoryString);
		assertEquals(newCategoryString, referable.getCategory());
	}
	
	@Test
	public void testSetDescription() {
		LangStrings newDescriptionString = new LangStrings("DE", "newTest");
		referable.setDescription(newDescriptionString);
		assertEquals(newDescriptionString, referable.getDescription());
	}
	
	@Test
	public void testSetParent() {
		Reference parent = new Reference(new Identifier(IdentifierType.IRDI, "testNewId"), KeyElements.ASSET, true);
		referable.setParent(parent);
		assertEquals(parent, referable.getParent());
	}
}
