/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.registration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for a registry. All registry provider implementations have to pass these tests.
 *
 * @author espen
 *
 */
public abstract class TestRegistryProviderSuite {
	// The registry proxy that is used to access the sql servlet
	protected final IAASRegistry proxy = getRegistryService();

	// Ids, shortIds and endpoints for registered AAS and submodel
	protected IIdentifier aasId1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:registryAAS#001");
	protected IIdentifier aasId2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:registryAAS#002");
	protected IIdentifier smId1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:statusSM#001");
	protected IIdentifier smId2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:testSM#001");
	protected String aasIdShort1 = "aasIdShort1";
	protected String aasIdShort2 = "aasIdShort2";
	protected String smIdShort1 = "smIdShort1";
	protected String smIdShort2 = "smIdShort2";
	protected String aasEndpoint1 = "http://www.registrytest.de/aas01/aas";
	protected String aasEndpoint2 = "http://www.registrytest.de/aas02/aas";
	protected String smEndpoint1 = "http://www.registrytest.de/aas01/aas/submodels/" + smIdShort1 + "/submodel";
	protected String smEndpoint2 = "http://www.registrytest.de/aas01/aas/submodels/" + smIdShort2 + "/submodel";
	protected Asset asset1;
	protected Asset asset2;
	/**
	 * Getter for the tested registry provider. Tests for actual registry provider
	 * have to realize this method.
	 */
	protected abstract IAASRegistry getRegistryService();

	/**
	 * Before each test, clean entries are created in the registry using a proxy
	 */
	@Before
	public void setUp() {
		// Create assets
		asset1 = new Asset(new Reference(new Identifier(IdentifierType.CUSTOM, "asset001"), KeyElements.ASSET, false));
		asset1.setIdentification(IdentifierType.CUSTOM, "asset001");
		asset1.setIdShort("asset001");
		asset2 = new Asset(new Reference(new Identifier(IdentifierType.CUSTOM, "asset002"), KeyElements.ASSET, false));
		asset2.setIdentification(IdentifierType.CUSTOM, "asset002");
		asset2.setIdShort("asset002");
		// Create descriptors for AAS and submodels
		AASDescriptor aasDesc1 = new AASDescriptor(aasIdShort1, aasId1, asset1, new Endpoint(aasEndpoint1));
		aasDesc1.addSubmodelDescriptor(new SubmodelDescriptor(smIdShort1, smId1, new Endpoint(smEndpoint1)));
		AASDescriptor aasDesc2 = new AASDescriptor(aasIdShort2, aasId2, asset2, new Endpoint(aasEndpoint2));

		// Register Asset Administration Shells
		proxy.register(aasDesc1);
		proxy.register(aasDesc2);
	}

