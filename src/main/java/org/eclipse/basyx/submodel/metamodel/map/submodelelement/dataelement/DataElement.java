/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;

public class DataElement extends SubmodelElement implements IDataElement {
	public static final String MODELTYPE = "DataElement";

	public DataElement() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	/**
	 * Constructor with mandatory attribute
	 * @param idShort
	 */
	public DataElement(String idShort) {
		super(idShort);
		
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Creates a DataElement object from a map
	 * 
	 * @param obj a DataElement object as raw map
	 * @return a DataElement object, that behaves like a facade for the given map
	 */
	public static DataElement createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(DataElement.class, obj);
		}
		
		DataElement facade = new DataElement();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj);
	}
	
	/**
	 * Returns true if the given submodel element map is recognized as a data element
	 */
	public static boolean isDataElement(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		return MODELTYPE.equals(modelType) || Property.isProperty(map) || Blob.isBlob(map) || File.isFile(map)
				|| Range.isRange(map) || MultiLanguageProperty.isMultiLanguageProperty(map)
				|| ReferenceElement.isReferenceElement(map);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.DATAELEMENT;
	}

	@Override
	public DataElement getLocalCopy() {
		// Return a shallow copy
		DataElement copy = new DataElement();
		copy.putAll(this);
		return copy;
	}
}
