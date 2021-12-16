/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
