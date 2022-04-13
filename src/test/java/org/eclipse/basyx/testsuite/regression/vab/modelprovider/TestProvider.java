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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider;

import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.junit.Test;

/**
 * Abstract test suite for testing CRUD-operations for different types of model
 * providers. The concrete test cases implement concrete VABConnectionManagers
 * that are tested
 * 
 * @author espen
 *
 */
public abstract class TestProvider {
	protected abstract VABConnectionManager getConnectionManager();

	@Test
	public void testMapCreateDelete() throws Exception {
		MapCreateDelete.test(getConnectionManager());
	}

	@Test
	public void testMapRead() {
		MapRead.test(getConnectionManager());
	}

	@Test
	public void testMapUpdate() {
		MapUpdate.test(getConnectionManager());
	}

	@Test
	public void testMapInvoke() {
		MapInvoke.test(getConnectionManager());
	}

	@Test
	public void testCollectionCreateDelete() throws Exception {
		TestCollectionProperty.testCreateDelete(getConnectionManager());
	}

	@Test
	public void testCollectionRead() {
		TestCollectionProperty.testRead(getConnectionManager());
	}

	@Test
	public void testCollectionUpdate() {
		TestCollectionProperty.testUpdate(getConnectionManager());
	}

	@Test
	public void testObjectTransfer() {
		MapUpdate.testPushAll(getConnectionManager());
	}

	@Test
	public void testHandlingException() {
		Exceptions.testHandlingException(getConnectionManager());
	}
}
