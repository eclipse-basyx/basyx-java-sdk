/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier.haskind;

import static org.junit.Assert.assertEquals;

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
		assertEquals(hasKind.getModelingKind(), MODELING_KIND); 
	}

	@Test
	public void testSetModelingKind() {
		ModelingKind newModelingKind = ModelingKind.TEMPLATE;
		hasKind.setModelingKind(newModelingKind);
		assertEquals(newModelingKind, hasKind.getModelingKind());
	}
}
