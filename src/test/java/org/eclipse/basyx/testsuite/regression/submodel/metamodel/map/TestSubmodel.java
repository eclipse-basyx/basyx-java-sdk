/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.TestSubmodelSuite;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures correct behavior of {@link Submodel}
 * 
 * @author haque
 *
 */
public class TestSubmodel extends TestSubmodelSuite {

	ISubmodel submodel;

	@Before
	public void build() {
		submodel = getReferenceSubmodel();
	}

	@Test
	public void testParentAddSubmodelElement() {
		Property prop = new Property("propIdShort", ValueType.String);
		IIdentifier identifier = new ModelUrn("testId");
		Submodel submodel = new Submodel("smIdShort", identifier);
		submodel.addSubmodelElement(prop);
		
		// Create expected parent of the element for assertion
		Reference expectedParent = new Reference(new Key(KeyElements.SUBMODEL, true, identifier.getId(), identifier.getIdType()));
		assertEquals(expectedParent, prop.getParent());
	} 

	/**
	 * Tests if a Submodel containing a list for SUBMODELELEMENT is correctly
	 * handled by the facading submodel. This is necessary because the submodel
	 * serialization does specify SUBMODELELEMENT as list
	 */
	@Test
	public void testCreateAsFacadePropertyList() {
		// Create test property
		String propId = "testProp";
		
		Property expected = new Property(5);
		expected.setIdShort(propId);

		// Create test submodel and force key SUBMODELELEMENT to contain a list
		String id = "testIdShort";
		Submodel sm = new Submodel(id, new Identifier(IdentifierType.IRDI, id));
		sm.put(Submodel.SUBMODELELEMENT, Collections.singleton(expected));

		// Check if the facade converts the SUBMODELELEMENT value correctly
		Submodel facade = Submodel.createAsFacade(sm);
		assertTrue(facade.get(Submodel.SUBMODELELEMENT) instanceof Map<?, ?>);
		assertEquals(expected, facade.getSubmodelElements().get(propId));
	}

	@Override
	protected ISubmodel getSubmodel() {
		return submodel;
	}
}
