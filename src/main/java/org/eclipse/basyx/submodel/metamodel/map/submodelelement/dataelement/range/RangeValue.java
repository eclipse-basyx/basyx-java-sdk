/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;


/**
 * Container class for holding the value of Range
 * 
 * @author conradi
 *
 */
public class RangeValue extends VABModelMap<Object> {

	private RangeValue() {}
	
	public RangeValue(Object min, Object max) {
		put(Range.MIN, min);
		put(Range.MAX, max);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isRangeValue(Object value) {
		
		// Given Object must be a Map
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		// Given Map must contain all necessary Entries
		return map.containsKey(Range.MIN) && map.containsKey(Range.MAX);
	}
	
	/**
	 * Creates a RangeValue object from a map
	 * 
	 * @param obj a RangeValue object as raw map
	 * @return a RangeValue object, that behaves like a facade for the given map
	 */
	public static RangeValue createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}
		
		RangeValue facade = new RangeValue();
		facade.setMap(obj);
		return facade;
	}
	
	public Object getMin() {
		return get(Range.MIN);
	}
	
	public Object getMax() {
		return get(Range.MAX);
	}
}
