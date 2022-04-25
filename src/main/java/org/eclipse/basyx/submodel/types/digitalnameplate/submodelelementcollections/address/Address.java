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
package org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * Address as defined in the AAS Digital Nameplate Template document <br>
 * It is a submodel element collection which contains The standardized SMC
 * Address contains information about address of a partner within the value
 * chain.
 * 
 * @author haque
 *
 */
public class Address extends SubmodelElementCollection {
	public static final String DEPARTMENTID = "Department";
	public static final String STREETID = "Street";
	public static final String ZIPCODEID = "Zipcode";
	public static final String POBOXID = "POBox";
	public static final String ZIPCODEOFPOBOXID = "ZipCodeOfPOBox";
	public static final String CITYTOWNID = "CityTown";
	public static final String STATECOUNTYID = "StateCounty";
	public static final String NATIONALCODEID = "NationalCode";
	public static final String VATNUMBERID = "VATNumber";
	public static final String ADDRESSREMARKSID = "AddressRemarks";
	public static final String ADDRESSOFADDITIONALLINKID = "AddressOfAdditionalLink";
	public static final String PHONEPREFIX = "Phone";
	public static final String FAXPREFIX = "Fax";
	public static final String EMAILPREFIX = "Email";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAQ832#005", KeyType.IRDI));
	public static final String ADDRESSIDSHORT = "Address";

	private Address() {
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param street
	 * @param zipCode
	 * @param cityTown
	 * @param nationalCode
	 */
	public Address(MultiLanguageProperty street, MultiLanguageProperty zipCode, MultiLanguageProperty cityTown, MultiLanguageProperty nationalCode) {
		this(ADDRESSIDSHORT, street, zipCode, cityTown, nationalCode);
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param street
	 * @param zipCode
	 * @param cityTown
	 * @param nationalCode
	 */
	public Address(LangString street, LangString zipCode, LangString cityTown, LangString nationalCode) {
		this(ADDRESSIDSHORT, street, zipCode, cityTown, nationalCode);
	}

	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param street
	 * @param zipCode
	 * @param cityTown
	 * @param nationalCode
	 */
	public Address(String idShort, MultiLanguageProperty street, MultiLanguageProperty zipCode, MultiLanguageProperty cityTown, MultiLanguageProperty nationalCode) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setStreet(street);
		setZipCode(zipCode);
		setCityTown(cityTown);
		setNationalCode(nationalCode);
	}

	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param street
	 * @param zipCode
	 * @param cityTown
	 * @param nationalCode
	 */
	public Address(String idShort, LangString street, LangString zipCode, LangString cityTown, LangString nationalCode) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setStreet(street);
		setZipCode(zipCode);
		setCityTown(cityTown);
		setNationalCode(nationalCode);
	}

