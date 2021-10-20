/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.modeltype;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link ModelType} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestModelType {

	@Test
	public void testConstructor() {
		String type = "testType";
		ModelType modelType = new ModelType(type);
		assertEquals(type, modelType.getName());
	}
}
