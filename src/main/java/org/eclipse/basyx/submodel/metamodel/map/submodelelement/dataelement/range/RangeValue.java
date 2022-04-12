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
