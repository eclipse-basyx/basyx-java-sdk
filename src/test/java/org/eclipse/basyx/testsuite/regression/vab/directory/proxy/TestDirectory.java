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
package org.eclipse.basyx.testsuite.regression.vab.directory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for a registry. All registry provider implementations have
 * to pass these tests.
 * 
 * @author espen, schnicke
 * 
 */
public abstract class TestDirectory {
	// The registry proxy that is used to access the backend
	protected final IVABRegistryService registry = getRegistry();

	// Ids and endpoints for registered elements
	protected String elem1 = "elem1";
	protected String elem2 = "elem2";
	protected String elem3 = "elem3";
	protected String endpoint1 = "http://www.registrytest.de/elem1";
	protected String endpoint2 = "http://www.registrytest.de/elem2";
	protected String endpoint3 = "http://www.registrytest.de/elem3";

	/**
	 * Getter for the tested registry provider. Tests for actual registry provider
	 * have to realize this method.
	 */
	protected abstract IVABRegistryService getRegistry();

	/**
	 * During setup of the tests, new entries are created in the registry using a
	 * proxy
	 */
	@Before
	public void setUp() {
		// Register Elements
		registry.addMapping(elem1, endpoint1);
		registry.addMapping(elem2, endpoint2);
	}

	/**
	 * Remove registry entries after each test
	 */
	@After
	public void tearDown() {
		registry.removeMapping(elem1);
		registry.removeMapping(elem2);
	}

	/**
	 * Tests getting single entries from the registry and validates the result.
	 */
	@Test
	public void testGetSingleElement() {
		// Retrieve and check the first Element
		String result = registry.lookup(elem1);
		assertEquals(endpoint1, result);


		// Retrieve and check the second AAS
		result = registry.lookup(elem2);
		assertEquals(endpoint2, result);
	}

	/**
	 * Tests deletion for element entries
	 */
	@Test
	public void testDeleteCall() {
		// After the setup, both Elements should have been inserted to the registry
		assertNotNull(registry.lookup(elem1));
		assertNotNull(registry.lookup(elem2));

		registry.removeMapping(elem2);

		// After elem2 has been deleted, only elem1 should be registered
		assertNotNull(registry.lookup(elem1));
		try {
			registry.lookup(elem2);
			fail();
		} catch (ResourceNotFoundException e) {
			// Expected
		}

		registry.removeMapping(elem1);

		// After elem2 has been deleted, no element should be registered
		try {
			registry.lookup(elem1);
			fail();
		} catch (ResourceNotFoundException e) {
			// Expected
		}
		try {
			registry.lookup(elem2);
			fail();
		} catch (ResourceNotFoundException e) {
			// Expected
		}
	}
}
