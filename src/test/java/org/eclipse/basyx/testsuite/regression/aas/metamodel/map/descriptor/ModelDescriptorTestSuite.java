/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
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
