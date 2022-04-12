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
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate;

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
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Address;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.AssetSpecificProperties;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings.Marking;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings.Markings;
import org.eclipse.basyx.submodel.types.digitalnameplate.DigitalNameplateSubmodel;
import org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.address.TestAddress;
import org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.TestAssetSpecificProperties;
import org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.markings.TestMarking;
import org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.markings.TestMarkings;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link DigitalNameplateSubmodel} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestDigitalNameplateSubmodel {
	public static MultiLanguageProperty manufacturerName = new MultiLanguageProperty(DigitalNameplateSubmodel.MANUFACTURERNAMEID);
	public static MultiLanguageProperty designation = new MultiLanguageProperty(DigitalNameplateSubmodel.MANUFACTURERPRODUCTDESIGNATIONID);
	public static Address address = new Address(TestAddress.street, TestAddress.zipCode, TestAddress.cityTown, TestAddress.nationalCode);
	public static MultiLanguageProperty productFamily = new MultiLanguageProperty(DigitalNameplateSubmodel.MANUFACTURERPRODUCTFAMILYID);
	public static Property serialNumber = new Property(DigitalNameplateSubmodel.SERIALNUMBERID, ValueType.String);
	public static Property yearsOfConstruction = new Property(DigitalNameplateSubmodel.YEARSOFCONSTRUCTIONID, ValueType.String);
	public static Markings markings;
	public static AssetSpecificProperties assetSpecificProperties = new AssetSpecificProperties(Collections.singletonList(TestAssetSpecificProperties.guidelineSpecificProperties));
	public static Identifier identifier = new Identifier(IdentifierType.IRI, "https://admin-shell.io/zvei/nameplate/1/0/Nameplate");
	private Map<String, Object> submodelMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void buildFax() {
		manufacturerName.setValue(new LangStrings(new LangString("DE", "Test Manufacturer")));
		designation.setValue(new LangStrings(new LangString("DE", "Test Designation")));
		productFamily.setValue(new LangStrings(new LangString("DE", "Test Product Family")));
		serialNumber.setValue("123456");
		yearsOfConstruction.setValue("2020");
		
		TestMarking.markingFile.setIdShort(Marking.MARKINGFILEID);
		TestMarking.markingName.setValue("0173-1#07-DAA603#004");
		TestMarkings.marking = new Marking(TestMarking.IDSHORT, TestMarking.markingName, TestMarking.markingFile);
		TestMarkings.marking.setParent(new Reference(new Key(KeyElements.SUBMODELELEMENTCOLLECTION, true, Markings.IDSHORT, IdentifierType.IRDI)));
		TestMarkings.markings = new ArrayList<Marking>();
		TestMarkings.markings.add(TestMarkings.marking);
		markings = new Markings(TestMarkings.markings);
		
		TestAddress.street.setValue(new LangStrings(new LangString("DE", "musterstraße 1")));
		TestAddress.zipCode.setValue(new LangStrings(new LangString("DE", "12345")));
		TestAddress.cityTown.setValue(new LangStrings(new LangString("DE", "MusterStadt")));
		TestAddress.nationalCode.setValue(new LangStrings(new LangString("DE", "DE")));
		
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(manufacturerName);
		elements.add(designation);
		elements.add(productFamily);
		elements.add(serialNumber);
		elements.add(yearsOfConstruction);
		elements.add(markings);
		elements.add(address);
		elements.add(assetSpecificProperties);
		submodelMap.put(Referable.IDSHORT, DigitalNameplateSubmodel.SUBMODELID);
		submodelMap.put(HasSemantics.SEMANTICID, DigitalNameplateSubmodel.SEMANTICID);
		submodelMap.put(Submodel.SUBMODELELEMENT, elements);
		submodelMap.put(Identifiable.IDENTIFICATION, identifier);
	}

	@Test
	public void testCreateAsFacade() {
		DigitalNameplateSubmodel submodelFromMap = DigitalNameplateSubmodel.createAsFacade(submodelMap);
		assertEquals(DigitalNameplateSubmodel.SEMANTICID, submodelFromMap.getSemanticId());
		assertEquals(manufacturerName, submodelFromMap.getManufacturerName());
		assertEquals(designation, submodelFromMap.getManufacturerProductDesignation());
		assertEquals(address, submodelFromMap.getAddress());
		assertEquals(productFamily, submodelFromMap.getManufacturerProductFamily());
		assertEquals(serialNumber, submodelFromMap.getSerialNumber());
		assertEquals(yearsOfConstruction, submodelFromMap.getYearOfConstruction());
		assertEquals(markings, submodelFromMap.getMarkings());
		assertEquals(assetSpecificProperties, submodelFromMap.getAssetSpecificProperties());
		assertEquals(DigitalNameplateSubmodel.SUBMODELID, submodelFromMap.getIdShort());
		assertEquals(identifier, submodelFromMap.getIdentification());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		submodelMap.remove(Referable.IDSHORT);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdentifier() {
		submodelMap.remove(Identifiable.IDENTIFICATION);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionManufacturerName() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(manufacturerName);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionZearsOfConstruction() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(yearsOfConstruction);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionManufacturerProductDesignation() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(designation);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionAddress() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(address);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionManufacturerProductFamily() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)submodelMap.get(Submodel.SUBMODELELEMENT);
		elements.remove(productFamily);
		DigitalNameplateSubmodel.createAsFacade(submodelMap);
	}
}
