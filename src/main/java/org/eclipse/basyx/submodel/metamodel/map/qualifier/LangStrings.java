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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This Class is a List, which holds LangString Objects <br>
 * It is used to hold a text in multiple languages
 * 
 * @author conradi, haque
 *
 */
public class LangStrings extends HashSet<LangString> {
	private static final long serialVersionUID = 1L;

	public LangStrings() {
	}

	/**
	 * Constructor taking a language and a description in that language
	 * 
	 * @param language
	 * @param description
	 */
	public LangStrings(String language, String description) {
		add(new LangString(language, description));
	}

	/**
	 * Constructor taking a single LangString
	 * 
	 * @param langString
	 *            single LangString to add
	 */
	public LangStrings(LangString langString) {
		add(langString);
	}

	/**
	 * Constructor taking a collection of LangString
	 * 
	 * @param langStrings
	 *            collection of LangString to add
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
	 * @param maps
	 *            a LangStrings object as raw collection of map
	 * @return a LangStrings object, that behaves like a facade for the given map
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

	/**
	 * QoL method which creates a <code>LangStrings</code> with the specified
	 * strings. The strings must be given in pairs, such that the first string is
	 * the language code and the second is the text.
	 * 
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	// Creates a LangStrings with two languages:
	 * 	LangStrings ls1 = LangStrings.fromStringPairs("en", "Manual", "de", "Betriebsanleitung");
	 * 
	 * 	// Throws an exception:
	 * 	LangStrings ls2 = LangStrings.fromStringPairs("en");
	 * }
	 * </pre>
	 * 
	 * @param strings
	 *            A even-numbered set of strings where every pair of two strings
	 *            describes one LangString.
	 * 
	 * @return A new instance of <code>LangStrings</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>strings</code> contains an odd number of elements.
	 */
	public static LangStrings fromStringPairs(String... strings) {
		if ((strings.length % 2) == 1) {
			throw new IllegalArgumentException("strings must have an even number of items.");
		}

		LangStrings result = new LangStrings();
		for (int i = 0; i < strings.length; i = i + 2) {
			result.add(new LangString(strings[i], strings[i + 1]));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static boolean isLangStrings(Object value) {
		if (!(value instanceof Collection<?>)) {
			return false;
		}

		Collection<Map<String, Object>> collection = (Collection<Map<String, Object>>) value;

		return collection.stream().allMatch(LangString::isLangString);
	}

	/**
	 * 
	 * @param language
	 * @return The String for the specified language or <br>
	 *         an empty String if no matching LangString is found
	 */
	public String get(String language) {
		for (LangString langString : this) {
			String currLanguage = langString.getLanguage();
			if (currLanguage == null ? language == null : currLanguage.equalsIgnoreCase(language)) {
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
