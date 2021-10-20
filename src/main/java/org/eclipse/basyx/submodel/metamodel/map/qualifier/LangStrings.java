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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This Class is a List, which holds LangString Objects <br/>
 * It is used to hold a text in multiple languages
 * 
 * @author conradi, haque
 *
 */
public class LangStrings extends HashSet<LangString> {
	private static final long serialVersionUID = 1L;
	
	public LangStrings() {}
	
	/**
	 * Constructor taking a language and a description in that language
	 * @param language 
	 * @param description
	 */
	public LangStrings(String language, String description) {
		add(new LangString(language, description));
	}
	
	/**
	 * Constructor taking a single LangString
	 * @param langString single LangString to add
	 */
	public LangStrings(LangString langString) {
		add(langString);
	}
	
	/**
	 * Constructor taking a collection of LangString
	 * @param langStrings collection of LangString to add
	 */
	public LangStrings(Collection<LangString> langStrings) {
		if (langStrings != null) {
			langStrings.stream().forEach(ls -> {
				add(ls);
			});
		}
	}
	
	/**
	 * Creates a LangStrings object from a collection of map
	 * 
	 * @param obj a LangStrings object as raw collection of map
	 * @return a LangStrings object, that behaves like a facade for
	 *         the given map
	 */
	public static LangStrings createAsFacade(Collection<Map<String, Object>> maps) {
		if (maps == null) {
			return null;
		}

		LangStrings ret = new LangStrings();
		for (Map<String, Object> map : maps) {
			LangString langString = LangString.createAsFacade(map);
			ret.add(langString);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isLangStrings(Object value) {
		if(!(value instanceof Collection<?>)) {
			return false;
		}
		
		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) value;
		
		return collection.stream().allMatch(LangString::isLangString);
	}

	/**
	 * 
	 * @param language
	 * @return The String for the specified language or <br/>
	 * an empty String if no matching LangString is found
	 */
	public String get(String language) {
		for (LangString langString : this) {
			String currLanguage = langString.getLanguage();
			if(currLanguage == null ? language == null : currLanguage.equalsIgnoreCase(language)) {
				return langString.getDescription();
			}
		}
		return "";
	}
	
	/**
	 * @return A Set of Strings containing all languages of this LangStrings Object
	 */
	public Set<String> getLanguages() {
		HashSet<String> languageSet = new HashSet<>();
		for (LangString langString : this) {
			languageSet.add(langString.getLanguage());
		}
		return languageSet;
	}
}
