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
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.GuidelineSpecificProperties;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link GuidelineSpecificProperties} for
 * their correctness
 * 
 * @author haque
 *
 */
public class TestGuidelineSpecificProperties {
	public static final String IDSHORT = "GuidelineSpecificProperties01";
	public static Property conformityDeclaration = new Property(GuidelineSpecificProperties.GUIDELINEFORCONFORMITYDECLARATIONID, ValueType.String);
	public static Property arbitrary = new Property("arbitraryId", ValueType.String);

	private Map<String, Object> guidelineMap = new LinkedHashMap<String, Object>();

	@Before
	public void buildGuidelineSpecificProperties() {
		conformityDeclaration.setValue("test Declaration");
		arbitrary.setValue("0173-1#07-DAA603#004");

		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(conformityDeclaration);
		elements.add(arbitrary);

		guidelineMap.put(Referable.IDSHORT, IDSHORT);
		guidelineMap.put(HasSemantics.SEMANTICID, GuidelineSpecificProperties.SEMANTICID);
		guidelineMap.put(Property.VALUE, elements);
	}

	@Test
	public void testCreateAsFacade() {
		GuidelineSpecificProperties guidelineFromMap = GuidelineSpecificProperties.createAsFacade(guidelineMap);
		assertEquals(GuidelineSpecificProperties.SEMANTICID, guidelineFromMap.getSemanticId());
		assertEquals(conformityDeclaration, guidelineFromMap.getGuidelineForConformityDeclaration());
		assertEquals(Collections.singletonList(arbitrary), guidelineFromMap.getArbitrary());
		assertEquals(IDSHORT, guidelineFromMap.getIdShort());
	}

	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		guidelineMap.remove(Referable.IDSHORT);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionArbitrary() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>) guidelineMap.get(Property.VALUE);
		elements.remove(arbitrary);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionDeclaration() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>) guidelineMap.get(Property.VALUE);
		elements.remove(conformityDeclaration);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}
}
