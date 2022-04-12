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

package org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

/**
 * ProductClassificationItem as described in the Submodel Template AAS Technical Data Document
 * 
 * It is a submodel element collection which contains Single product 
 * classification by association with product class in a particular 
 * classification system or property dictionary.
 * 
 * @author haque
 *
 */
public class ProductClassificationItem extends SubmodelElementCollection {
	public static final String PRODUCTCLASSIFICATIONSYSTEMID = "ProductClassificationSystem";
	public static final String CLASSIFICATIONSYSTEMVERSIONID = "ClassificationSystemVersion";
	public static final String PRODUCTCLASSID = "ProductClassId";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ProductClassificationItem/1/1", KeyType.IRI));
	
	private ProductClassificationItem() {
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param productClassificationSystem
	 * @param productClassId
	 */
	public ProductClassificationItem(String idShort, Property productClassificationSystem, Property productClassId) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setProductClassificationSystem(productClassificationSystem);
		setProductClassId(productClassId);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param productClassificationSystem
	 * @param productClassId
	 */
	public ProductClassificationItem(String idShort, String productClassificationSystem, String productClassId) {
		super(idShort);
		setSemanticId(SEMANTICID);
		setProductClassificationSystem(productClassificationSystem);
		setProductClassId(productClassId);
	}
	
	/**
	 * Creates a ProductClassificationItem SMC object from a map
	 * 
	 * @param obj a ProductClassificationItem SMC object as raw map
	 * @return a ProductClassificationItem SMC object, that behaves like a facade for the given map
	 */
	public static ProductClassificationItem createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(ProductClassificationItem.class, obj);
		}
		
		ProductClassificationItem productClassificationItem = new ProductClassificationItem();
		productClassificationItem.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return productClassificationItem;
	}
	
	/**
	 * Creates a ProductClassificationItem SMC object from a map without validation
	 * 
	 * @param obj a ProductClassificationItem SMC object as raw map
	 * @return a ProductClassificationItem SMC object, that behaves like a facade for the given map
	 */
	private static ProductClassificationItem createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		ProductClassificationItem productClassificationItem = new ProductClassificationItem();
		productClassificationItem.setMap(SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return productClassificationItem;
	}
	
	/**
	 * Check whether all mandatory elements for ProdictClassificationSystem SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		ProductClassificationItem productClassificationItem = createAsFacadeNonStrict(obj);
		return SubmodelElementCollection.isValid(obj)
				&& Property.isValid((Map<String, Object>) productClassificationItem.getProductClassificationSystem())
				&& Property.isValid((Map<String, Object>) productClassificationItem.getProductClassId());
	}
	
	/**
	 * Sets common name of the classification system.
	 * 
	 * Note: Examples for common names for classification systems are "ECLASS" or "IEC CDD".
	 * @param system
	 */
	public void setProductClassificationSystem(Property system) {
		addSubmodelElement(system);
	}
	
	/**
	 * Sets common name of the classification system.
	 * 
	 * Note: Examples for common names for classification systems are "ECLASS" or "IEC CDD".
	 * @param system
	 */
	public void setProductClassificationSystem(String system) {
		Property productClassificationSystemProp = new Property(PRODUCTCLASSIFICATIONSYSTEMID, ValueType.String);
		productClassificationSystemProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ProductClassificationSystem/1/1", IdentifierType.IRI)));
		productClassificationSystemProp.setValue(system);
		setProductClassificationSystem(productClassificationSystemProp);
	}
	
	/**
	 * Gets common name of the classification system.
	 * 
	 * Note: Examples for common names for classification systems are "ECLASS" or "IEC CDD".
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getProductClassificationSystem() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(PRODUCTCLASSIFICATIONSYSTEMID));
	}
	
	/**
	 * Sets common version identifier of the used classification system, in order to distinguish different version of the property dictionary.
	 * 
	 * Note: Casing is to be ignored.
	 * @param version
	 */
	public void setClassificationSystemVersion(Property version) {
		addSubmodelElement(version);
	}
	
	/**
	 * Sets common version identifier of the used classification system, in order to distinguish different version of the property dictionary.
	 * 
	 * Note: Casing is to be ignored.
	 * @param version
	 */
	public void setClassificationSystemVersion(String version) {
		Property versionProp = new Property(CLASSIFICATIONSYSTEMVERSIONID, ValueType.String);
		versionProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ClassificationSystemVersion/1/1", IdentifierType.IRI)));
		versionProp.setValue(version);
		setClassificationSystemVersion(versionProp);
	}
	
	/**
	 * Gets common version identifier of the used classification system, in order to distinguish different version of the property dictionary.
	 * 
	 * Note: Casing is to be ignored.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getClassificationSystemVersion() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(CLASSIFICATIONSYSTEMVERSIONID));
	}
	
	/**
	 * Sets class of the associated product or industrial equipment in the classification system. According to the notation of the system.
	 * 
	 * Note: Ideally, the Property/valueId is used to reference the IRI/ IRDI of the product class.
	 * @param id
	 */
	public void setProductClassId(Property id) {
		addSubmodelElement(id);
	}
	
	/**
	 * Sets class of the associated product or industrial equipment in the classification system. According to the notation of the system.
	 * 
	 * Note: Ideally, the Property/valueId is used to reference the IRI/ IRDI of the product class.
	 * @param id
	 */
	public void setProductClassId(String id) {
		Property idProp = new Property(PRODUCTCLASSID, ValueType.String);
		idProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ProductClassId/1/1", IdentifierType.IRI)));
		idProp.setValue(id);
		setProductClassId(idProp);
	}
	
	/**
	 * Gets class of the associated product or industrial equipment in the classification system. According to the notation of the system.
	 * 
	 * Note: Ideally, the Property/valueId is used to reference the IRI/ IRDI of the product class.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public IProperty getProductClassId() {
		return Property.createAsFacade((Map<String, Object>) getSubmodelElement(PRODUCTCLASSID));
	}
}