	/**
	 * Creates a Address SMC object from a map
	 * 
	 * @param obj
	 *            a Address SMC object as raw map
	 * @return a Address SMC object, that behaves like a facade for the given map
	 */
	public static Address createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Address.class, obj);
		}

		Address address = new Address();
		address.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return address;
	}

	/**
	 * Creates a Address SMC object from a map without validation
	 * 
	 * @param obj
	 *            a Address SMC object as raw map
	 * @return a Address SMC object, that behaves like a facade for the given map
	 */
	private static Address createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		Address address = new Address();
		address.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return address;
	}

	/**
	 * Check whether all mandatory elements for Address SMC exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		Address address = createAsFacadeNonStrict(obj);

		return SubmodelElementCollection.isValid(obj) && MultiLanguageProperty.isValid((Map<String, Object>) address.getStreet()) && MultiLanguageProperty.isValid((Map<String, Object>) address.getZipCode())
				&& MultiLanguageProperty.isValid((Map<String, Object>) address.getCityTown()) && MultiLanguageProperty.isValid((Map<String, Object>) address.getNationalCode());
	}

	/**
	 * Gets administrative section within an organisation where a business partner
	 * is located
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getDepartment() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(DEPARTMENTID));
	}

	/**
	 * Sets administrative section within an organisation where a business partner
	 * is located
	 * 
	 * @param department
	 *            {@link MultiLanguageProperty}
	 */
	public void setDepartment(MultiLanguageProperty department) {
		addSubmodelElement(department);
	}

	/**
	 * Sets administrative section within an organisation where a business partner
	 * is located
	 * 
	 * @param department
	 *            {@link LangString}
	 */
	public void setDepartment(LangString department) {
		MultiLanguageProperty deptProp = new MultiLanguageProperty(DEPARTMENTID);
		deptProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO127#003", IdentifierType.IRDI)));
		deptProp.setValue(new LangStrings(department));
		setDepartment(deptProp);
	}

	/**
	 * Gets street name and house number Note: mandatory property according to EU
	 * Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getStreet() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(STREETID));
	}

	/**
	 * Sets street name and house number Note: mandatory property according to EU
	 * Machine Directive 2006/42/EC.
	 * 
	 * @param street
	 *            {@link MultiLanguageProperty}
	 */
	public void setStreet(MultiLanguageProperty street) {
		addSubmodelElement(street);
	}

	/**
	 * Sets street name and house number Note: mandatory property according to EU
	 * Machine Directive 2006/42/EC.
	 * 
	 * @param street
	 *            {@link LangString}
	 */
	public void setStreet(LangString street) {
		MultiLanguageProperty streetProp = new MultiLanguageProperty(STREETID);
		streetProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO128#002", IdentifierType.IRDI)));
		streetProp.setValue(new LangStrings(street));
		setStreet(streetProp);
	}

	/**
	 * Gets ZIP code of address Note: mandatory property according to EU Machine
	 * Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getZipCode() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(ZIPCODEID));
	}

	/**
	 * Sets ZIP code of address Note: mandatory property according to EU Machine
	 * Directive 2006/42/EC.
	 * 
	 * @param zipCode
	 *            {@link MultiLanguageProperty}
	 */
	public void setZipCode(MultiLanguageProperty zipCode) {
		addSubmodelElement(zipCode);
	}

	/**
	 * Sets ZIP code of address Note: mandatory property according to EU Machine
	 * Directive 2006/42/EC.
	 * 
	 * @param zipCode
	 *            {@link LangString}
	 */
	public void setZipCode(LangString zipCode) {
		MultiLanguageProperty zipCodeProp = new MultiLanguageProperty(ZIPCODEID);
		zipCodeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO129#002", IdentifierType.IRDI)));
		zipCodeProp.setValue(new LangStrings(zipCode));
		setZipCode(zipCodeProp);
	}

	/**
	 * Gets P.O. box number
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getPOBox() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(POBOXID));
	}

	/**
	 * Sets P.O. box number
	 * 
	 * @param poBox
	 *            {@link MultiLanguageProperty}
	 */
	public void setPOBox(MultiLanguageProperty poBox) {
		addSubmodelElement(poBox);
	}

	/**
	 * Sets P.O. box number
	 * 
	 * @param poBox
	 *            {@link LangString}
	 */
	public void setPOBox(LangString poBox) {
		MultiLanguageProperty poBoxProp = new MultiLanguageProperty(POBOXID);
		poBoxProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO130#002", IdentifierType.IRDI)));
		poBoxProp.setValue(new LangStrings(poBox));
		setPOBox(poBoxProp);
	}

	/**
	 * Gets ZIP code of P.O. box address
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getZipCodeOfPOBox() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(ZIPCODEOFPOBOXID));
	}

	/**
	 * Sets ZIP code of P.O. box address
	 * 
	 * @param zipCodeOfPoBox
	 *            {@link MultiLanguageProperty}
	 */
	public void setZipCodeOfPOBox(MultiLanguageProperty zipCodeOfPoBox) {
		addSubmodelElement(zipCodeOfPoBox);
	}

	/**
	 * Sets ZIP code of P.O. box address
	 * 
	 * @param zipCodeOfPoBox
	 *            {@link LangString}
	 */
	public void setZipCodeOfPOBox(LangString zipCodeOfPoBox) {
		MultiLanguageProperty zipCodeOfPoBoxProp = new MultiLanguageProperty(ZIPCODEOFPOBOXID);
		zipCodeOfPoBoxProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO131#002", IdentifierType.IRDI)));
		zipCodeOfPoBoxProp.setValue(new LangStrings(zipCodeOfPoBox));
		setZipCodeOfPOBox(zipCodeOfPoBoxProp);
	}

	/**
	 * Gets town or city Note: mandatory property according to EU Machine Directive
	 * 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getCityTown() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(CITYTOWNID));
	}

	/**
	 * Sets town or city Note: mandatory property according to EU Machine Directive
	 * 2006/42/EC.
	 * 
	 * @param cityTown
	 *            {@link MultiLanguageProperty}
	 */
	public void setCityTown(MultiLanguageProperty cityTown) {
		addSubmodelElement(cityTown);
	}

	/**
	 * Sets town or city Note: mandatory property according to EU Machine Directive
	 * 2006/42/EC.
	 * 
	 * @param cityTown
	 *            {@link LangString}
	 */
	public void setCityTown(LangString cityTown) {
		MultiLanguageProperty cityTownProp = new MultiLanguageProperty(CITYTOWNID);
		cityTownProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO132#002", IdentifierType.IRDI)));
		cityTownProp.setValue(new LangStrings(cityTown));
		setCityTown(cityTownProp);
	}

	/**
	 * Gets federal state a part of a state
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getStateCounty() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(STATECOUNTYID));
	}

	/**
	 * Sets federal state a part of a state
	 * 
	 * @param stateCounty
	 *            {@link MultiLanguageProperty}
	 */
	public void setStateCounty(MultiLanguageProperty stateCounty) {
		addSubmodelElement(stateCounty);
	}

	/**
	 * Sets federal state a part of a state
	 * 
	 * @param stateCounty
	 *            {@link LangString}
	 */
	public void setStateCounty(LangString stateCounty) {
		MultiLanguageProperty stateCountyProp = new MultiLanguageProperty(STATECOUNTYID);
		stateCountyProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO133#002", IdentifierType.IRDI)));
		stateCountyProp.setValue(new LangStrings(stateCounty));
		setStateCounty(stateCountyProp);
	}

	/**
	 * Gets code of a country Note: Country codes defined accord. to DIN EN ISO
	 * 3166-1 Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getNationalCode() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(NATIONALCODEID));
	}

	/**
	 * Sets code of a country Note: Country codes defined accord. to DIN EN ISO
	 * 3166-1 Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param nationalCode
	 *            {@link MultiLanguageProperty}
	 */
	public void setNationalCode(MultiLanguageProperty nationalCode) {
		addSubmodelElement(nationalCode);
	}

	/**
	 * Sets code of a country Note: Country codes defined accord. to DIN EN ISO
	 * 3166-1 Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param nationalCode
	 *            {@link LangString}
	 */
	public void setNationalCode(LangString nationalCode) {
		MultiLanguageProperty nationalCodeProp = new MultiLanguageProperty(NATIONALCODEID);
		nationalCodeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO134#002", IdentifierType.IRDI)));
		nationalCodeProp.setValue(new LangStrings(nationalCode));
		setNationalCode(nationalCodeProp);
	}

	/**
	 * Gets VAT identification number of the business partner
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getVatNumber() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(VATNUMBERID));
	}

	/**
	 * Sets VAT identification number of the business partner
	 * 
	 * @param vatNumber
	 *            {@link MultiLanguageProperty}
	 */
	public void setVatNumber(MultiLanguageProperty vatNumber) {
		addSubmodelElement(vatNumber);
	}

	/**
	 * Sets VAT identification number of the business partner
	 * 
	 * @param vatNumber
	 *            {@link LangString}
	 */
	public void setVatNumber(LangString vatNumber) {
		MultiLanguageProperty vatNumberProp = new MultiLanguageProperty(VATNUMBERID);
		vatNumberProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO135#002", IdentifierType.IRDI)));
		vatNumberProp.setValue(new LangStrings(vatNumber));
		setVatNumber(vatNumberProp);
	}

	/**
	 * Gets plain text characterizing address information for which there is no
	 * property
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getAddressRemarks() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(ADDRESSREMARKSID));
	}

	/**
	 * Sets plain text characterizing address information for which there is no
	 * property
	 * 
	 * @param addressRemarks
	 *            {@link MultiLanguageProperty}
	 */
	public void setAddressRemarks(MultiLanguageProperty addressRemarks) {
		addSubmodelElement(addressRemarks);
	}

	/**
	 * Sets plain text characterizing address information for which there is no
	 * property
	 * 
	 * @param addressRemarks
	 *            {@link LangString}
	 */
	public void setAddressRemarks(LangString addressRemarks) {
		MultiLanguageProperty addressRemarksProp = new MultiLanguageProperty(ADDRESSREMARKSID);
		addressRemarksProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO202#003", IdentifierType.IRDI)));
		addressRemarksProp.setValue(new LangStrings(addressRemarks));
		setAddressRemarks(addressRemarksProp);
	}

	/**
	 * Gets web site address where information about the product or contact is given
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getAddressOfAdditionalLink() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(ADDRESSOFADDITIONALLINKID));
	}

	/**
	 * Sets web site address where information about the product or contact is given
	 * 
	 * @param addressOfAdditionalLink
	 *            {@link Property}
	 */
	public void setAddressOfAdditionalLink(Property addressOfAdditionalLink) {
		addSubmodelElement(addressOfAdditionalLink);
	}

	/**
	 * Sets web site address where information about the product or contact is given
	 * 
	 * @param addressOfAdditionalLink
	 *            {@link String}
	 */
	public void setAddressOfAdditionalLink(String addressOfAdditionalLink) {
		Property addressOfAdditionalLinkProp = new Property(ADDRESSOFADDITIONALLINKID, ValueType.String);
		addressOfAdditionalLinkProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAQ326#002", IdentifierType.IRDI)));
		addressOfAdditionalLinkProp.setValue(addressOfAdditionalLink);
		setAddressOfAdditionalLink(addressOfAdditionalLinkProp);
	}

	/**
	 * Gets Phone number including type
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Phone> getPhone() {
		List<Phone> ret = new ArrayList<Phone>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(PHONEPREFIX, getSubmodelElements());

		for (ISubmodelElement element : elements) {
			ret.add(Phone.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}

	/**
	 * Sets Phone number including type
	 * 
	 * @param phones
	 */
	public void setPhone(List<Phone> phones) {
		if (phones != null && phones.size() > 0) {
			for (Phone phone : phones) {
				addSubmodelElement(phone);
			}
		}
	}

	/**
	 * Gets fax number including type
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Fax> getFax() {
		List<Fax> ret = new ArrayList<Fax>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(FAXPREFIX, getSubmodelElements());

		for (ISubmodelElement element : elements) {
			ret.add(Fax.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}

	/**
	 * Sets fax number including type
	 * 
	 * @param faxes
	 */
	public void setFax(List<Fax> faxes) {
		if (faxes != null && faxes.size() > 0) {
			for (Fax fax : faxes) {
				addSubmodelElement(fax);
			}
		}
	}

	/**
	 * Gets E-mail address and encryption method
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Email> getEmail() {
		List<Email> ret = new ArrayList<Email>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(EMAILPREFIX, getSubmodelElements());

		for (ISubmodelElement element : elements) {
			ret.add(Email.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}

	/**
	 * Sets E-mail address and encryption method
	 * 
	 * @param emails
	 */
	public void setEmail(List<Email> emails) {
		if (emails != null && emails.size() > 0) {
			for (Email email : emails) {
				addSubmodelElement(email);
			}
		}
	}
}
