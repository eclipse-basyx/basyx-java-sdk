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
package org.eclipse.basyx.vab.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Removes type information similar to what a communication over VAB would do
 * <br>
 * Additionally, Sets are changed to Lists
 * 
 * @author rajashek
 *
 */
public class TypeDestroyer {
	
	/**
	 * Removes type information of all objects within the map, i.e. every subclass
	 * of LinkedHashMap is reduced to LinkedHashMap
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> destroyType(Map<String, Object> map) {
		return (Map<String, Object>) handle(map);
	}
	
	@SuppressWarnings("unchecked")
	public static Object handle(Object o) {
		if(o instanceof Map) {
			return handleMap((Map<String, Object>) o);
		} else if (o instanceof Set) {
			return handleSet((Set<Object>) o);
		} else if (o instanceof List) {
			return handleList((List<Object>) o);
		} else {
			return o;
		}
	}
	
	private static List<Object> handleSet(Set<Object> set) {
		List<Object> ret = new ArrayList<>();
		for (Object o : set) {
			ret.add(handle(o));
		}
		return ret;
	}

	private static List<Object> handleList(List<Object> list) {
		List<Object> ret = new ArrayList<>();
		for (Object o : list) {
			ret.add(handle(o));
		}
		return ret;
	}

	private static Map<String, Object> handleMap(Map<String, Object> map) {
		Map<String, Object> ret = new LinkedHashMap<>();
		for (Entry<String, Object> entry : map.entrySet()) {
			ret.put(entry.getKey(), handle(entry.getValue()));
		}
		return ret;
	}
}
