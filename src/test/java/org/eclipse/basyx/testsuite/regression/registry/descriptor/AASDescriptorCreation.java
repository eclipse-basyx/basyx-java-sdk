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
public class AASDescriptorCreation extends ModelDescriptorTestSuite {

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
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullIdShort() {
		map.put(Referable.IDSHORT, null);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongIdShort() {
		map.put(Referable.IDSHORT, 0);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNoIdentification() {
		map.remove(Identifiable.IDENTIFICATION);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullIdentification() {
		map.put(Identifiable.IDENTIFICATION, null);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongdentification() {
		map.put(Identifiable.IDENTIFICATION, "testId");
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNoEndpoints() {
		map.remove(ModelDescriptor.ENDPOINTS);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, null);
		new AASDescriptor(map);
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, "testEndpoint");
		new AASDescriptor(map);
	}

	@Test
	public void validateNoSubmodels() {
		map.remove(AssetAdministrationShell.SUBMODELS);
		assertNotNull(new AASDescriptor(map).getSubmodelDescriptors());
	}

	@Test(expected = MalformedRequestException.class)
	public void validateNullSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, null);
		new AASDescriptor(map).getSubmodelDescriptors();
	}

	@Test(expected = MalformedRequestException.class)
	public void validateWrongSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, "testSubmodel");
		new AASDescriptor(map).getSubmodelDescriptors();
	}

	@Override
	public ModelDescriptor retrieveModelDescriptor() {
		return new AASDescriptor(map);
	}
}
