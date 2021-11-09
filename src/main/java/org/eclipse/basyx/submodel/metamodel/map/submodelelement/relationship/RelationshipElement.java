/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

/**
 * RelationshipElement as defined in DAAS document <br>
 * A relationship element is used to define a relationship between two referable
 * elements.
 * 
 * 
 * @author schnicke
 *
 */
public class RelationshipElement extends SubmodelElement implements IRelationshipElement {
	public static final String FIRST = "first";
	public static final String SECOND = "second";
	public static final String MODELTYPE = "RelationshipElement";
	
	/**
	 * Constructor
	 */
	public RelationshipElement() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * 
	 * @param first
	 *            First element in the relationship taking the role of the subject.
	 * @param second
	 *            Second element in the relationship taking the role of the object.
	 */
	public RelationshipElement(IReference first, IReference second) {
		// Add model type
		putAll(new ModelType(MODELTYPE));
		
		put(FIRST, first);
		put(SECOND, second);
	}
	
	/**
	 * Constructor with only mandatory attributes
	 * @param idShort
	 * @param first
	 * @param second
	 */
	public RelationshipElement(String idShort, IReference first, IReference second) {
		this(first, second);
		setIdShort(idShort);
	}
	
	/**
	 * Creates a RelationshipElement object from a map
	 * 
	 * @param obj a RelationshipElement object as raw map
	 * @return a RelationshipElement object, that behaves like a facade for the given map
	 */
	public static RelationshipElement createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(RelationshipElement.class, obj);
		}
		
		
		RelationshipElement ret = new RelationshipElement();
		ret.setMap(obj);
		return ret;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj) &&
				obj.containsKey(FIRST) &&
				obj.containsKey(SECOND) &&
				Reference.isValid((Map<String, Object>)obj.get(FIRST)) &&
				Reference.isValid((Map<String, Object>)obj.get(SECOND));
	}

	/**
	 * Returns true if the given submodel element map is recognized as a RelationshipElement
	 */
	public static boolean isRelationshipElement(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained
		return MODELTYPE.equals(modelType) || (modelType == null && map.containsKey(FIRST) && map.containsKey(SECOND));
	}

	public void setFirst(IReference first) {
		put(RelationshipElement.FIRST, first);
		
	}

	@SuppressWarnings("unchecked")
	public IReference getFirst() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.FIRST));
	}

	public void setSecond(IReference second) {
		put(RelationshipElement.SECOND, second);
		
	}

	@SuppressWarnings("unchecked")
	public IReference getSecond() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.SECOND));
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.RELATIONSHIPELEMENT;
	}

	@Override
	public RelationshipElementValue getValue() {
		return new RelationshipElementValue(getFirst(), getSecond());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if(RelationshipElementValue.isRelationshipElementValue(value)) {
			RelationshipElementValue rev = 
					RelationshipElementValue.createAsFacade((Map<String, Object>) value);
			setValue(rev);
		} else {
			throw new IllegalArgumentException("Given Object is not an RelationshipElementValue");
		}
	}

	@Override
	public RelationshipElement getLocalCopy() {
		// Return a shallow copy
		RelationshipElement copy = new RelationshipElement();
		copy.putAll(this);
		return copy;
	}

	@Override
	public void setValue(RelationshipElementValue value) {
		setFirst(value.getFirst());
		setSecond(value.getSecond());
	}
}
