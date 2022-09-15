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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement;

import static java.util.Collections.emptySet;
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
 * Tests constructor and getter of {@link SubmodelElement} for their correctness
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
	public void testDataSpecificationReferencesInitialization() {
	  assertEquals(emptySet(), submodelElement.getDataSpecificationReferences());
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
		submodelElement.setKind(newModelingKind);
		assertEquals(newModelingKind, submodelElement.getKind());
	}
}
