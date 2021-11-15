/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.registry.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.basyx.registry.descriptor.ModelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for Model Descriptor common method testing
 *
 * @author haque, fischer, fried, jung
 *
 */
public abstract class ModelDescriptorTestSuite {
	private static final String TESTENDPOINT1 = "dummy.com";
	private static final String TESTENDPOINT2 = "dummy2.com";
	private static final String TEST_ADMINISTRATION_VERSION = "v0";
	private static final String TEST_ADMINISTRATION_REVISION = "a";
	private static final LangString DESCRIPTION1 = new LangString("DE", "Beschreibung");
	private static final LangString DESCRIPTION2 = new LangString("EN", "Description");

	private static ModelDescriptor modelDescriptor;
	private static Endpoint endpoint1;
	private static Endpoint endpoint2;

	public abstract ModelDescriptor retrieveModelDescriptor();

	@BeforeClass
	public static void setUpClass() {
		endpoint1 = new Endpoint(TESTENDPOINT1);
		endpoint2 = new Endpoint(TESTENDPOINT2);
	}

	@Test
	public void addEndpoint() {
		addEndpoints();
		Collection<Endpoint> endpoints = modelDescriptor.getEndpoints();
		assertTrue(endpoints.contains(endpoint1));
		assertTrue(endpoints.contains(endpoint2));
	}

	@Test
	public void removeEndpoint() {
		addEndpoints();
		modelDescriptor.removeEndpoint(TESTENDPOINT1);
		Collection<Endpoint> endpoints = modelDescriptor.getEndpoints();
		assertTrue(!endpoints.contains(endpoint1));
		assertTrue(endpoints.contains(endpoint2));
	}

	@Test
	public void setAdministration() {
		modelDescriptor = retrieveModelDescriptor();
		modelDescriptor.setAdministration(new AdministrativeInformation(TEST_ADMINISTRATION_VERSION, TEST_ADMINISTRATION_REVISION));
		AdministrativeInformation adminInformation = modelDescriptor.getAdministration();
		assertEquals(TEST_ADMINISTRATION_VERSION, adminInformation.getVersion());
		assertEquals(TEST_ADMINISTRATION_REVISION, adminInformation.getRevision());
	}

	@Test
	public void addDescription() {
		addDescriptions();
		Collection<LangString> descriptions = modelDescriptor.getDescriptions();
		assertTrue(descriptions.contains(DESCRIPTION1));
		assertTrue(descriptions.contains(DESCRIPTION2));
		assertEquals(2, descriptions.size());
	}

	@Test
	public void removeDescription() {
		addDescriptions();
		modelDescriptor.removeDescription(DESCRIPTION1);
		Collection<LangString> descriptions = modelDescriptor.getDescriptions();
		assertTrue(!descriptions.contains(DESCRIPTION1));
		assertTrue(descriptions.contains(DESCRIPTION2));
		assertEquals(1, descriptions.size());
	}

	private void addDescriptions() {
		modelDescriptor = retrieveModelDescriptor();
		modelDescriptor.addDescription(DESCRIPTION1);
		modelDescriptor.addDescription(DESCRIPTION2);
	}

	private void addEndpoints() {
		modelDescriptor = retrieveModelDescriptor();
		modelDescriptor.addEndpoint(endpoint1);
		modelDescriptor.addEndpoint(endpoint2);
	}

}
