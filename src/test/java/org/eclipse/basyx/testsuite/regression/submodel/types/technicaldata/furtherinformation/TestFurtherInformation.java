/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.furtherinformation;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.furtherinformation.FurtherInformation;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link FurtherInformation} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestFurtherInformation {
	public static MultiLanguageProperty statement = new MultiLanguageProperty(FurtherInformation.TEXTSTATEMENTPREFIX + "01");
	public static Property validDate = new Property(FurtherInformation.VALIDDATEID, ValueType.DateTime);
	private Map<String, Object> furtherInfoMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void init() {
		statement.setValue(new LangStrings(new LangString("DE", "test statement")));
		validDate.setValue("01-01-2021");
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(statement);
		elements.add(validDate);
		furtherInfoMap.put(Referable.IDSHORT, FurtherInformation.IDSHORT);
		furtherInfoMap.put(HasSemantics.SEMANTICID, FurtherInformation.SEMANTICID);
		furtherInfoMap.put(Property.VALUE, elements);
	}
	
	@Test
	public void testCreateAsFacade() {
		FurtherInformation infoFromMap = FurtherInformation.createAsFacade(furtherInfoMap);
		assertEquals(FurtherInformation.SEMANTICID, infoFromMap.getSemanticId());
		assertEquals(Collections.singletonList(statement), infoFromMap.getStatements());
		assertEquals(validDate, infoFromMap.getValidDate());
		assertEquals(FurtherInformation.IDSHORT, infoFromMap.getIdShort());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		furtherInfoMap.remove(Referable.IDSHORT);
		FurtherInformation.createAsFacade(furtherInfoMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionValidDate() {
		List<ISubmodelElement> newElements = new ArrayList<ISubmodelElement>();
		newElements.add(statement);
		furtherInfoMap.put(Property.VALUE, newElements);
		FurtherInformation.createAsFacade(furtherInfoMap);
	}
}
