/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.testsuite.regression.extensions.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.basyx.extensions.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link BaSyxStorageAPI} abstract class
 * 
 * DISCLAIMER: Some data created by this Testsuite is explicitly NOT removed
 * from the persistence storage to ensure nothing important deleted by accident.
 * 
 * @author jungjan, fried
 *
 */
public abstract class BaSyxStorageAPISuite {

	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.CUSTOM, "testSubmodelIidentifier");
	protected static Submodel testType = new Submodel("testSubmodel", SUBMODEL_IDENTIFIER);
	private BaSyxStorageAPI<Submodel> storageAPI;

	protected abstract BaSyxStorageAPI<Submodel> getStorageAPI();

	@Before
	public void setUp() {
		storageAPI = getStorageAPI();
	}

	@After
	public void cleanUp() {
		storageAPI.delete(testType.getIdentification().getId());
	}

	@Test
	public void retrieve() {
		storageAPI.createOrUpdate(testType);
		Submodel actual = storageAPI.retrieve(testType.getIdentification().getId());
		assertEquals(testType, actual);
	}

	@Test
	public void update() {
		storageAPI.createOrUpdate(testType);
		testType.setIdShort("updated");
		Submodel actual = storageAPI.createOrUpdate(testType);

		assertEquals(testType, actual);
	}

	@Test
	public void delete() {
		System.out.println(testType);
		storageAPI.createOrUpdate(testType);
		assertTrue(storageAPI.delete(testType.getIdentification().getId()));
		Collection<Submodel> allElements = storageAPI.retrieveAll();
		assertFalse(allElements.contains(testType));
	}
}
