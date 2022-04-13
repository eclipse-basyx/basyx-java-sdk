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
package org.eclipse.basyx.submodel.types.digitalnameplate;

import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Address;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.assetspecificproperties.AssetSpecificProperties;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings.Markings;

/**
 * DigitalNameplateSubmodel as defined in the AAS Digital Nameplate Template
 * document <br>
 * this contains the nameplate information attached to the product
 * 
 * @author haque
 *
 */
public class DigitalNameplateSubmodel extends Submodel {
	public static final String MANUFACTURERNAMEID = "ManufacturerName";
	public static final String MANUFACTURERPRODUCTDESIGNATIONID = "ManufacturerProductDesignation";
	public static final String ADDRESSID = "Address";
	public static final String MANUFACTURERPRODUCTFAMILYID = "ManufacturerProductFamily";
	public static final String SERIALNUMBERID = "SerialNumber";
	public static final String YEARSOFCONSTRUCTIONID = "YearOfConstruction";
	public static final String MARKINGSID = "Markings";
	public static final String ASSETSPECIFICPROPERTIESID = "AssetSpecificProperties";
	public static final Reference SEMANTICID = new Reference(Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/zvei/nameplate/1/0/Nameplate", KeyType.IRI)));
	public static final String SUBMODELID = "Nameplate";

	private DigitalNameplateSubmodel() {
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param identifier
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param address
	 * @param manufacturerProductFamily
	 * @param yearsOfConstruction
	 */
	public DigitalNameplateSubmodel(Identifier identifier, MultiLanguageProperty manufacturerName, MultiLanguageProperty manufacturerProductDesignation, Address address, MultiLanguageProperty manufacturerProductFamily,
			Property yearsOfConstruction) {
		this(SUBMODELID, identifier, manufacturerName, manufacturerProductDesignation, address, manufacturerProductFamily, yearsOfConstruction);
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param identifier
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param address
	 * @param manufacturerProductFamily
	 * @param yearsOfConstruction
	 */
	public DigitalNameplateSubmodel(Identifier identifier, LangString manufacturerName, LangString manufacturerProductDesignation, Address address, LangString manufacturerProductFamily, String yearsOfConstruction) {
		this(SUBMODELID, identifier, manufacturerName, manufacturerProductDesignation, address, manufacturerProductFamily, yearsOfConstruction);
	}

	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param identifier
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param address
	 * @param manufacturerProductFamily
	 * @param yearsOfConstruction
	 */
	public DigitalNameplateSubmodel(String idShort, Identifier identifier, MultiLanguageProperty manufacturerName, MultiLanguageProperty manufacturerProductDesignation, Address address, MultiLanguageProperty manufacturerProductFamily,
			Property yearsOfConstruction) {
		super(idShort, identifier);
		setSemanticId(SEMANTICID);
		setManufacturerName(manufacturerName);
		setManufacturerProductDesignation(manufacturerProductDesignation);
		setAddress(address);
		setManufacturerProductFamily(manufacturerProductFamily);
		setYearOfConstruction(yearsOfConstruction);
	}

	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param identifier
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param address
	 * @param manufacturerProductFamily
	 * @param yearsOfConstruction
	 */
	public DigitalNameplateSubmodel(String idShort, Identifier identifier, LangString manufacturerName, LangString manufacturerProductDesignation, Address address, LangString manufacturerProductFamily, String yearsOfConstruction) {
		super(idShort, identifier);
		setSemanticId(SEMANTICID);
		setManufacturerName(manufacturerName);
		setManufacturerProductDesignation(manufacturerProductDesignation);
		setAddress(address);
		setManufacturerProductFamily(manufacturerProductFamily);
		setYearOfConstruction(yearsOfConstruction);
	}

	/**
	 * Creates a DigitalNameplateSubmodel object from a map
	 * 
	 * @param obj
	 *            a DigitalNameplateSubmodel SMC object as raw map
	 * @return a DigitalNameplateSubmodel SMC object, that behaves like a facade for
	 *         the given map
	 */
	public static DigitalNameplateSubmodel createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(DigitalNameplateSubmodel.class, obj);
		}

		DigitalNameplateSubmodel ret = new DigitalNameplateSubmodel();
		ret.setMap(SubmodelElementMapCollectionConverter.mapToSM(obj));
		return ret;
	}

	/**
	 * Creates a DigitalNameplateSubmodel object from a map without validation
	 * 
	 * @param obj
	 *            a DigitalNameplateSubmodel SMC object as raw map
	 * @return a DigitalNameplateSubmodel SMC object, that behaves like a facade for
	 *         the given map
	 */
	private static DigitalNameplateSubmodel createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		DigitalNameplateSubmodel ret = new DigitalNameplateSubmodel();
		ret.setMap(SubmodelElementMapCollectionConverter.mapToSM(obj));
		return ret;
	}

