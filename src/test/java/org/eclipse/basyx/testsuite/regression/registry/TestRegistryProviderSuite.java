/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.registry.descriptor.parts.GlobalAssetId;
import org.eclipse.basyx.registry.descriptor.parts.SpecificAssetId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for a registry. All registry provider implementations have
 * to pass these tests.
 *
 * @author espen, fischer
 *
 */
public abstract class TestRegistryProviderSuite {
	protected final IRegistry proxy = getRegistryService();

	protected IIdentifier shellIdentifier1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:registryAAS#001");
	protected IIdentifier shellIdentifier2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:registryAAS#002");
	protected String shellIdShort1 = "shellIdShort1";
	protected String shellIdShort2 = "shellIdShort2";
	protected String shellEndpoint1 = "http://www.registrytest.de/aas01/shell";
	protected String shellEndpoint2 = "http://www.registrytest.de/aas02/shell";
	protected GlobalAssetId globalAssetId1 = new GlobalAssetId();
	protected GlobalAssetId globalAssetId2 = new GlobalAssetId();
	protected SpecificAssetId specificAssetIds1 = new SpecificAssetId();
	protected SpecificAssetId specificAssetIds2 = new SpecificAssetId();

	protected IIdentifier submodelIdentifier1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:statusSM#001");
	protected IIdentifier submodelIdentifier2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:testSM#001");
	protected String submodelIdShort1 = "submodelIdShort1";
	protected String submodelIdShort2 = "submodelIdShort2";
	protected String submodelEndpoint1 = "http://www.registrytest.de/aas01/aas/submodels/" + submodelIdShort1 + "/submodel";
	protected String submodelEndpoint2 = "http://www.registrytest.de/aas01/aas/submodels/" + submodelIdShort2 + "/submodel";

	/**
	 * Getter for the tested registry provider. Tests for actual registry provider
	 * have to realize this method.
	 */
	protected abstract IRegistry getRegistryService();

	/**
	 * Before each test, clean entries are created in the registry using a proxy
	 */
	@Before
	public void setUp() {
		SubmodelDescriptor submodelDescriptor1 = new SubmodelDescriptor(submodelIdShort1, submodelIdentifier1, new Endpoint(submodelEndpoint1));
		SubmodelDescriptor submodelDescriptor2 = new SubmodelDescriptor(submodelIdShort2, submodelIdentifier2, new Endpoint(submodelEndpoint2));

		AASDescriptor shellDescriptor1 = new AASDescriptor(shellIdShort1, shellIdentifier1, globalAssetId1, specificAssetIds1, new Endpoint(shellEndpoint1));
		shellDescriptor1.addSubmodelDescriptor(submodelDescriptor1);
		AASDescriptor shellDescriptor2 = new AASDescriptor(shellIdShort2, shellIdentifier2, globalAssetId2, specificAssetIds2, new Endpoint(shellEndpoint2));

		proxy.register(shellDescriptor1);
		proxy.register(shellDescriptor2);
		proxy.register(submodelDescriptor1);
		proxy.register(submodelDescriptor2);
	}

