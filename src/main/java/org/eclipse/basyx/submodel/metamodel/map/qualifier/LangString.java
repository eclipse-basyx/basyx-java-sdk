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
	 * 
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
	 * @param map
	 *            a LangString object as raw map
	 * @return a LangString object, that behaves like a facade for the given map
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
		if (!(value instanceof Map<?, ?>)) {
			return false;
		}

		Map<String, Object> map = (Map<String, Object>) value;

		return map.get(LANGUAGE) instanceof String && map.get(DESCRIPTION) instanceof String;
	}

	/**
	 * Get Language of the langString
	 * 
	 * @return Language
	 */
	public String getLanguage() {
		return (String) get(LANGUAGE);
	}

	/**
	 * Get Description of the langString
	 * 
	 * @return Description
	 */
	public String getDescription() {
		return (String) get(DESCRIPTION);
	}
}
