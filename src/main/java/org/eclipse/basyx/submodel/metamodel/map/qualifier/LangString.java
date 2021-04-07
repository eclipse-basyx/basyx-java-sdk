/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * This class holds a description in a single language
 * 
 * @author haque
 *
 */
public class LangString extends VABModelMap<Object> {
	private static final String LANGUAGE = "language";
	private static final String DESCRIPTION = "text";
	
	private LangString() {
	}
	
	/**
	 * Constructor that accepts a language and a description
	 * @param language
	 * @param description
	 */
	public LangString(String language, String description) {
		put(LANGUAGE, language);
		put(DESCRIPTION, description);
	}
	
	/**
	 * Creates a LangString object from a map
	 * 
	 * @param map a LangString object as raw map
	 * @return a LangString object, that behaves like a facade for
	 *         the given map
	 */
	public static LangString createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		LangString ret = new LangString();
		ret.setMap(map);
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isLangString(Object value) {
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		return map.get(LANGUAGE) instanceof String && map.get(DESCRIPTION) instanceof String;
	}
	
	/**
	 * Get Language of the langString
	 * @return Language
	 */
	public String getLanguage() {
		return (String) get(LANGUAGE);
	}
	
	/**
	 * Get Description of the langString
	 * @return Description
	 */
	public String getDescription() {
		return (String) get(DESCRIPTION);
	} 
}
