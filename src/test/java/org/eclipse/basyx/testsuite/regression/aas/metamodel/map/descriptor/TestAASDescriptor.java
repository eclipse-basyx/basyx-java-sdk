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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
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
public class TestAASDescriptor extends ModelDescriptorTestSuite {
	
	private Map<String, Object> map;
	
	@Before
	public void initialize() {
		map = new LinkedHashMap<String, Object>();
		map.put(Referable.IDSHORT, "123");
		map.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRDI, "123"));
		map.put(ModelDescriptor.ENDPOINTS, Arrays.asList(new LinkedHashMap<String, String>()));
		map.put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
	}

	/**
	 * Tests retrieval of all registered submodel descriptors
	 */
	@Test
	public void testGetAllSubmodels() {
		// Setup descriptor and add one submodel descriptor
		AASDescriptor descriptor = new AASDescriptor(new Identifier(IdentifierType.CUSTOM, "Test"), "http://a.b/c/aas");
		descriptor.addSubmodelDescriptor(new SubmodelDescriptor("SM1", new Identifier(IdentifierType.CUSTOM, "SM1"), "http://a.b/c/aas/submodels/SM1"));

		// Assert correct retrieval
		assertEquals(1, descriptor.getSubmodelDescriptors().size());
		assertEquals("SM1", descriptor.getSubmodelDescriptors().iterator().next().getIdentifier().getId());

		// Add a second descriptor
		descriptor.addSubmodelDescriptor(new SubmodelDescriptor("SM2", new Identifier(IdentifierType.CUSTOM, "SM2"), "http://a.b/c/aas/submodels/SM2"));

		// Assert correct retrieval
		assertEquals(2, descriptor.getSubmodelDescriptors().size());
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNoIdShort() {
		map.remove(Referable.IDSHORT);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNullIdShort() {
		map.put(Referable.IDSHORT, null);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateWrongIdShort() {
		map.put(Referable.IDSHORT, 0);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNoIdentification() {
		map.remove(Identifiable.IDENTIFICATION);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNullIdentification() {
		map.put(Identifiable.IDENTIFICATION, null);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateWrongdentification() {
		map.put(Identifiable.IDENTIFICATION, "testId");
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNoEndpoints() {
		map.remove(ModelDescriptor.ENDPOINTS);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateNullEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, null);
		new AASDescriptor(map);
	}
	
	@Test(expected = MalformedRequestException.class)
	public void testValidateWrongEndpoints() {
		map.put(ModelDescriptor.ENDPOINTS, "testEndpoint");
		new AASDescriptor(map);
	}
	
	@Test
	public void testValidateNoSubmodels() {
		map.remove(AssetAdministrationShell.SUBMODELS);
		assertNotNull(new AASDescriptor(map).getSubmodelDescriptors());
	}

	@Test(expected = MalformedRequestException.class)
	public void testValidateNullSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, null);
		new AASDescriptor(map).getSubmodelDescriptors();
	}

	@Test(expected = MalformedRequestException.class)
	public void testValidateWrongSubmodels() {
		map.put(AssetAdministrationShell.SUBMODELS, "testSubmodel");
		new AASDescriptor(map).getSubmodelDescriptors();
	}

	@Override
	public ModelDescriptor retrieveModelDescriptor() {
		return new AASDescriptor(map);
	}
}
