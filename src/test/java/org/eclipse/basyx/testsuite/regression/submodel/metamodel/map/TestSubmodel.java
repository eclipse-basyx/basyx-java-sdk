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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.eclipse.basyx.aas.metamodel.exception.IdShortDuplicationException;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
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
	
	@Test(expected = IdShortDuplicationException.class)
	public void checkForExceptionWithDuplicateIdShortInSubmodel() {
		Map<String, Object> faultySubmodel = createSubmodelWithDuplicateIdShortProperties();

		Submodel.createAsFacade(faultySubmodel);
	}

	private Map<String, Object> createSubmodelWithDuplicateIdShortProperties() {
		String duplicateIdShort = "testProp";
		
		Property property1 = new Property(duplicateIdShort, 5);
		Property property2 = new Property(duplicateIdShort, 7);
		
		Collection<Map<String, Object>> collection = Arrays.asList(property1, property2);
		
		String idShort = "submodelIdShort";
		
		Submodel submodel = new Submodel(idShort, new Identifier(IdentifierType.IRI, idShort));
		
		submodel.put(Submodel.SUBMODELELEMENT, collection);
		
		return submodel;
	}
}
