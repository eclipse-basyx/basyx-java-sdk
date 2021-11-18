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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.ModelDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AASDescriptor}
 *
 * @author schnicke
 *
 */
public class TestAASDescriptorCreation extends ModelDescriptorTestSuite {

	private Map<String, Object> map;

	@Before
	public void setUp() {
		map = new HashMap<String, Object>();
		map.put(Referable.IDSHORT, "123");
		map.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRDI, "123"));
		map.put(ModelDescriptor.ENDPOINTS, new ArrayList<>());
		map.put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNoIdShort() {
		map.remove(Referable.IDSHORT);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullIdShort() {
		map.put(Referable.IDSHORT, null);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongIdShort() {
		map.put(Referable.IDSHORT, 0);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNoIdentification() {
		map.remove(Identifiable.IDENTIFICATION);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullIdentification() {
		map.put(Identifiable.IDENTIFICATION, null);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongdentification() {
		map.put(Identifiable.IDENTIFICATION, "testId");
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNoEndpoints() {
		map.remove(ModelDescriptor.ENDPOINTS);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, null);
		AASDescriptor.createAsFacade(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, "testEndpoint");
		AASDescriptor.createAsFacade(map);
	}

	@Test
	public void validateNoSubmodels() {
		map.remove(AssetAdministrationShell.SUBMODELS);
		assertNotNull(AASDescriptor.createAsFacade(map).getSubmodelDescriptors());
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, null);
		AASDescriptor.createAsFacade(map).getSubmodelDescriptors();
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, "testSubmodel");
		AASDescriptor.createAsFacade(map).getSubmodelDescriptors();
	}

	@Override
	public ModelDescriptor retrieveModelDescriptor() {
		return AASDescriptor.createAsFacade(map);
	}
}