	/**
	 * Check whether all mandatory elements for DigitalNameplateSubmodel exist in
	 * the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		DigitalNameplateSubmodel submodel = createAsFacadeNonStrict(obj);

		return Submodel.isValid(obj) && MultiLanguageProperty.isValid((Map<String, Object>) submodel.getManufacturerName()) && MultiLanguageProperty.isValid((Map<String, Object>) submodel.getManufacturerProductDesignation())
				&& Address.isValid(submodel.getAddress()) && MultiLanguageProperty.isValid((Map<String, Object>) submodel.getManufacturerProductFamily()) && Property.isValid((Map<String, Object>) submodel.getYearOfConstruction());
	}

	/**
	 * Sets manufacturerName legally valid designation of the natural or judicial
	 * person which is directly responsible for the design, production, packaging
	 * and labeling of a product in respect to its being brought into circulation
	 * Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerName
	 *            {@link MultiLanguageProperty}
	 */
	public void setManufacturerName(MultiLanguageProperty manufacturerName) {
		addSubmodelElement(manufacturerName);
	}

	/**
	 * Sets manufacturerName legally valid designation of the natural or judicial
	 * person which is directly responsible for the design, production, packaging
	 * and labeling of a product in respect to its being brought into circulation
	 * Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerName
	 *            {@link LangString}
	 */
	public void setManufacturerName(LangString manufacturerName) {
		MultiLanguageProperty manufacturerNameProp = new MultiLanguageProperty(MANUFACTURERNAMEID);
		manufacturerNameProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAO677#002", IdentifierType.IRDI)));
		manufacturerNameProp.setValue(new LangStrings(manufacturerName));
		setManufacturerName(manufacturerNameProp);
	}

	/**
	 * 
	 * Gets manufacturerName legally valid designation of the natural or judicial
	 * person which is directly responsible for the design, production, packaging
	 * and labeling of a product in respect to its being brought into circulation
	 * Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getManufacturerName() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERNAMEID));
	}

	/**
	 * Sets Short description of the product (short text) Note: mandatory property
	 * according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerProductDesignation
	 *            {@link MultiLanguageProperty}
	 */
	public void setManufacturerProductDesignation(MultiLanguageProperty manufacturerProductDesignation) {
		addSubmodelElement(manufacturerProductDesignation);
	}

	/**
	 * Sets Short description of the product (short text) Note: mandatory property
	 * according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerProductDesignation
	 *            {@link LangString}
	 */
	public void setManufacturerProductDesignation(LangString manufacturerProductDesignation) {
		MultiLanguageProperty manufacturerProductDesignationProp = new MultiLanguageProperty(MANUFACTURERPRODUCTDESIGNATIONID);
		manufacturerProductDesignationProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAW338#001", IdentifierType.IRDI)));
		manufacturerProductDesignationProp.setValue(new LangStrings(manufacturerProductDesignation));
		setManufacturerProductDesignation(manufacturerProductDesignationProp);
	}

	/**
	 * Gets Short description of the product (short text) Note: mandatory property
	 * according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getManufacturerProductDesignation() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERPRODUCTDESIGNATIONID));
	}

	/**
	 * Sets address information of a business partner
	 * 
	 * Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param address
	 */
	public void setAddress(Address address) {
		addSubmodelElement(address);
	}

	/**
	 * Gets address information of a business partner
	 * 
	 * Note: mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Address getAddress() {
		ISubmodelElement element = getSubmodelElement(ADDRESSID);
		return element == null ? null : Address.createAsFacade((Map<String, Object>) element);
	}

	/**
	 * Sets 2nd level of a 3 level manufacturer specific product hierarchy Note:
	 * mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerProductFamily
	 *            {@link MultiLanguageProperty}
	 */
	public void setManufacturerProductFamily(MultiLanguageProperty manufacturerProductFamily) {
		addSubmodelElement(manufacturerProductFamily);
	}

