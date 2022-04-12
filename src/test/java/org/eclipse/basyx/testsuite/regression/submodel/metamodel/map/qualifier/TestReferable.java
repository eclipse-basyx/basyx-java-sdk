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
