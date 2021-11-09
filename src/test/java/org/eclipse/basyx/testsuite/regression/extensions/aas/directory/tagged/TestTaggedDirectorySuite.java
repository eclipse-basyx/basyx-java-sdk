/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.testsuite.regression.aas.registration.TestRegistryProviderSuite;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.Test;

/**
 * Testsuite for TaggedDirectories extending the RegistryProviderSuite
 * 
 * @author schnicke
 *
 */
public abstract class TestTaggedDirectorySuite extends TestRegistryProviderSuite {
	protected abstract IAASTaggedDirectory getDirectory();

	protected final IAASTaggedDirectory directory = getDirectory();

	// Identifiers used in test cases
	protected final IIdentifier taggedAAS1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#001");
	protected final IIdentifier taggedAAS2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#002");
	protected final IIdentifier taggedAAS3 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#003");

	// IdShorts used in test cases
	protected final String taggedAasIdShort1 = "taggedAasIdShort1";
	protected final String taggedAasIdShort2 = "taggedAasIdShort2";
	protected final String taggedAasIdShort3 = "taggedAasIdShort3";

	// Endpoints used in test cases
	protected final String taggedAasEndpoint1 = "http://www.registrytest.de/aas01/taggedaas";
	protected final String taggedAasEndpoint2 = "http://www.registrytest.de/aas02/taggedaas";
	protected final String taggedAasEndpoint3 = "http://www.registrytest.de/aas03/taggedaas";

	// Tags used in test cases
	protected final String DEVICE = "device";
	protected final String SUPPLIER_A = "SupplierA";
	protected final String SUPPLIER_B = "SupplierB";
	protected final String MILL = "mill";
	protected final String PACKAGER = "packager";
	protected final String BASYS_READY = "basysReady";

	/**
	 * This method is not included in @Before to not interfere with test cases of
	 * parent test suite, namely {@link TestRegistryProviderSuite#testGetMultiAAS()}
	 */
	private void init() {
		// Register AASs using several tags
		TaggedAASDescriptor desc1 = new TaggedAASDescriptor(taggedAasIdShort1, taggedAAS1, taggedAasEndpoint1);
		desc1.addTag(DEVICE);
		desc1.addTag(SUPPLIER_A);
		desc1.addTag(MILL);
		desc1.addTag(BASYS_READY);
		directory.register(desc1);

		TaggedAASDescriptor desc2 = new TaggedAASDescriptor(taggedAasIdShort2, taggedAAS2, taggedAasEndpoint2);
		desc2.addTags(Arrays.asList(DEVICE, PACKAGER, SUPPLIER_A, BASYS_READY));
		directory.register(desc2);

		TaggedAASDescriptor desc3 = new TaggedAASDescriptor(taggedAasIdShort3, taggedAAS3, taggedAasEndpoint3);
		desc3.addTags(Arrays.asList(DEVICE, PACKAGER, SUPPLIER_B, BASYS_READY));
		directory.register(desc3);
	}

	/**
	 * Remove registry entries after each test
	 */
	@Override
	@After
	public void tearDown() {
		try {
			proxy.deleteModel(taggedAAS1);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.deleteModel(taggedAAS2);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
		try {
			proxy.deleteModel(taggedAAS3);
		} catch (ResourceNotFoundException e) {
			// Does not matter
		}
	}

	@Test
	public void testRetrieveSingleTag() {
		init();
		// Lookup a tag that all three AAS have
		assertEquals(3, directory.lookupTag(DEVICE).size());
		
		// Lookup a tag that multiple, but not all AAS have
		Set<TaggedAASDescriptor> packagers = directory.lookupTag(PACKAGER);
		assertEquals(2, packagers.size());
		testDescriptorsExistance(packagers, Arrays.asList(taggedAasIdShort2, taggedAasIdShort3));

		// Lookup a tag that only a single AAS has
		Set<TaggedAASDescriptor> mills = directory.lookupTag(MILL);
		assertEquals(1, mills.size());
		assertEquals(taggedAasIdShort1, mills.iterator().next().getIdShort());

		// Lookup a tag that no AAS has
		assertEquals(0, directory.lookupTag("unknownTag").size());
	}

	@Test
	public void testRetrieveMultipleTags() {
		init();
		// Lookup tags that all three AAS have
		assertEquals(3, directory.lookupTags(new HashSet<>((Arrays.asList(DEVICE, BASYS_READY)))).size());

		// Lookup tags that only two AAS have
		Set<TaggedAASDescriptor> supplierA = directory.lookupTags(new HashSet<>((Arrays.asList(DEVICE, SUPPLIER_A))));
		assertEquals(2, supplierA.size());
		testDescriptorsExistance(supplierA, Arrays.asList(taggedAasIdShort1, taggedAasIdShort2));

		// Lookup tags that only a single AAS has
		Set<TaggedAASDescriptor> packagers = directory.lookupTags(new HashSet<>((Arrays.asList(PACKAGER, SUPPLIER_B))));
		assertEquals(1, packagers.size());
		assertEquals(taggedAasIdShort3, packagers.iterator().next().getIdShort());

		// Lookup a tag that no AAS has
		assertEquals(0, directory.lookupTags(new HashSet<>(Arrays.asList("unknownTag1", "unknownTag2"))).size());
	}

	@Test
	public void testLookupOfDeletedAAS() {
		// Ensure that no Descriptor is returned
		assertEquals(0, directory.lookupTag(MILL).size());
		assertEquals(0, directory.lookupTags(new HashSet<>(Arrays.asList(DEVICE, MILL))).size());
	}

	/**
	 * Tests if for every expected idShort, there's a descriptor contained
	 * 
	 * @param descriptors
	 * @param idShorts
	 */
	private void testDescriptorsExistance(Set<TaggedAASDescriptor> descriptors, List<String> idShorts) {
		idShorts.stream().forEach(s -> {
			assertTrue(descriptors.stream().filter(d -> d.getIdShort().equals(s)).findAny().isPresent());
		});
	}

}
