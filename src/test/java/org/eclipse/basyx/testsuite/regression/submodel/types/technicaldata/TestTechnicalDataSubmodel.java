/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.technicaldata.TechnicalDataSubmodel;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.furtherinformation.FurtherInformation;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.generalinformation.GeneralInformation;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassifications;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.technicalproperties.TechnicalProperties;
import org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.generalinformation.TestGeneralInformation;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link TechnicalDataSubmodel} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestTechnicalDataSubmodel {
	public static GeneralInformation generalInformation = new GeneralInformation(TestGeneralInformation.manufacturerName, TestGeneralInformation.designation, TestGeneralInformation.partNumber, TestGeneralInformation.orderCode);
	public static ProductClassifications productClassifications = new ProductClassifications(TechnicalDataSubmodel.PRODUCTCLASSIFICATIONSID);
	public static TechnicalProperties technicalProperties = new TechnicalProperties(TechnicalDataSubmodel.TECHNICALPROPERTIESID);
	public static FurtherInformation furtherInformation = new FurtherInformation(new Property(FurtherInformation.VALIDDATEID, ValueType.DateTime));
	
	public static Identifier identifier = new Identifier(IdentifierType.IRI, "http://admin-shell.io/ZVEI/TechnicalData/Submodel/1/1");
	private Map<String, Object> submodelMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void buildFax() {
		TestGeneralInformation.manufacturerName.setValue("Example Company");
		TestGeneralInformation.designation.setValue(new LangStrings(new LangString("de", "Elektrischer Energie Beschleuniger")));
		TestGeneralInformation.partNumber.setValue("A123-456");
		TestGeneralInformation.orderCode.setValue("EEA-EX-200-S/47-Q3");

		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(generalInformation);
		elements.add(furtherInformation);
		elements.add(productClassifications);
		elements.add(technicalProperties);
		
		submodelMap.put(Referable.IDSHORT, TechnicalDataSubmodel.SUBMODELID);
		submodelMap.put(HasSemantics.SEMANTICID, TechnicalDataSubmodel.SEMANTICID);
		submodelMap.put(Submodel.SUBMODELELEMENT, elements);
		submodelMap.put(Identifiable.IDENTIFICATION, identifier);
	}

	@Test
	public void testCreateAsFacade() {
		TechnicalDataSubmodel submodelFromMap = TechnicalDataSubmodel.createAsFacade(submodelMap);
		assertEquals(TechnicalDataSubmodel.SEMANTICID, submodelFromMap.getSemanticId());
		assertEquals(generalInformation, submodelFromMap.getGeneralInformation());
		assertEquals(furtherInformation, submodelFromMap.getFurtherInformation());
		assertEquals(technicalProperties, submodelFromMap.getTechnicalProperties());
		assertEquals(productClassifications, submodelFromMap.getProductClassifications());
		assertEquals(TechnicalDataSubmodel.SUBMODELID, submodelFromMap.getIdShort());
		assertEquals(identifier, submodelFromMap.getIdentification());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		submodelMap.remove(Referable.IDSHORT);
		TechnicalDataSubmodel.createAsFacade(submodelMap);
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdentifier() {
		submodelMap.remove(Identifiable.IDENTIFICATION);
		TechnicalDataSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionGeneralInfo() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(generalInformation);
		TechnicalDataSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionTechnicalProp() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(technicalProperties);
		TechnicalDataSubmodel.createAsFacade(submodelMap);
	}
}
