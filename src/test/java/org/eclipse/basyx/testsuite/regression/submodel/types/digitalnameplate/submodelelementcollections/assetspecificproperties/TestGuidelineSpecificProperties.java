/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
 * Tests createAsFacade and isValid of {@link GuidelineSpecificProperties} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestGuidelineSpecificProperties {
	public static final String IDSHORT = "GuidelineSpecificProperties01";
	public static Property conformityDeclaration = new Property(GuidelineSpecificProperties.GUIDELINEFORCONFORMITYDECLARATIONID, ValueType.String);
	public static Property arbitrary = new Property("arbitraryId", ValueType.String);
	
	private Map<String, Object> guidelineMap = new HashMap<String, Object>();
	
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
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		guidelineMap.remove(Referable.IDSHORT);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionArbitrary() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)guidelineMap.get(Property.VALUE);
		elements.remove(arbitrary);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}

	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionDeclaration() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)guidelineMap.get(Property.VALUE);
		elements.remove(conformityDeclaration);
		GuidelineSpecificProperties.createAsFacade(guidelineMap);
	}
}
