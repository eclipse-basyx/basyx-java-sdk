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

package org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.generalinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * GeneralInformation as described in the Submodel Template AAS Technical Data Document
 * 
 * It is a submodel element collection which contains General information, for example ordering and manufacturer information.
 * 
 * @author haque
 *
 */
public class GeneralInformation extends SubmodelElementCollection{
	public static final String IDSHORT = "GeneralInformation";
	public static final String MANUFACTURERNAMEID = "ManufacturerName";
	public static final String MANUFACTURERLOGOID = "ManufacturerLogo";
	public static final String MANUFACTURERPRODUCTDESIGNATIONID = "ManufacturerProductDesignation";
	public static final String MANUFACTURERPARTNUMBERID = "ManufacturerPartNumber";
	public static final String MANUFACTURERORDERCODEID = "ManufacturerOrderCode";
	public static final String PRODUCTIMAGEPREFIX = "ProductImage";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/GeneralInformation/1/1", KeyType.IRI));
	
	private GeneralInformation() {
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param manufacturerPartNumber
	 * @param manufacturerOrderCode
	 */
	public GeneralInformation(Property manufacturerName, MultiLanguageProperty manufacturerProductDesignation, Property manufacturerPartNumber, Property manufacturerOrderCode) {
		this(IDSHORT, manufacturerName, manufacturerProductDesignation, manufacturerPartNumber, manufacturerOrderCode);
	}
	
	/**
	 * Constructor with default idShort
	 * 
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param manufacturerPartNumber
	 * @param manufacturerOrderCode
	 */
	public GeneralInformation(String manufacturerName, LangString manufacturerProductDesignation, String manufacturerPartNumber, String manufacturerOrderCode) {
		this(IDSHORT, manufacturerName, manufacturerProductDesignation, manufacturerPartNumber, manufacturerOrderCode);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param manufacturerPartNumber
	 * @param manufacturerOrderCode
	 */
	public GeneralInformation(String idShort, Property manufacturerName, MultiLanguageProperty manufacturerProductDesignation, Property manufacturerPartNumber, Property manufacturerOrderCode) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setManufacturerName(manufacturerName);
		setManufacturerProductDesignation(manufacturerProductDesignation);
		setManufacturerPartNumber(manufacturerPartNumber);
		setManufacturerOrderCode(manufacturerOrderCode);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param manufacturerName
	 * @param manufacturerProductDesignation
	 * @param manufacturerPartNumber
	 * @param manufacturerOrderCode
	 */
	public GeneralInformation(String idShort, String manufacturerName, LangString manufacturerProductDesignation, String manufacturerPartNumber, String manufacturerOrderCode) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setManufacturerName(manufacturerName);
		setManufacturerProductDesignation(manufacturerProductDesignation);
		setManufacturerPartNumber(manufacturerPartNumber);
		setManufacturerOrderCode(manufacturerOrderCode);
	}
	
	/**
	 * Creates a GeneralInformation SMC object from a map
	 * 
	 * @param obj a GeneralInformation SMC object as raw map
	 * @return a GeneralInformation SMC object, that behaves like a facade for the given map
	 */
	public static GeneralInformation createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(GeneralInformation.class, obj);
		}
		