	/**
	 * Remove registry entries after each test
	 */
	@After
	public void tearDown() {
		try {
			proxy.deleteModel(shellIdentifier1);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.deleteModel(shellIdentifier2);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.deleteSubmodel(submodelIdentifier1);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.deleteSubmodel(submodelIdentifier2);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
	}

	/**
	 * Tests getting single shell entries from the registry and validates the
	 * result.
	 */
	@Test
	public void testGetSingleShell() {
		// Retrieve and check the first shell
		AASDescriptor shellDescriptor = proxy.lookupShell(shellIdentifier1);
		validateShellDescriptor1(shellDescriptor);
	}

	/**
	 * Tests getting single submodel entries from the registry and validates the
	 * result.
	 */
	@Test
	public void getSingleSubmodel() {
		// Retrieve and check the first Submodel
		SubmodelDescriptor submodelDescriptor = proxy.lookupSubmodel(submodelIdentifier1);
		validateSubmodelDescriptor1(submodelDescriptor);
	}

	/**
	 * Tests getting all entries from the registry and validates the result.
	 */
	@Test
	public void getMultipleShell() {
		// Get all registered Shells
		List<AASDescriptor> result = proxy.lookupAllShells();
		// Check, if both shells are registered. Ordering does not matter
		assertEquals(2, result.size());
		if (result.get(0).getIdShort().equals(shellIdShort1)) {
			validateShellDescriptor1(result.get(0));
			validateShellDescriptor2(result.get(1));
		} else {
			validateShellDescriptor2(result.get(0));
			validateShellDescriptor1(result.get(1));
		}
	}

	/**
	 * Tests getting all entries from the registry and validates the result.
	 */
	@Test
	public void getMultipleSubmodel() {
		// Get all registered submodels
		List<SubmodelDescriptor> result = proxy.lookupAllSubmodels();
		// Check, if both AAS are registered. Ordering does not matter
		assertEquals(2, result.size());
		if (result.get(0).getIdShort().equals(submodelIdShort1)) {
			validateSubmodelDescriptor1(result.get(0));
			validateSubmodelDescriptor2(result.get(1));
		} else {
			validateSubmodelDescriptor2(result.get(0));
			validateSubmodelDescriptor1(result.get(1));
		}
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the
	 * first shellDescriptor as given by the test setup
	 */
	protected void validateShellDescriptor1(AASDescriptor shellDescriptor) {
		assertEquals(shellIdentifier1.getId(), shellDescriptor.getIdentifier().getId());
		assertEquals(shellIdentifier1.getIdType(), shellDescriptor.getIdentifier().getIdType());
		assertEquals(shellEndpoint1, shellDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());

		// Check, if the SM descriptor in the AASDescriptor is correct
		SubmodelDescriptor submodelDescriptor = shellDescriptor.getSubmodelDescriptorFromIdentifier(submodelIdentifier1);
		validateSubmodelDescriptor1(submodelDescriptor);
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the
	 * second shellDescriptor as given by the test setup
	 */
	protected void validateShellDescriptor2(AASDescriptor descriptor) {
		assertEquals(shellIdentifier2.getId(), descriptor.getIdentifier().getId());
		assertEquals(shellIdentifier2.getIdType(), descriptor.getIdentifier().getIdType());
		assertEquals(shellEndpoint2, descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the
	 * first submodelDescriptor as given by the test setup
	 */
	protected void validateSubmodelDescriptor1(SubmodelDescriptor submodelDescriptor) {
		assertEquals(submodelIdentifier1.getId(), submodelDescriptor.getIdentifier().getId());
		assertEquals(submodelIdentifier1.getIdType(), submodelDescriptor.getIdentifier().getIdType());
		assertEquals(submodelIdShort1, submodelDescriptor.get(Referable.IDSHORT));
		assertEquals(submodelEndpoint1, submodelDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the
	 * second submodelDescriptor as given by the test setup
	 */
	protected void validateSubmodelDescriptor2(SubmodelDescriptor submodelDescriptor) {
		assertEquals(submodelIdentifier2.getId(), submodelDescriptor.getIdentifier().getId());
		assertEquals(submodelIdentifier2.getIdType(), submodelDescriptor.getIdentifier().getIdType());
		assertEquals(submodelIdShort2, submodelDescriptor.get(Referable.IDSHORT));
		assertEquals(submodelEndpoint2, submodelDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
	}

	/**
	 * Tests deletion for shellDescriptors
	 */
	@Test
	public void testDeleteShellDescriptors() {
		assertNotNull(proxy.lookupShell(shellIdentifier1));
		assertNotNull(proxy.lookupShell(shellIdentifier2));

		proxy.deleteModel(shellIdentifier2);

		// After aas2 has been deleted, only aas1 should be registered
		assertNotNull(proxy.lookupShell(shellIdentifier1));
		try {
			proxy.lookupShell(shellIdentifier2);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}

		proxy.deleteModel(shellIdentifier1);

		// After aas1 has been deleted, both should not be registered any more
		try {
			proxy.lookupShell(shellIdentifier1);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
		try {
			proxy.lookupShell(shellIdentifier2);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	/**
	 * Tests deletion for submodelDescriptors
	 */
	@Test
	public void deleteSubmodelDescriptors() {
		assertNotNull(proxy.lookupSubmodel(submodelIdentifier1));
		assertNotNull(proxy.lookupSubmodel(submodelIdentifier2));

		proxy.deleteSubmodel(submodelIdentifier2);

		// After aas2 has been deleted, only aas1 should be registered
		assertNotNull(proxy.lookupSubmodel(submodelIdentifier1));
		try {
			proxy.lookupSubmodel(submodelIdentifier2);
			System.out.println(proxy.lookupSubmodel(submodelIdentifier2));
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}

		proxy.deleteSubmodel(submodelIdentifier1);

		// After aas1 has been deleted, both should not be registered any more
		try {
			proxy.lookupSubmodel(submodelIdentifier1);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
		try {
			proxy.lookupSubmodel(submodelIdentifier2);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getSingleShellWithSubmodelIdentifier() {
		proxy.lookupShell(submodelIdentifier1);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getSingleSubmodelWithShellIdentifier() {
		proxy.lookupSubmodel(shellIdentifier1);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteNotExistingSubmodelFromNotExistingShell() {
		proxy.deleteSubmodelFromShell(new Identifier(IdentifierType.CUSTOM, "nonExistent"), new Identifier(IdentifierType.CUSTOM, "nonExistentSubmodelId"));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteNotExistingSubmodelFromExistingShell() {
		proxy.deleteSubmodelFromShell(shellIdentifier1, new Identifier(IdentifierType.CUSTOM, "nonExistentSubmodelId"));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteNotExistingShell() {
		proxy.deleteModel(new Identifier(IdentifierType.CUSTOM, "nonExistent"));
	}

	@Test
	public void retrieveSubmodelDescriptors() {
		List<SubmodelDescriptor> submodelDescriptors = proxy.lookupAllSubmodelsForShell(shellIdentifier1);
		assertEquals(1, submodelDescriptors.size());
		assertEquals(submodelIdShort1, submodelDescriptors.get(0).getIdShort());
	}

	@Test
	public void retrieveSpecificSubmodelDescriptor() {
		SubmodelDescriptor submodelDescriptor = proxy.lookupSubmodel(shellIdentifier1, submodelIdentifier1);
		assertEquals(submodelIdShort1, submodelDescriptor.getIdShort());
	}

	/**
	 * Tests overwriting the descriptor of a Shell
	 */
	@Test
	public void overwritingShellDescriptor() {
		AASDescriptor shellDescriptor = new AASDescriptor(shellIdShort1, shellIdentifier1, globalAssetId1, specificAssetIds1, new Endpoint("http://testendpoint/"));
		proxy.updateShell(shellDescriptor.getIdentifier(), shellDescriptor);
		AASDescriptor retrievedShellDescriptor = proxy.lookupShell(shellIdentifier1);
		assertEquals(shellDescriptor.getFirstEndpoint(), retrievedShellDescriptor.getFirstEndpoint());
	}

	/**
	 * Tests overwriting the descriptor of a Submodel
	 */
	@Test
	public void overwritingSubmodelDescriptor() {
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodelIdShort1, submodelIdentifier1, new Endpoint("http://testendpoint/"));
		proxy.updateSubmodel(submodelDescriptor.getIdentifier(), submodelDescriptor);
		SubmodelDescriptor retrievedSubmodelDescriptor = proxy.lookupSubmodel(submodelIdentifier1);
		assertEquals(submodelDescriptor.getFirstEndpoint(), retrievedSubmodelDescriptor.getFirstEndpoint());
	}

	/**
	 * Tests overwriting a not existing descriptor of a Shell
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void overwritingNotExistingShellDescriptor() {
		AASDescriptor shellDescriptor = new AASDescriptor("notExistingShell", new Identifier(IdentifierType.CUSTOM, "notExisitingShellIdentifier"), new Endpoint("http://testendpoint/"));
		proxy.updateShell(shellDescriptor.getIdentifier(), shellDescriptor);
	}

	/**
	 * Tests overwriting a not existing descriptor of a Submodel
	 */
	@Test(expected = ResourceNotFoundException.class)
	public void overwritingNotExistingSubmodelDescriptor() {
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("notExistingSubmodel", new Identifier(IdentifierType.CUSTOM, "notExistingSubmodelIdentifier"), new Endpoint("http://testendpoint/"));
		proxy.updateSubmodel(submodelDescriptor.getIdentifier(), submodelDescriptor);
	}

	/**
	 * Tests creating already existing Shell
	 */
	@Test(expected = MalformedRequestException.class)
	public void createExistingShellDescriptor() {
		AASDescriptor shellDescriptor = new AASDescriptor(shellIdShort1, shellIdentifier1, new Endpoint("http://testendpoint/"));
		proxy.register(shellDescriptor);
	}

	/**
	 * Tests creating already existing Submodel
	 */
	@Test(expected = MalformedRequestException.class)
	public void createExistingSubmodelDescriptor() {
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodelIdShort1, submodelIdentifier1, new Endpoint("http://testendpoint/"));
		proxy.register(submodelDescriptor);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void registerSubmodelToNotExistingShell() {
		proxy.registerSubmodelForShell(new Identifier(IdentifierType.CUSTOM, "nonExistent"), new SubmodelDescriptor(submodelIdShort1, submodelIdentifier1, new Endpoint(submodelEndpoint1)));
	}

	/**
	 * Tests addition, retrieval and removal of submodels
	 */
	@Test
	public void submodelAsPartOfExistingShell() {
		String submodelIdShort = "submodelIdShortForShell";
		IIdentifier submodelIdentifier = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:submodelForShell");
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodelIdShort, submodelIdentifier, new Endpoint("http://testendpoint"));
		proxy.registerSubmodelForShell(shellIdentifier1, submodelDescriptor);

		AASDescriptor shellDescriptor = proxy.lookupShell(shellIdentifier1);
		assertEquals(submodelDescriptor, shellDescriptor.getSubmodelDescriptorFromIdShort(submodelIdShort));

		SubmodelDescriptor submodelDescriptorNew = new SubmodelDescriptor(submodelIdShort, submodelIdentifier, new Endpoint("http://testendpoint/newElement/"));
		proxy.updateSubmodelForShell(shellIdentifier1, submodelDescriptorNew);
		AASDescriptor shellDescriptorNew = proxy.lookupShell(shellIdentifier1);
		assertEquals(submodelDescriptorNew.getFirstEndpoint(), shellDescriptorNew.getSubmodelDescriptorFromIdShort(submodelIdShort).getFirstEndpoint());

		proxy.deleteSubmodelFromShell(shellIdentifier1, submodelIdentifier);

		shellDescriptor = proxy.lookupShell(shellIdentifier1);
		assertNotNull(shellDescriptor.getSubmodelDescriptorFromIdShort(submodelIdShort1));
		try {
			shellDescriptor.getSubmodelDescriptorFromIdShort(submodelIdShort);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	/**
	 * Test for correct specificAssetIds. specificAssetIds is currently an empty map
	 */
	@Test
	public void specificAssetIds() {
		AASDescriptor shellDescriptor = proxy.lookupShell(shellIdentifier1);
		assertTrue(shellDescriptor.getSpecificAssetIds().isEmpty());
	}

	/**
	 * Test for correct globalAssetId. globalAssetId is currently empty
	 */
	@Test
	public void globalAssetId() {
		AASDescriptor shellDescriptor = proxy.lookupShell(shellIdentifier1);
		assertTrue(shellDescriptor.getGlobalAssetId().isEmpty());
	}

	@Test(expected = MalformedRequestException.class)
	public void createSubmodelWithExistingShellIdentifier() {
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("sameIdentifierAsShell", shellIdentifier1, new Endpoint(null));
		proxy.register(submodelDescriptor);
	}

	@Test(expected = MalformedRequestException.class)
	public void createShellWithExistingSubmodelIdentifier() {
		AASDescriptor shellDescriptor = new AASDescriptor("sameIdentifierAsSubmodel", submodelIdentifier1, globalAssetId1, specificAssetIds1, new Endpoint(null));
		proxy.register(shellDescriptor);
	}
}
