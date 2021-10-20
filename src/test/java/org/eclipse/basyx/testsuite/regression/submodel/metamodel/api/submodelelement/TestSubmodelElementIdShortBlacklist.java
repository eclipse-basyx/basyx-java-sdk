/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.api.submodelelement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.SubmodelElementIdShortBlacklist;
import org.junit.Test;

/**
 * Tests if the SubmodelElementIdShortBlacklist works as intended
 * 
 * @author schnicke
 *
 */
public class TestSubmodelElementIdShortBlacklist {

	@Test
	public void testIsBlacklisted() {
		String allowed[] = { "test", "values", "invocations" };
		
		for (String s : SubmodelElementIdShortBlacklist.BLACKLIST) {
			assertTrue(s + " was incorrectly allowed", SubmodelElementIdShortBlacklist.isBlacklisted(s));
		}

		for (String s : allowed) {
			assertFalse(s + " was incorrectly blacklisted", SubmodelElementIdShortBlacklist.isBlacklisted(s));
		}
	}
}