		GeneralInformation generalInformation = new GeneralInformation();
		generalInformation.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return generalInformation;
	}
	
	/**
	 * Creates a GeneralInformation SMC object from a map without validation
	 * 
	 * @param obj a GeneralInformation SMC object as raw map
	 * @return a GeneralInformation SMC object, that behaves like a facade for the given map
	 */
	private static GeneralInformation createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		GeneralInformation generalInformation = new GeneralInformation();
		generalInformation.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return generalInformation;
	}
	
	/**
	 * Check whether all mandatory elements for GeneralInformation SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		GeneralInformation generalInformation = createAsFacadeNonStrict(obj);
		return SubmodelElementCollection.isValid(obj)
				&& Property.isValid((Map<String, Object>) generalInformation.getManufacturerName())
				&& MultiLanguageProperty.isValid((Map<String, Object>) generalInformation.getManufacturerProductDesignation())
				&& Property.isValid((Map<String, Object>) generalInformation.getManufacturerPartNumber())
				&& Property.isValid((Map<String, Object>) generalInformation.getManufacturerOrderCode());
	}
	
	/**
	 * Sets legally valid designation of the natural or judicial body which is directly responsible for the design, production, packaging and labeling of a product in respect to its being brought into the market.
	 * 
	 * @param name
	 */
	public void setManufacturerName(Property name) {
		addSubmodelElement(name);
	}
	
	/**
	 * Sets legally valid designation of the natural or judicial body which is directly responsible for the design, production, packaging and labeling of a product in respect to its being brought into the market.
	 * 
	 * @param name
	 */
	public void setManufacturerName(String name) {
		Property nameProp = new Property(MANUFACTURERNAMEID, ValueType.String);
		nameProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ManufacturerName/1/1", IdentifierType.IRI)));
		nameProp.setValue(name);
		setManufacturerName(nameProp);
	}
	
	/**
	 * Gets legally valid designation of the natural or judicial body which is directly responsible for the design, production, packaging and labeling of a product in respect to its being brought into the market.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getManufacturerName() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERNAMEID));
	}
	
	/**
	 * Sets imagefile for logo of manufacturer provided in common format (.png, .jpg).
	 * 
	 * @param logo
	 */
	public void setManufacturerLogo(File logo) {
		addSubmodelElement(logo);
	}
	
	/**
	 * Gets imagefile for logo of manufacturer provided in common format (.png, .jpg).
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IFile getManufacturerLogo() {
		return File.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERLOGOID));
	}
	
	/**
	 * Sets product designation as given by the mnaufacturer. Short description of the product, product group or function (short text) in common language.
	 * 
	 * Note: Whenever possible, a multi-language definition is preferred.
	 * 
	 * @param designation {@link MultiLanguageProperty}
	 */
	public void setManufacturerProductDesignation(MultiLanguageProperty designation) {
		addSubmodelElement(designation);
	}
	
	/**
	 * Sets product designation as given by the mnaufacturer. Short description of the product, product group or function (short text) in common language.
	 * 
	 * Note: Whenever possible, a multi-language definition is preferred.
	 * 
	 * @param designation {@link LangString}
	 */
	public void setManufacturerProductDesignation(LangString designation) {
		MultiLanguageProperty designationProp = new MultiLanguageProperty(MANUFACTURERPRODUCTDESIGNATIONID);
		designationProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ManufacturerProductDesignation/1/1", IdentifierType.IRI)));
		designationProp.setValue(new LangStrings(designation));
		setManufacturerProductDesignation(designationProp);
	}
	
	/**
	 * Gets product designation as given by the mnaufacturer. Short description of the product, product group or function (short text) in common language.
	 * 
	 * Note: Whenever possible, a multi-language definition is preferred.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IMultiLanguageProperty getManufacturerProductDesignation() {
		return MultiLanguageProperty.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERPRODUCTDESIGNATIONID));
	}
	
	/**
	 * Sets unique product identifier of the manufacturer for the product type respective the type designation of the industrial equipemnt.
	 * 
	 * Note: The Manufacturer part number is represented as a string, although often a numerical id.
	 * @param partNumber
	 */
	public void setManufacturerPartNumber(Property partNumber) {
		addSubmodelElement(partNumber);
	}
	
	/**
	 * Sets unique product identifier of the manufacturer for the product type respective the type designation of the industrial equipemnt.
	 * 
	 * Note: The Manufacturer part number is represented as a string, although often a numerical id.
	 * @param partNumber
	 */
	public void setManufacturerPartNumber(String partNumber) {
		Property partNumberProp = new Property(MANUFACTURERPARTNUMBERID, ValueType.String);
		partNumberProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ManufacturerPartNumber/1/1", IdentifierType.IRI)));
		partNumberProp.setValue(partNumber);
		setManufacturerPartNumber(partNumberProp);
	}
	
	/**
	 * Gets unique product identifier of the manufacturer for the product type respective the type designation of the industrial equipemnt.
	 * 
	 * Note: The Manufacturer part number is represented as a string, although often a numerical id.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getManufacturerPartNumber() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERPARTNUMBERID));
	}
	
	/**
	 * Sets unique product identifier of the manufacturer sufficient to order the exact same product.
	 * 
	 * @param orderCode
	 */
	public void setManufacturerOrderCode(Property orderCode) {
		addSubmodelElement(orderCode);
	}
	
	/**
	 * Sets unique product identifier of the manufacturer sufficient to order the exact same product.
	 * 
	 * @param orderCode
	 */
	public void setManufacturerOrderCode(String orderCode) {
		Property orderCodeProp = new Property(MANUFACTURERORDERCODEID, ValueType.String);
		orderCodeProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ManufacturerOrderCode/1/1", IdentifierType.IRI)));
		orderCodeProp.setValue(orderCode);
		setManufacturerOrderCode(orderCodeProp);
	}
	
	/**
	 * Gets unique product identifier of the manufacturer sufficient to order the exact same product.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getManufacturerOrderCode() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(MANUFACTURERORDERCODEID));
	}
	
	/**
	 * Sets image file for associated product provided in common format (.png, .jpg).
	 * 
	 * @param images
	 */
	public void setProductImages(List<File> images) {
		if (images != null && images.size() > 0) {
			for (File image : images) {
				addSubmodelElement(image);
			}
		}
	}
	
	/**
	 * Gets image file for associated product provided in common format (.png, .jpg).
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<IFile> getProductImages() {
		List<IFile> ret = new ArrayList<IFile>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(PRODUCTIMAGEPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(File.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
}
