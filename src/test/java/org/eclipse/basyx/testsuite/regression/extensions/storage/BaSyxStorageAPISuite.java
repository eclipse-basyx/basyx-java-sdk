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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.basyx.extensions.storage.BaSyxStorageAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link BaSyxStorageAPI} abstract class
 * 
 * DISCLAIMER: Some data created by this Testsuite is explicitly NOT removed
 * from the persistence storage to ensure nothing important deleted by accident.
 * 
 * @author jungjan
 *
 */
public abstract class BaSyxStorageAPISuite {

	protected static VABTestType testType;
	private BaSyxStorageAPI<VABTestType> storageAPI;

	protected abstract BaSyxStorageAPI<VABTestType> getStorageAPI();

	@BeforeClass
	public static void setUpClass() {
		testType = new VABTestType(VABTestType.DEFAULT_ID, VABTestType.DEFAULT_STR, VABTestType.DEFAULT_INT, VABTestType.DEFAULT_ARRAY, VABTestType.DEFAULT_COLLECTION, VABTestType.DEFAULT_MAP);
	}

	@Before
	public void setUp() {
		storageAPI = getStorageAPI();
	}

	@After
	public void cleanUp() {
		storageAPI.delete(testType.getIdentification().getId());
	}

	@Test
	public void createOrUpdate() {
		assertCreate(testType);
		VABTestType beforeActual = storageAPI.retrieve(testType.getIdentification().getId(), testType.getClass());
		VABTestType afterExpected = invertTestTypeIntValue(beforeActual);
		VABTestType afterActual = storageAPI.createOrUpdate(afterExpected);

		assertNotEquals(beforeActual, afterActual);
		assertEquals(afterExpected, afterActual);
	}

	private void assertCreate(VABTestType testType) {
		VABTestType result = storageAPI.createOrUpdate(testType);
		assertEquals(testType, result);
	}

	@Test
	public void update() {
		storageAPI.createOrUpdate(testType);
		VABTestType beforeActual = storageAPI.retrieve(testType.getIdentification().getId(), testType.getClass());
		VABTestType afterExpected = invertTestTypeIntValue(beforeActual);
		VABTestType afterActual = storageAPI.update(afterExpected, testType.getIdentification().getId());

		assertNotEquals(beforeActual, afterActual);
		assertEquals(afterExpected, afterActual);
	}

	private VABTestType invertTestTypeIntValue(VABTestType beforeActual) {
		VABTestType afterExpected = new VABTestType(beforeActual);
		afterExpected.setTestInt(-testType.getTestInt());
		return afterExpected;
	}

	@Test
	public void retrieve() {
		VABTestType expected = testType;
		storageAPI.createOrUpdate(testType);

		VABTestType actual = storageAPI.retrieve(testType.getIdentification().getId(), testType.getClass());

		assertEquals(expected, actual);
	}

	@Test
	public void retrieveAll() {
		VABTestType entry1 = new VABTestType(testType);
		VABTestType entry2 = new VABTestType(testType);

		entry1.setTestStr("1");
		entry1.setTestStr("2");

		storageAPI.createOrUpdate(entry1);
		storageAPI.createOrUpdate(entry2);

		Collection<VABTestType> retrieved = storageAPI.retrieveAll(testType.getClass());

		assertTrue(retrieved.contains(entry1));
		assertTrue(retrieved.contains(entry2));

		cleanRetrieveAll(retrieved);
	}

	private void cleanRetrieveAll(Collection<VABTestType> retrieved) {
		retrieved.forEach(entry -> storageAPI.delete(entry.getIdentification().getId()));
	}

	@Test
	public void delete() {
		storageAPI.createOrUpdate(testType);
		VABTestType there = storageAPI.retrieve(testType.getIdentification().getId(), testType.getClass());
		assertNotNull(there);
		assertTrue(storageAPI.delete(testType.getIdentification().getId()));
		VABTestType gone = storageAPI.retrieve(testType.getIdentification().getId(), testType.getClass());
		assertNull(gone);
	}
}
