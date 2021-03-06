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
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.AssetSpecificProperties;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.GuidelineSpecificProperties;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link AssetSpecificProperties} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestAssetSpecificProperties {
	public static final String IDSHORT = "AssetSpecificProperties";
	public static GuidelineSpecificProperties guidelineSpecificProperties = new GuidelineSpecificProperties(TestGuidelineSpecificProperties.IDSHORT, TestGuidelineSpecificProperties.conformityDeclaration,
			Collections.singletonList(TestGuidelineSpecificProperties.arbitrary));

	private Map<String, Object> assetMap = new LinkedHashMap<String, Object>();

	@Before
	public void buildAssetSpecificProperties() {

		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(guidelineSpecificProperties);

		assetMap.put(Referable.IDSHORT, IDSHORT);
		assetMap.put(HasSemantics.SEMANTICID, AssetSpecificProperties.SEMANTICID);
		assetMap.put(Property.VALUE, elements);
	}

	@Test
	public void testCreateAsFacade() {
		AssetSpecificProperties assetFromMap = AssetSpecificProperties.createAsFacade(assetMap);
		assertEquals(AssetSpecificProperties.SEMANTICID, assetFromMap.getSemanticId());
		assertEquals(Collections.singletonList(guidelineSpecificProperties), assetFromMap.getGuidelineSpecificProperties());
		assertEquals(IDSHORT, assetFromMap.getIdShort());
	}

	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		assetMap.remove(Referable.IDSHORT);
		AssetSpecificProperties.createAsFacade(assetMap);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionGuideline() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>) assetMap.get(Property.VALUE);
		elements.remove(guidelineSpecificProperties);
		AssetSpecificProperties.createAsFacade(assetMap);
	}
}