	/**
	 * Remove registry entries after each test
	 */
	@After
	public void tearDown() {
		try {
			proxy.delete(aasId1);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.delete(aasId2);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
	}

	/**
	 * Tests getting single entries from the registry and validates the result.
	 */
	@Test
	public void testGetSingleAAS() {
		// Retrieve and check the first AAS
		AASDescriptor descriptor = proxy.lookupAAS(aasId1);
		validateDescriptor1(descriptor);
	}


	/**
	 * Tests getting all entries from the registry and validates the result.
	 */
	@Test
	public void testGetMultiAAS() {
		// Get all registered AAS
		List<AASDescriptor> result = proxy.lookupAll();
		// Check, if both AAS are registered. Ordering does not matter
		assertEquals(2, result.size());
		if (result.get(0).getIdShort().equals(aasIdShort1)) {
			validateDescriptor1(result.get(0));
			validateDescriptor2(result.get(1));
		} else {
			validateDescriptor2(result.get(0));
			validateDescriptor1(result.get(1));
		}
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the first descriptor
	 * as given by the test setup
	 */
	protected void validateDescriptor1(AASDescriptor descriptor) {
		assertEquals(aasId1.getId(), descriptor.getIdentifier().getId());
		assertEquals(aasId1.getIdType(), descriptor.getIdentifier().getIdType());
		IAsset asset = descriptor.getAsset();
		assertEquals(asset1.getIdentification(), asset.getIdentification());
		assertEquals(aasEndpoint1, descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());

		// Check, if the SM descriptor in the AASDescriptor is correct
		SubmodelDescriptor smDescriptor = descriptor.getSubmodelDescriptorFromIdentifierId(smId1.getId());
		assertEquals(smId1.getId(), smDescriptor.getIdentifier().getId());
		assertEquals(smId1.getIdType(), smDescriptor.getIdentifier().getIdType());
		assertEquals(smIdShort1, smDescriptor.get(Referable.IDSHORT));
		assertEquals(smEndpoint1, smDescriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
	}

	/**
	 * Checks, if the given descriptor is valid. Should contain the values of the second descriptor
	 * as given by the test setup
	 */
	protected void validateDescriptor2(AASDescriptor descriptor) {
		assertEquals(aasId2.getId(), descriptor.getIdentifier().getId());
		assertEquals(aasId2.getIdType(), descriptor.getIdentifier().getIdType());
		IAsset asset = descriptor.getAsset();
		assertEquals(asset2.getIdentification(), asset.getIdentification());
		assertEquals(aasEndpoint2, descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
	}

	@Test
	public void testDeleteWithAssetExtension() {
		// After the setup, both AAS should have been inserted to the registry
		assertNotNull(proxy.lookupAAS(aasId1));
		assertNotNull(proxy.lookupAAS(aasId2));

		proxy.delete(aasId2);

		// After aas2 has been deleted, only aas1 should be registered
		assertNotNull(proxy.lookupAAS(asset1.getIdentification()));

		// Reference of asset-id to the AAS descriptor should also to deleted
		try {
			proxy.lookupAAS(asset2.getIdentification());
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}

		proxy.delete(aasId1);

		// Reference of both asset-ids to the AAS descriptors should also to deleted
		try {
			proxy.lookupAAS(asset1.getIdentification());
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
		try {
			proxy.lookupAAS(asset2.getIdentification());
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	/**
	 * Tests deletion for aas entries
	 */
	@Test
	public void testDeleteCall() {
		// After the setup, both AAS should have been inserted to the registry
		assertNotNull(proxy.lookupAAS(aasId1));
		assertNotNull(proxy.lookupAAS(aasId2));

		proxy.delete(aasId2);

		// After aas2 has been deleted, only aas1 should be registered
		assertNotNull(proxy.lookupAAS(aasId1));
		try {
			proxy.lookupAAS(aasId2);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}

		proxy.delete(aasId1);

		// After aas1 has been deleted, both should not be registered any more
		try {
			proxy.lookupAAS(aasId1);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
		try {
			proxy.lookupAAS(aasId2);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	/**
	 * Tests deletion for aas entries
	 */
	@Test
	public void testDeleteByAssetIdCall() {
		proxy.delete(asset1.getIdentification());

		// After aas1 has been deleted, only aas2 should be registered
		assertNotNull(proxy.lookupAAS(aasId2));
		assertNotNull(proxy.lookupAAS(asset2.getIdentification()));
		try {
			proxy.lookupAAS(aasId1);
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}

		try {
			proxy.lookupAAS(asset1.getIdentification());
			fail();
		} catch (ResourceNotFoundException e) {
			// expected
		}
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteNotExistingSubmodelFromNotExistingAAS() {
		proxy.delete(new Identifier(IdentifierType.CUSTOM, "nonExistent"), new Identifier(IdentifierType.CUSTOM, "nonExistentSubmodelId"));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteNotExistingSubmodel() {
		proxy.delete(aasId1, new Identifier(IdentifierType.CUSTOM, "nonExistentSubmodelId"));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testDeleteNotExistingAAS() {
		proxy.delete(new Identifier(IdentifierType.CUSTOM, "nonExistent"));
	}

	@Test
	public void testRetrieveSubmodelDescriptors() {
		List<SubmodelDescriptor> descs = proxy.lookupSubmodels(aasId1);
		assertEquals(1, descs.size());
		assertEquals(smIdShort1, descs.get(0).getIdShort());
	}

	@Test
	public void testRetrieveSpecificSubmodelDescriptor() {
		SubmodelDescriptor desc = proxy.lookupSubmodel(aasId1, smId1);
		assertEquals(smIdShort1, desc.getIdShort());
	}

	/**
	 * Tests overwriting the descriptor of an AAS
	 */
	@Test
	public void testOverwritingAASDescriptor() {
		AASDescriptor aasDesc2 = new AASDescriptor(aasIdShort2, aasId2, asset2, new Endpoint("http://testendpoint2/"));
		proxy.register(aasDesc2);
		AASDescriptor retrieved = proxy.lookupAAS(aasId2);
		assertEquals(aasDesc2.getFirstEndpoint(), retrieved.getFirstEndpoint());
	}

	/**
	 * Tests addition, retrieval and removal of submodels
	 */
	@Test
	public void testSubmodelCalls() {
		// Add descriptor
		SubmodelDescriptor smDesc = new SubmodelDescriptor(smIdShort2, smId2, new Endpoint(smEndpoint2));
		proxy.register(aasId1, smDesc);

		// Ensure that the submodel is correctly stored in the aas descriptor
		AASDescriptor aasDesc = proxy.lookupAAS(aasId1);
		assertEquals(smDesc, aasDesc.getSubmodelDescriptorFromIdShort(smIdShort2));

		// Test overwriting an SM descriptor
		SubmodelDescriptor smDescNew = new SubmodelDescriptor(smIdShort2, smId2, new Endpoint("http://testendpoint2/submodel/"));
		proxy.register(aasId1, smDescNew);
		AASDescriptor aasDescNew = proxy.lookupAAS(aasId1);
		assertEquals(smDescNew.getFirstEndpoint(), aasDescNew.getSubmodelDescriptorFromIdShort(smIdShort2).getFirstEndpoint());

		// Remove Submodel
		proxy.delete(aasId1, smId2);

		// Ensure that the submodel was correctly removed
		aasDesc = proxy.lookupAAS(aasId1);
		assertNotNull(aasDesc.getSubmodelDescriptorFromIdShort(smIdShort1));
		assertNull(aasDesc.getSubmodelDescriptorFromIdShort(smIdShort2));
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testRegisterSubmodelToNotExistingAAS() {
		proxy.register(new Identifier(IdentifierType.CUSTOM, "nonExistent"), new SubmodelDescriptor(smIdShort1, smId1, new Endpoint(smEndpoint1)));
	}
}
