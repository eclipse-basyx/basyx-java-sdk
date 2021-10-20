/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.event;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.event.IBasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

/**
 * A BasicEvent element as defined in DAAS document
 * 
 * @author conradi
 *
 */
public class BasicEvent extends SubmodelElement implements IBasicEvent {

	public static final String MODELTYPE = "BasicEvent";
	public static final String OBSERVED = "observed";
	
	public BasicEvent() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}
	
	public BasicEvent(IReference observed) {
		this();
		put(OBSERVED, observed);
	}
	
	/**
	 * Constructor accepting only mandatory attribute
	 * @param idShort
	 * @param observed
	 */
	public BasicEvent(String idShort, IReference observed) {
		super(idShort);
		
		// Add model type
		putAll(new ModelType(MODELTYPE));
		put(OBSERVED, observed);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.BASICEVENT;
	}
	
	/**
	 * Creates a BasicEvent object from a map
	 * 
	 * @param obj a BasicEvent object as raw map
	 * @return a BasicEvent object, that behaves like a facade for the given map
	 */
	public static BasicEvent createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		if (!isValid(obj)) {
			throw new MetamodelConstructionException(BasicEvent.class, obj);			
		}
		
		BasicEvent facade = new BasicEvent();
		facade.setMap(obj);
		return facade;
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj) && obj.containsKey(OBSERVED);
	}
	
	/**
	 * Returns true if the given submodel element map is recognized as an BasicEvent element
	 */
	public static boolean isBasicEvent(Map<String, Object> map) {
		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are contained (fallback)
		return MODELTYPE.equals(modelType) || (modelType == null && map.containsKey(OBSERVED));
	}

	@Override
	@SuppressWarnings("unchecked")
	public IReference getObserved() {
		return Reference.createAsFacade((Map<String, Object>) get(OBSERVED));
	}

	@Override
	public IReference getValue() {
		return getObserved();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		if(Reference.isReference(value)) {
			put(OBSERVED, Reference.createAsFacade((Map<String, Object>) value));
		}
		else {
			throw new IllegalArgumentException("Given Object is not a Reference");
		}
	}

	@Override
	public BasicEvent getLocalCopy() {
		// Return a shallow copy
		BasicEvent copy = new BasicEvent();
		copy.putAll(this);
		return copy;
	}
}
