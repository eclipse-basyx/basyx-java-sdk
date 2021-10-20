/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
