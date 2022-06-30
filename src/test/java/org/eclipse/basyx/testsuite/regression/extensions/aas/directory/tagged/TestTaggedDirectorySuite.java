/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
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
	protected final IIdentifier taggedAAS4 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#004");
	protected final IIdentifier taggedAAS5 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#005");
	protected final IIdentifier taggedAAS6 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedDirectoryAAS#006");

	protected IIdentifier taggedSmId1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedSM#001");
	protected IIdentifier taggedSmId2 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedSM#002");
	protected IIdentifier taggedSmId3 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedSM#003");
	protected IIdentifier taggedSmId4 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:taggedSM#004");
	protected IIdentifier submodelId1 = new ModelUrn("urn:de.FHG:devices.es.iese/test:aas:1.0:1:submodel#001");

	// IdShorts used in test cases
	protected final String taggedAasIdShort1 = "taggedAasIdShort1";
	protected final String taggedAasIdShort2 = "taggedAasIdShort2";
	protected final String taggedAasIdShort3 = "taggedAasIdShort3";
	protected final String taggedAasIdShort4 = "taggedAasIdShort4";
	protected final String taggedAasIdShort5 = "taggedAasIdShort5";
	protected final String taggedAasIdShort6 = "taggedAasIdShort6";

	protected final String taggedSmIdShort1 = "taggedSubmodelIdShort1";
	protected final String taggedSmIdShort2 = "taggedSubmodelIdShort2";
	protected final String taggedSmIdShort3 = "taggedSubmodelIdShort3";
	protected final String taggedSmIdShort4 = "taggedSubmodelIdShort4";
	protected final String submodelIdShort1 = "submodelIdShort1";

	// Endpoints used in test cases
	protected final String taggedAasEndpoint1 = "http://www.registrytest.de/aas01/taggedaas";
	protected final String taggedAasEndpoint2 = "http://www.registrytest.de/aas02/taggedaas";
	protected final String taggedAasEndpoint3 = "http://www.registrytest.de/aas03/taggedaas";
	protected final String taggedAasEndpoint4 = "http://www.registrytest.de/aas04/taggedaas";
	protected final String taggedAasEndpoint5 = "http://www.registrytest.de/aas05/taggedaas";
	protected final String taggedAasEndpoint6 = "http://www.registrytest.de/aas06/taggedaas";

	protected String taggedSmEndpoint1 = "http://www.registrytest.de/aas01/taggedAas/submodel";
	protected String taggedSmEndpoint2 = "http://www.registrytest.de/aas02/taggedAas/submodel";
	protected String taggedSmEndpoint3 = "http://www.registrytest.de/aas03/taggedAas/submodel";
	protected String taggedSmEndpoint4 = "http://www.registrytest.de/aas05/taggedAas/taggedsubmodel";
	protected String submodelEndpoint1 = "http://www.registrytest.de/aas01/taggedAas/submodel_not_tagged";

	// Tags used in test cases
	protected final String DEVICE = "device";
	protected final String SUPPLIER_A = "SupplierA";
	protected final String SUPPLIER_B = "SupplierB";
	protected final String MILL = "mill";
	protected final String PACKAGER = "packager";
	protected final String BASYS_READY = "basysReady";
	protected final String INTERNAL = "internal";
	protected final String INTEGRATOR = "integrator";
	protected final String MACHINE = "machine";
	protected final String KEY = "key";
	protected final String COMPONENT = "component";
	protected final String SM_ONLY = "smOnly";

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

		TaggedAASDescriptor desc4 = new TaggedAASDescriptor(taggedAasIdShort4, taggedAAS4, taggedAasEndpoint4);
		desc4.addTags(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE));
		directory.register(desc4);

		TaggedSubmodelDescriptor taggedSmDesc1 = new TaggedSubmodelDescriptor(taggedSmIdShort1, taggedSmId1, taggedSmEndpoint1);
		taggedSmDesc1.addTags(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE));
		directory.registerSubmodel(taggedAAS4, taggedSmDesc1);

		TaggedAASDescriptor desc5 = new TaggedAASDescriptor(taggedAasIdShort5, taggedAAS5, taggedAasEndpoint5);
		desc5.addTags(Arrays.asList(INTERNAL, MACHINE));

		SubmodelDescriptor smDesc = new SubmodelDescriptor(submodelIdShort1, submodelId1, submodelEndpoint1);
		desc5.addSubmodelDescriptor(smDesc);

		directory.register(desc5);

		TaggedSubmodelDescriptor taggedSmDesc2 = new TaggedSubmodelDescriptor(taggedSmIdShort2, taggedSmId2, taggedSmEndpoint2);
		taggedSmDesc2.addTags(Arrays.asList(INTEGRATOR, KEY, COMPONENT));
		directory.registerSubmodel(taggedAAS5, taggedSmDesc2);

		TaggedSubmodelDescriptor taggedSmDesc3 = new TaggedSubmodelDescriptor(taggedSmIdShort3, taggedSmId3, taggedSmEndpoint3);
		taggedSmDesc3.addTags(Arrays.asList(INTEGRATOR, KEY, COMPONENT));
		directory.registerSubmodel(taggedAAS5, taggedSmDesc3);
		
		TaggedAASDescriptor desc6 = new TaggedAASDescriptor(taggedAasIdShort6, taggedAAS6, taggedAasEndpoint6);
		TaggedSubmodelDescriptor taggedSmDesc4 = new TaggedSubmodelDescriptor(taggedSmIdShort4, taggedSmId4, taggedSmEndpoint4);
		taggedSmDesc4.addTags(Arrays.asList(SM_ONLY));
		desc6.addSubmodelDescriptor(taggedSmDesc4);
		directory.register(desc6);
	}

	/**
	 * Remove registry entries after each test
	 */
	@Override
	@After
	public void tearDown() {
		try {
			directory.delete(taggedAAS1);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS2);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS3);
		} catch (ResourceNotFoundException doesntMatter) {

		}
		try {
			directory.delete(taggedAAS4, taggedSmId1);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS4);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS5);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS5, taggedSmId2);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS5, taggedSmId3);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS5, submodelId1);
		} catch (ResourceNotFoundException doesntMatter) {
		}
		
		try {
			directory.delete(taggedAAS6);
		} catch (ResourceNotFoundException doesntMatter) {
		}

		try {
			directory.delete(taggedAAS6, taggedSmId4);
		} catch (ResourceNotFoundException doesntMatter) {
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

	@Test
	public void testLookupTaggedSubmodelDescriptor() {
		init();

		Set<TaggedSubmodelDescriptor> descriptors = directory.lookupSubmodelTag(INTERNAL);

		assertEquals(1, descriptors.size());
	}

	@Test
	public void testLookUpNonExistingSubmodelTag() {
		init();

		Set<TaggedSubmodelDescriptor> descriptors = directory.lookupSubmodelTag("non-existing-tag");

		assertTrue(descriptors.isEmpty());
	}

	@Test
	public void testLookUpMultipleSubmodelTags() {
		init();

		Set<TaggedSubmodelDescriptor> descriptors1 = directory.lookupSubmodelTags(new HashSet<>(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE)));

		assertEquals(1, descriptors1.size());
	}

	@Test
	public void testRegisterAndLookupAasAndSubmodelTags() {
		init();

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors = directory.lookupSubmodelTags(new HashSet<>(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE)));

		assertEquals(1, taggedSmDescriptors.size());

		Set<TaggedAASDescriptor> taggedAASDescriptors = directory.lookupTags(new HashSet<>(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE)));

		assertEquals(1, taggedAASDescriptors.size());
	}

	@Test
	public void testSameTagShouldOnlyBeAddedOnce() {
		TaggedSubmodelDescriptor taggedSubmodelDescriptor = new TaggedSubmodelDescriptor(smIdShort1, smId1, smEndpoint1);
		taggedSubmodelDescriptor.addTag("same-tag");
		taggedSubmodelDescriptor.addTag("same-tag");

		assertEquals(1, taggedSubmodelDescriptor.getTags().size());
	}

	@Test
	public void testLookUpBothAasAndSubmodelTags() {
		init();

		Set<String> aasTags = new HashSet<>(Arrays.asList(INTERNAL, MACHINE));
		Set<String> submodelTags = new HashSet<>(Arrays.asList(INTEGRATOR, KEY, COMPONENT));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors = directory.lookupBothAasAndSubmodelTags(aasTags, submodelTags);
		assertEquals(2, taggedSmDescriptors.size());

		Set<String> aasTags1 = new HashSet<>(Arrays.asList(INTERNAL));
		Set<String> submodelTags1 = new HashSet<>(Arrays.asList(KEY, COMPONENT));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors1 = directory.lookupBothAasAndSubmodelTags(aasTags1, submodelTags1);
		assertEquals(2, taggedSmDescriptors1.size());
		
		Set<String> aasTagWildcard = new HashSet<>(Arrays.asList("*"));
		Set<String> submodelTags2 = new HashSet<>(Arrays.asList(SM_ONLY));
		Set<TaggedSubmodelDescriptor> taggedSmDescriptors2 = directory.lookupBothAasAndSubmodelTags(aasTagWildcard, submodelTags2);
		assertEquals(1, taggedSmDescriptors2.size());
	}

	@Test
	public void testLookupMultipleSubmodelDescriptors() {
		init();

		Set<String> submodelTags1 = new HashSet<>(Arrays.asList(INTEGRATOR, COMPONENT));
		Set<TaggedSubmodelDescriptor> desc1 = directory.lookupSubmodelTags(submodelTags1);
		assertEquals(2, desc1.size());

		Set<String> submodelTags2 = new HashSet<>(Arrays.asList(INTEGRATOR, KEY, COMPONENT));
		Set<TaggedSubmodelDescriptor> desc2 = directory.lookupSubmodelTags(submodelTags2);
		assertEquals(2, desc2.size());

		Set<String> submodelTags3 = new HashSet<>(Collections.emptyList());
		Set<TaggedSubmodelDescriptor> desc3 = directory.lookupSubmodelTags(submodelTags3);
		assertEquals(0, desc3.size());
	}

	@Test
	public void testLookupAasTagsAndSubmodelTagsThatDontBelongTogether() {
		init();

		Set<String> aasTags = new HashSet<>(Arrays.asList(DEVICE, PACKAGER, SUPPLIER_A, BASYS_READY));
		Set<String> smTags = new HashSet<>(Arrays.asList(INTEGRATOR, KEY, COMPONENT));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors = directory.lookupBothAasAndSubmodelTags(aasTags, smTags);
		assertEquals(0, taggedSmDescriptors.size());
	}

	@Test
	public void testLookUpBothAasAndSubmodelTagsWithEmptySets() {
		init();

		Set<String> emptyTags = new HashSet<>(Arrays.asList());
		Set<String> submodelTags = new HashSet<>(Arrays.asList(INTEGRATOR, KEY, COMPONENT));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors = directory.lookupBothAasAndSubmodelTags(emptyTags, submodelTags);
		assertEquals(0, taggedSmDescriptors.size());

		Set<String> aasTags1 = new HashSet<>(Arrays.asList(INTERNAL, MACHINE));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors2 = directory.lookupBothAasAndSubmodelTags(aasTags1, emptyTags);
		assertEquals(0, taggedSmDescriptors2.size());

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors3 = directory.lookupBothAasAndSubmodelTags(emptyTags, emptyTags);

		assertEquals(0, taggedSmDescriptors3.size());
	}

	@Test
	public void testLookUpBothAasAndSubmodelTagsWithWildcard() {
		init();

		Set<String> allTags = new HashSet<>(Arrays.asList("*"));
		Set<String> submodelTags = new HashSet<>(Arrays.asList(INTEGRATOR, KEY, COMPONENT));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors = directory.lookupBothAasAndSubmodelTags(allTags, submodelTags);
		assertEquals(2, taggedSmDescriptors.size());

		Set<String> aasTagsWithWildcard = new HashSet<>(Arrays.asList(INTERNAL, MACHINE, "*"));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors1 = directory.lookupBothAasAndSubmodelTags(aasTagsWithWildcard, submodelTags);

		assertEquals(2, taggedSmDescriptors1.size());

		Set<String> aasTags = new HashSet<>(Arrays.asList(INTERNAL, INTEGRATOR, MACHINE));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors2 = directory.lookupBothAasAndSubmodelTags(aasTags, allTags);
		assertEquals(1, taggedSmDescriptors2.size());

		Set<String> aasTags1 = new HashSet<>(Arrays.asList(INTERNAL, MACHINE));

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors3 = directory.lookupBothAasAndSubmodelTags(aasTags1, allTags);
		assertEquals(4, taggedSmDescriptors3.size());

		Set<TaggedSubmodelDescriptor> taggedSmDescriptors4 = directory.lookupBothAasAndSubmodelTags(allTags, allTags);

		assertEquals(0, taggedSmDescriptors4.size());

	}
}
