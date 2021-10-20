/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.types.helper.SubmodelElementRetrievalHelper;

/**
 * ProductClassifications as described in the Submodel Template AAS Technical Data Document
 * It is a submodel element collection which contains Product classifications by association with product classes in common classification systems.
 * 
 * @author haque
 *
 */
public class ProductClassifications extends SubmodelElementCollection {
	public static final String IDSHORT = "ProductClassifications";
	public static final Reference SEMANTICID = new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false, "https://admin-shell.io/ZVEI/TechnicalData/ProductClassifications/1/1", KeyType.IRI));
	public static final String PRODUCTCLASSIFICATIONITEMPREFIX = "ProductClassificationItem";
	
	/**
	 * Constructor with default idshort
	 */
	public ProductClassifications() {
		this(IDSHORT);
	}
	
	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 */
	public ProductClassifications(String idShort) {
		super(idShort);
		setProductClassificationItems(new ArrayList<ProductClassificationItem>());
	}
	
	/**
	 * Creates a ProductClassifications SMC object from a map
	 * 
	 * @param obj a ProductClassifications SMC object as raw map
	 * @return a ProductClassifications SMC object, that behaves like a facade for the given map
	 */
	public static ProductClassifications createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(ProductClassifications.class, obj);
		}
		
		ProductClassifications productClassifications = new ProductClassifications();
		productClassifications.setMap((Map<String, Object>)SubmodelElementMapCollectionConverter.mapToSmECollection(obj));
		return productClassifications;
	}
	
	/**
	 * Check whether all mandatory elements for ProductClassifications SMC
	 * exist in the map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElementCollection.isValid(obj);
	}
	
	/**
	 * Sets single product classification item by association with product class in a particular classification system or property dictionary
	 * 
	 * @param items
	 */
	public void setProductClassificationItems(List<ProductClassificationItem> items) {
		if (items != null && items.size() > 0) {
			for (ProductClassificationItem item: items) {
				addSubmodelElement(item);
			}
		}
	}
	
	/**
	 * Gets single product classification item by association with product class in a particular classification system or property dictionary
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ProductClassificationItem> getProductClassificationItems() {
		List<ProductClassificationItem> ret = new ArrayList<ProductClassificationItem>();
		List<ISubmodelElement> elements = SubmodelElementRetrievalHelper.getSubmodelElementsByIdPrefix(PRODUCTCLASSIFICATIONITEMPREFIX, getSubmodelElements());
		
		for (ISubmodelElement element: elements) {
			ret.add(ProductClassificationItem.createAsFacade((Map<String, Object>) element));
		}
		return ret;
	}
}
