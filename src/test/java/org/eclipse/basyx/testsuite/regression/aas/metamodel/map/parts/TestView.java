/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.parts;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.basyx.aas.metamodel.map.parts.View;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link View} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestView {
	private static final Reference REFERENCE = new Reference(new Identifier(IdentifierType.CUSTOM, "testValue"), KeyElements.ASSET, false);
	private static final Reference REFERENCE2 = new Reference(new Identifier(IdentifierType.IRDI, "testNewId"), KeyElements.ASSET, true);
	
	private View view;
	private Collection<IReference> references;
	
	@Before
	public void buildConceptDictionary() {
		view = new View("testIdShort");
		view.setContainedElement(Collections.singleton(REFERENCE));
		references = new HashSet<IReference>();
		references.add(REFERENCE);
		references.add(REFERENCE2);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(Collections.singleton(REFERENCE), view.getContainedElement());
	}
	
	@Test
	public void testSetContainedElement() {
		view.setContainedElement(references);
		assertEquals(references, view.getContainedElement());
	}
	
	@Test
	public void testSetSemanticID() {
		view.setSemanticId(REFERENCE);
		assertEquals(REFERENCE, view.getSemanticId());
	}
	
	@Test
	public void testSetDataSpecificationReferences() {
		view.setDataSpecificationReferences(references);
		assertEquals(references, view.getDataSpecificationReferences());
	}
	
	@Test
	public void testSetEmbeddedDataSpecifications() {
		EmbeddedDataSpecification embeddedDataSpecification = new EmbeddedDataSpecification();
		Collection<IEmbeddedDataSpecification> specifications = Collections.singleton(embeddedDataSpecification);
		view.setEmbeddedDataSpecifications(specifications);
		assertEquals(specifications, view.getEmbeddedDataSpecifications());
	}
	
	@Test
	public void testSetIdShort() {
		String newIdString = "newId";
		view.setIdShort(newIdString);
		assertEquals(newIdString, view.getIdShort());
	}
	
	@Test
	public void testSetCategory() {
		String newCategoryString = "newCategory";
		view.setCategory(newCategoryString);
		assertEquals(newCategoryString, view.getCategory());
	}
	
	@Test
	public void testSetDescription() {
		LangStrings newDescriptionString = new LangStrings("DE", "newTest");
		view.setDescription(newDescriptionString);
		assertEquals(newDescriptionString, view.getDescription());
	}
	
	@Test
	public void testSetParent() {
		view.setParent(REFERENCE2);
		assertEquals(REFERENCE2, view.getParent());
	}
}
