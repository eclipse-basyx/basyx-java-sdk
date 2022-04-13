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

package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.descriptor;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.junit.Test;

/**
 * Test suite for Model Descriptor common method testing
 * 
 * @author haque
 *
 */
public abstract class ModelDescriptorTestSuite {
	private static final String TESTENDPOINT = "dummy.com";
	private static final String TESTENDPOINT2 = "dummy2.com";
	private ModelDescriptor descriptor;

	public abstract ModelDescriptor retrieveModelDescriptor();

	@Test
	public void testAddEndpoint() {
		addEndpoints();
		Collection<Map<String, Object>> endpoints = descriptor.getEndpoints();
		assertTrue(endpoints.stream().anyMatch(x -> x.get(AssetAdministrationShell.ADDRESS) != null && x.get(AssetAdministrationShell.ADDRESS).equals(TESTENDPOINT)));
		assertTrue(endpoints.stream().anyMatch(x -> x.get(AssetAdministrationShell.ADDRESS) != null && x.get(AssetAdministrationShell.ADDRESS).equals(TESTENDPOINT2)));
	}

	@Test
	public void testRemoveEndpoint() {
		addEndpoints();
		descriptor.removeEndpoint(TESTENDPOINT);
		Collection<Map<String, Object>> endpoints = descriptor.getEndpoints();
		assertTrue(endpoints.stream().noneMatch(x -> x.get(AssetAdministrationShell.ADDRESS) != null && x.get(AssetAdministrationShell.ADDRESS).equals(TESTENDPOINT)));
	}

	private void addEndpoints() {
		descriptor = retrieveModelDescriptor();
		descriptor.addEndpoint(TESTENDPOINT);
		descriptor.addEndpoint(TESTENDPOINT2);
	}
}
