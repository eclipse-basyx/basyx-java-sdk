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

import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link LangString} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestLangString {
	private static final String LANGUAGE = "Eng";
	private static final String DESCRIPTION = "test";
	
	@Test
	public void testConstructor() {
		LangString langString = new LangString(LANGUAGE, DESCRIPTION);
		assertEquals(LANGUAGE, langString.getLanguage());
		assertEquals(DESCRIPTION, langString.getDescription());
	}
}
