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

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.technicalproperties;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.technicalproperties.TechnicalProperties;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link TestTechnicalProperties} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestTechnicalProperties {
	public static SubmodelElementCollection mainSection = new SubmodelElementCollection(TechnicalProperties.MAINSECTIONPREFIX + "01");
	public static SubmodelElementCollection subSection = new SubmodelElementCollection(TechnicalProperties.SUBSECTIONPREFIX + "01");
	public static SubmodelElement arbitrary1 = new Property("arbitraryId1", ValueType.String);
	public static SubmodelElement arbitrary2 = new Property("arbitraryId2", ValueType.String);
	
	private Map<String, Object> technicalMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void init() {
		mainSection.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, TechnicalProperties.MAINSECTIONID, IdentifierType.IRDI)));
		subSection.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, TechnicalProperties.SUBSECTIONID, IdentifierType.IRDI)));
		arbitrary1.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "anyid", IdentifierType.IRDI)));
		arbitrary2.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, TechnicalProperties.SMENOTDESCRIBEDID, IdentifierType.IRDI)));

		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(mainSection);
		elements.add(subSection);
		elements.add(arbitrary1);
		elements.add(arbitrary2);
		
		technicalMap.put(Referable.IDSHORT, TechnicalProperties.IDSHORT);
		technicalMap.put(HasSemantics.SEMANTICID, TechnicalProperties.SEMANTICID);
		technicalMap.put(Property.VALUE, elements);
	}
	
	@Test
	public void testCreateAsFacade() {
		TechnicalProperties propFromMap = TechnicalProperties.createAsFacade(technicalMap);
		assertEquals(TechnicalProperties.SEMANTICID, propFromMap.getSemanticId());
		assertEquals(TechnicalProperties.IDSHORT, propFromMap.getIdShort());
		assertEquals(Collections.singletonList(mainSection), propFromMap.getMainSections());
		assertEquals(Collections.singletonList(subSection), propFromMap.getSubSections());
		assertEquals(Collections.singletonList(arbitrary1), propFromMap.getArbitrary());
		assertEquals(Collections.singletonList(arbitrary2), propFromMap.getSMENotDescribedBySemanticId());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		technicalMap.remove(Referable.IDSHORT);
		TechnicalProperties.createAsFacade(technicalMap);
	}
}
