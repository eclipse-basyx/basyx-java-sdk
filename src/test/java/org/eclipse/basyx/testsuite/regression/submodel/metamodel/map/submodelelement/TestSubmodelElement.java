/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link SubmodelElement} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestSubmodelElement {
	private static final Reference REFERENCE = new Reference(new Identifier(IdentifierType.CUSTOM, "testValue"), KeyElements.ACCESSPERMISSIONRULE, false);
	private static final Formula FORMULA = new Formula(Collections.singleton(new Reference(new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI))));
	
	private SubmodelElement submodelElement;
	
	@Before
	public void buidSubmodelElement() {
		submodelElement = new Property("testIdShort", "testId");
	}
	
	@Test
	public void testSetDataSpecificationReferences() {
		Collection<IReference> refs = Collections.singleton(REFERENCE);
		submodelElement.setDataSpecificationReferences(refs);
		assertEquals(refs, submodelElement.getDataSpecificationReferences());
	} 
	
	@Test
	public void testSetEmbeddedDataSpecifications() {
		EmbeddedDataSpecification embeddedDataSpecification = new EmbeddedDataSpecification();
		Collection<IEmbeddedDataSpecification> specifications = Collections.singleton(embeddedDataSpecification);
		submodelElement.setEmbeddedDataSpecifications(specifications);
		assertEquals(specifications, submodelElement.getEmbeddedDataSpecifications());
	}
	
	@Test
	public void testSetIdShort() {
		String newIdString = "newId";
		submodelElement.setIdShort(newIdString);
		assertEquals(newIdString, submodelElement.getIdShort());
	}
	
	@Test
	public void testSetCategory() {
		String newCategoryString = "newCategory";
		submodelElement.setCategory(newCategoryString);
		assertEquals(newCategoryString, submodelElement.getCategory());
	}
	
	@Test
	public void testSetDescription() {
		LangStrings newDescriptionString = new LangStrings("DE", "newTest");
		submodelElement.setDescription(newDescriptionString);
		assertEquals(newDescriptionString, submodelElement.getDescription());
	}
	
	@Test
	public void testSetParent() {
		submodelElement.setParent(REFERENCE);
		assertEquals(REFERENCE, submodelElement.getParent());
	}
	
	@Test
	public void testSetQualifier() {
		submodelElement.setQualifiers(Collections.singleton(FORMULA));
		assertEquals(Collections.singleton(FORMULA), submodelElement.getQualifiers());
	}
	
	@Test
	public void testSetSemanticID() {
		submodelElement.setSemanticId(REFERENCE);
		assertEquals(REFERENCE, submodelElement.getSemanticId());
	}
	
	@Test
	public void testSetModelingKind() {
		ModelingKind newModelingKind = ModelingKind.TEMPLATE;
		submodelElement.setModelingKind(newModelingKind);
		assertEquals(newModelingKind, submodelElement.getModelingKind());
	}
}
