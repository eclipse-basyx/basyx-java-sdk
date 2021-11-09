/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.address;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Address;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Email;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Fax;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Phone;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link Address} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestAddress {
	public static final String IDSHORT = "testAddressId";
	public static MultiLanguageProperty department = new MultiLanguageProperty(Address.DEPARTMENTID);
	public static MultiLanguageProperty street = new MultiLanguageProperty(Address.STREETID);
	public static MultiLanguageProperty zipCode = new MultiLanguageProperty(Address.ZIPCODEID);
	public static MultiLanguageProperty poBox = new MultiLanguageProperty(Address.POBOXID);
	public static MultiLanguageProperty zipPoBox = new MultiLanguageProperty(Address.ZIPCODEOFPOBOXID);
	public static MultiLanguageProperty cityTown = new MultiLanguageProperty(Address.CITYTOWNID);
	public static MultiLanguageProperty stateCounty = new MultiLanguageProperty(Address.STATECOUNTYID);
	public static MultiLanguageProperty nationalCode = new MultiLanguageProperty(Address.NATIONALCODEID);
	public static MultiLanguageProperty vatNumber = new MultiLanguageProperty(Address.VATNUMBERID);
	public static MultiLanguageProperty addressRemarks = new MultiLanguageProperty(Address.ADDRESSREMARKSID);
	public static Property additLink = new Property(Address.ADDRESSOFADDITIONALLINKID, ValueType.String);
	public static Phone phone1 = new Phone("Phone01", new LangString("DE", "123456789"));
	public static Phone phone2 = new Phone("Phone02", new LangString("US", "123456711"));
	public static Fax fax1 = new Fax("Fax01", new LangString("DE", "123456789"));
	public static Fax fax2 = new Fax("Fax02", new LangString("DE", "123456711"));
	public static Email email1 = new Email("Email01", new Property(Email.EMAILADDRESSID, "abc@test.com"));
	public static Email email2 = new Email("Email02", new Property(Email.EMAILADDRESSID, "abcd@test.com"));
	

	private Map<String, Object> addressMap = new HashMap<String, Object>();
	
	@Before
	public void buildFax() {
		department.setValue(new LangStrings(new LangString("DE", "Dept Test")));
		street.setValue(new LangStrings(new LangString("DE", "musterstraße 1")));
		zipCode.setValue(new LangStrings(new LangString("DE", "12345")));
		poBox.setValue(new LangStrings(new LangString("DE", "PE 1234")));
		zipPoBox.setValue(new LangStrings(new LangString("DE", "12345")));
		cityTown.setValue(new LangStrings(new LangString("DE", "MusterStadt")));
		stateCounty.setValue(new LangStrings(new LangString("DE", "RLP")));
		nationalCode.setValue(new LangStrings(new LangString("DE", "DE")));
		vatNumber.setValue(new LangStrings(new LangString("DE", "123456")));
		addressRemarks.setValue(new LangStrings(new LangString("DE", "test remarks")));
		additLink.setValue("test.com");
		
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(department);
		elements.add(street);
		elements.add(zipCode);
		elements.add(poBox);
		elements.add(zipPoBox);
		elements.add(cityTown);
		elements.add(stateCounty);
		elements.add(nationalCode);
		elements.add(vatNumber);
		elements.add(addressRemarks);
		elements.add(additLink);
		elements.add(phone1);
		elements.add(phone2);
		elements.add(fax1);
		elements.add(fax2);
		elements.add(email1);
		elements.add(email2);
		
		addressMap.put(Referable.IDSHORT, IDSHORT);
		addressMap.put(HasSemantics.SEMANTICID, Address.SEMANTICID);
		addressMap.put(Property.VALUE, elements);
	}
	
	@Test
	public void testCreateAsFacade() {
		Address addressFromMap = Address.createAsFacade(addressMap);
		assertEquals(Address.SEMANTICID, addressFromMap.getSemanticId());
		assertEquals(IDSHORT, addressFromMap.getIdShort());
		assertEquals(department, addressFromMap.getDepartment());
		assertEquals(street, addressFromMap.getStreet());
		assertEquals(poBox, addressFromMap.getPOBox());
		assertEquals(zipPoBox, addressFromMap.getZipCodeOfPOBox());
		assertEquals(cityTown, addressFromMap.getCityTown());
		assertEquals(stateCounty, addressFromMap.getStateCounty());
		assertEquals(nationalCode, addressFromMap.getNationalCode());
		assertEquals(zipCode, addressFromMap.getZipCode());
		assertEquals(vatNumber, addressFromMap.getVatNumber());
		assertEquals(addressRemarks, addressFromMap.getAddressRemarks());
		assertEquals(additLink, addressFromMap.getAddressOfAdditionalLink());
		List<Phone> phones = new ArrayList<Phone>();
		phones.add(phone1);
		phones.add(phone2);
		List<Fax> faxes = new ArrayList<Fax>();
		faxes.add(fax1);
		faxes.add(fax2);
		List<Email> emails = new ArrayList<Email>();
		emails.add(email1);
		emails.add(email2);
		assertEquals(phones, addressFromMap.getPhone());
		assertEquals(faxes, addressFromMap.getFax());
		assertEquals(emails, addressFromMap.getEmail());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		addressMap.remove(Referable.IDSHORT);
		Address.createAsFacade(addressMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionStreet() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)addressMap.get(Property.VALUE);
		elements.remove(street);
		Address.createAsFacade(addressMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionZipCode() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)addressMap.get(Property.VALUE);
		elements.remove(zipCode);
		Address.createAsFacade(addressMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionCityTown() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)addressMap.get(Property.VALUE);
		elements.remove(cityTown);
		Address.createAsFacade(addressMap);
	}
	
	@SuppressWarnings("unchecked")
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionNationalCode() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)addressMap.get(Property.VALUE);
		elements.remove(nationalCode);
		Address.createAsFacade(addressMap);
	}
}
