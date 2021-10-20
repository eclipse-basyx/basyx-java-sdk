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

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;


/**
 * Container class for holding the value of RelationshipElement
 * 
 * @author conradi
 *
 */
public class RelationshipElementValue extends VABModelMap<Object> {

	protected RelationshipElementValue() {
	}
	
	public RelationshipElementValue(IReference first, IReference second) {
		put(RelationshipElement.FIRST, first);
		put(RelationshipElement.SECOND, second);
	}
	
	/**
	 * Creates a RelationshipElementValue object from a map
	 * 
	 * @param obj a RelationshipElementValue object as raw map
	 * @return a RelationshipElementValue object, that behaves like a facade for the given map
	 */
	public static RelationshipElementValue createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		RelationshipElementValue facade = new RelationshipElementValue();
		facade.setMap(obj);
		return facade;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isRelationshipElementValue(Object value) {
		
		// Given Object must be a Map
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		return Reference.isReference(map.get(RelationshipElement.FIRST))
				&& Reference.isReference(map.get(RelationshipElement.SECOND));
	}
	
	@SuppressWarnings("unchecked")
	public IReference getFirst() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.FIRST));
	}
	
	@SuppressWarnings("unchecked")
	public IReference getSecond() {
		return Reference.createAsFacade((Map<String, Object>) get(RelationshipElement.SECOND));
	}
	
}
