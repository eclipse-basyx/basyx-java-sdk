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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.parts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.parts.ConceptDictionary;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link ConceptDescription} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestConceptDictionary {
	private static final Reference REFERENCE = new Reference(new Identifier(IdentifierType.CUSTOM, "testValue"), KeyElements.ASSET, false);
	private static final Reference REFERENCE2 = new Reference(new Identifier(IdentifierType.IRDI, "testNewId"), KeyElements.ASSET, true);
	
	private ConceptDictionary dictionary;
	
	@Before
	public void buildConceptDictionary() {
		List<IReference> refs = new ArrayList<>();
		refs.add(REFERENCE);
		dictionary = new ConceptDictionary("testIdShort");
		dictionary.setConceptDescriptionReferences(refs);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(Collections.singleton(REFERENCE), dictionary.getConceptDescriptionReferences());
		assertEquals(new ArrayList<IConceptDescription>(), dictionary.getConceptDescriptions());
	}
	
	@Test
	public void testSetIdShort() {
		String newIdString = "newId";
		dictionary.setIdShort(newIdString);
		assertEquals(newIdString, dictionary.getIdShort());
	}
	
	@Test
	public void testSetCategory() {
		String newCategoryString = "newCategory";
		dictionary.setCategory(newCategoryString);
		assertEquals(newCategoryString, dictionary.getCategory());
	}
	
	@Test
	public void testSetDescription() {
		LangStrings newDescriptionString = new LangStrings("DE", "newTest");
		dictionary.setDescription(newDescriptionString);
		assertEquals(newDescriptionString, dictionary.getDescription());
	}
	
	@Test
	public void testSetParent() {
		dictionary.setParent(REFERENCE2);
		assertEquals(REFERENCE2, dictionary.getParent());
	}
	
	@Test
	public void testSetConceptDescriptionReferences() {
		Collection<IReference> references = new HashSet<IReference>();
		references.add(REFERENCE);
		references.add(REFERENCE2);
		dictionary.setConceptDescriptionReferences(references);
		assertEquals(references, dictionary.getConceptDescriptionReferences());
	}
	
	@Test
	public void testSetConceptDescriptions() {
		ConceptDescription description1 = new ConceptDescription("testIdShort1", new Identifier(IdentifierType.IRDI, "testIdShort1"));
		description1.setCategory("cat1");
		ConceptDescription description2 = new ConceptDescription("testIdShort2", new Identifier(IdentifierType.IRDI, "testIdShort2"));
		description2.setCategory("cat2");
		Collection<IConceptDescription> descriptions = new ArrayList<IConceptDescription>();
		descriptions.add(description1);
		descriptions.add(description2);
		dictionary.setConceptDescriptions(descriptions);
		assertEquals(descriptions, dictionary.getConceptDescriptions());
	} 
	
	@Test
	public void testAddConceptDescription() {
		IdentifierType idType = IdentifierType.IRI;
		String id = "testId";
		ConceptDescription description = new ConceptDescription("testIdShort", new Identifier(idType, id));
		description.setCategory("testCategory");
		dictionary.addConceptDescription(description);
		assertEquals(Collections.singletonList(description), dictionary.getConceptDescriptions());

		// Build reference for newly added concept description
		Reference reference = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, true, id, idType));

		Collection<IReference> refs = dictionary.getConceptDescriptionReferences();
		assertTrue(refs.contains(REFERENCE));
		assertTrue(refs.contains(reference));
	}
}
