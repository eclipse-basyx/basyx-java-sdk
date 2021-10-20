/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.basyx.testsuite.regression.vab.modelprovider.SimpleVABElement;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Test;

/**
 * Removes type information similar to what a communication over VAB would do
 * @author rajashek
 *
 */
public class TestTypeDestroyer {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testTypeDestroyer() {
		SimpleVABElement sm = new SimpleVABElement();
		Map<String, Object> generic = TypeDestroyer.destroyType(sm);
		assertTrue(sm instanceof SimpleVABElement);
		assertFalse(generic instanceof SimpleVABElement);

		// Replace set with list as transmission over VAB would do
		((Map<String, Object>) sm.get("structure")).put("set", new ArrayList<>());
		assertEquals(generic, sm);
	}
}