	/**
	 * Sets 2nd level of a 3 level manufacturer specific product hierarchy Note:
	 * mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param manufacturerProductFamily
	 *            {@link LangString}
	 */
	public void setManufacturerProductFamily(LangString manufacturerProductFamily) {
		MultiLanguageProperty manufacturerProductFamilyProp = new MultiLanguageProperty(MANUFACTURERPRODUCTFAMILYID);
		manufacturerProductFamilyProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAU731#001", IdentifierType.IRDI)));
		manufacturerProductFamilyProp.setValue(new LangStrings(manufacturerProductFamily));
		setManufacturerProductFamily(manufacturerProductFamilyProp);
	}

	/**
	 * Gets 2nd level of a 3 level manufacturer specific product hierarchy Note:
	 * mandatory property according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getManufacturerProductFamily() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERPRODUCTFAMILYID));
	}

	/**
	 * Sets unique combination of numbers and letters used to identify the device
	 * once it has been manufactured
	 * 
	 * @param serialNumber
	 */
	public void setSerialNumber(Property serialNumber) {
		addSubmodelElement(serialNumber);
	}

	/**
	 * Sets unique combination of numbers and letters used to identify the device
	 * once it has been manufactured
	 * 
	 * @param serialNumber
	 */
	public void setSerialNumber(String serialNumber) {
		Property serialNumberProp = new Property(SERIALNUMBERID, ValueType.String);
		serialNumberProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAM556#002", IdentifierType.IRDI)));
		serialNumberProp.setValue(serialNumber);
		setSerialNumber(serialNumberProp);
	}

	/**
	 * Gets unique combination of numbers and letters used to identify the device
	 * once it has been manufactured
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getSerialNumber() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(SERIALNUMBERID));
	}

	/**
	 * Sets year as completion date of object Note: mandatory property according to
	 * EU Machine Directive 2006/42/EC.
	 * 
	 * @param yearsOfConstruction
	 */
	public void setYearOfConstruction(Property yearsOfConstruction) {
		addSubmodelElement(yearsOfConstruction);
	}

	/**
	 * Sets year as completion date of object Note: mandatory property according to
	 * EU Machine Directive 2006/42/EC.
	 * 
	 * @param yearsOfConstruction
	 */
	public void setYearOfConstruction(String yearsOfConstruction) {
		Property yearsOfConstructionProp = new Property(YEARSOFCONSTRUCTIONID, ValueType.String);
		yearsOfConstructionProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "0173-1#02-AAP906#001", IdentifierType.IRDI)));
		yearsOfConstructionProp.setValue(yearsOfConstruction);
		setYearOfConstruction(yearsOfConstructionProp);
	}

	/**
	 * Gets year as completion date of object Note: mandatory property according to
	 * EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getYearOfConstruction() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(YEARSOFCONSTRUCTIONID));
	}

	/**
	 * Sets collection of product markings Note: CE marking is declared as mandatory
	 * according to EU Machine Directive 2006/42/EC.
	 * 
	 * @param markings
	 */
	public void setMarkings(Markings markings) {
		addSubmodelElement(markings);
	}

	/**
	 * Gets collection of product markings Note: CE marking is declared as mandatory
	 * according to EU Machine Directive 2006/42/EC.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Markings getMarkings() {
		ISubmodelElement element = getSubmodelElement(MARKINGSID);
		return element == null ? null : Markings.createAsFacade((Map<String, Object>) element);
	}

	/**
	 * Sets collection of guideline specific properties
	 * 
	 * @param assetSpecificProperties
	 */
	public void setAssetSpecificProperties(AssetSpecificProperties assetSpecificProperties) {
		addSubmodelElement(assetSpecificProperties);
	}

	/**
	 * Gets collection of guideline specific properties
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AssetSpecificProperties getAssetSpecificProperties() {
		ISubmodelElement element = getSubmodelElement(ASSETSPECIFICPROPERTIESID);
		return element == null ? null : AssetSpecificProperties.createAsFacade((Map<String, Object>) element);
	}
}
