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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier.haskind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link HasKind} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestHasKind {
	private static final ModelingKind MODELING_KIND = ModelingKind.INSTANCE;
	
	private HasKind hasKind;
	
	@Before
	public void buildHasKind() {
		hasKind = new HasKind(MODELING_KIND);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(hasKind.getKind(), MODELING_KIND); 
	}

	@Test
	public void testSetModelingKind() {
		ModelingKind newModelingKind = ModelingKind.TEMPLATE;
		hasKind.setKind(newModelingKind);
		assertEquals(newModelingKind, hasKind.getKind());
	}
	
	@Test
	public void testSetModelingKindOfNull() {
		// Explicitly set null
		hasKind.setKind(null);
		assertNull(hasKind.getKind());
	}
}
