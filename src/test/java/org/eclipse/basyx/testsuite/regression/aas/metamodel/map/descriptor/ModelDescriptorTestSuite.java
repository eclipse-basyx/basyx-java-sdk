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

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
import org.eclipse.basyx.aas.metamodel.map.endpoint.ProtocolInformation;
import org.junit.Test;

/**
 * Test suite for Model Descriptor common method testing
 * 
 * @author haque, fischer, fried
 *
 */
public abstract class ModelDescriptorTestSuite {
	public final String ENDPOINT_INTERFACE = "testInterface";
	private static final String TESTENDPOINT = "dummy.com";
	private static final String TESTENDPOINT2 = "dummy2.com";
	private ModelDescriptor descriptor;

	public abstract ModelDescriptor retrieveModelDescriptor();

	@Test
	public void testAddEndpoint() {
		addEndpoints();
		Collection<Endpoint> endpoints = descriptor.getEndpoints();
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT)));
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT2)));
	}

	@Test
	public void testRemoveEndpoint() {
		addEndpoints();
		descriptor.removeEndpoint(TESTENDPOINT);
		Collection<Endpoint> endpoints = descriptor.getEndpoints();
		assertTrue(!endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT)));
		assertTrue(endpoints.stream().anyMatch(x -> x.getProtocolInformation().getEndpointAddress().equals(TESTENDPOINT2)));
	}

	private void addEndpoints() {
		descriptor = retrieveModelDescriptor();
		descriptor.addEndpoint(createEndpoint(ENDPOINT_INTERFACE, TESTENDPOINT));
		descriptor.addEndpoint(createEndpoint(ENDPOINT_INTERFACE, TESTENDPOINT2));
	}

	public Endpoint createEndpoint(String endpointInterface, String httpEndpoint) {
		ProtocolInformation protocolInformation = new ProtocolInformation(httpEndpoint);
		return new Endpoint(endpointInterface, protocolInformation);
	}
}
