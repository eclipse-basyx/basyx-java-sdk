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
package org.eclipse.basyx.submodel.factory.xml.converters.qualifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between a LangStrings object and the XML tag
 * &lt;aas:langString&gt; in both directions
 * 
 * @author conradi
 *
 */
public class LangStringsXMLConverter {

	public static final String LANG = "lang";
	public static final String LANG_STRING = "aas:langString";

	/**
	 * Parses the LangStrings object from XML
	 * 
	 * @param xmlObject
	 *            the XML map containing the &lt;aas:langString&gt; tags
	 * @return the parsed LangStrings object
	 */
	public static LangStrings parseLangStrings(Map<String, Object> xmlObject) {
		return parseLangStrings(xmlObject, LANG_STRING);
	}

	/**
	 * Parses the LangStrings object from XML/String with a custom lang string xml
	 * tag
	 * 
	 * @param obj
	 *            the LangString objects or a single string
	 * @param tagName
	 *            the custom &lt;*langString*&gt; tagName
	 * @return the parsed LangStrings object
	 */
	@SuppressWarnings("unchecked")
	public static LangStrings parseLangStrings(Object obj, String tagName) {
		if (obj == null) {
			return new LangStrings();
		} else if (obj instanceof Map) {
			return parseLangStringsFromMap((Map<String, Object>) obj, tagName);
		} else {
			return parseLangStringsFromString((String) obj);
		}
	}

	/**
	 * Parses the LangStrings object from String description
	 * 
	 * @param str
	 *            given description
	 * @return parsed LangStrings object
	 */
	private static LangStrings parseLangStringsFromString(String str) {
		LangString langString = new LangString("EN", str);
		return new LangStrings(langString);
	}

	/**
	 * Parses the LangStrings object from XML with a custom lang string xml tag
	 * 
	 * @param xmlObject
	 *            given XML object
	 * @param tagName
	 *            the custom &lt;*langString*&gt; tagName
	 * @return the parsed LangStrings object
	 */
	private static LangStrings parseLangStringsFromMap(Map<String, Object> xmlObject, String tagName) {
		LangStrings langStrings = new LangStrings();

		if (xmlObject != null) {
			List<Map<String, Object>> xmlLangStrings = XMLHelper.getList(xmlObject.get(tagName));
			for (Map<String, Object> xmlLangString : xmlLangStrings) {
				String text = XMLHelper.getString(xmlLangString.get(XMLHelper.TEXT));
				String lang = XMLHelper.getString(xmlLangString.get(LANG));
				langStrings.add(new LangString(lang, text));
			}
		}
		return langStrings;
	}

	/**
	 * Builds XML from a given LangStrings object
	 * 
	 * @param document
	 *            the XML document
	 * @param root
	 *            the root Element where the &lt;aas:langString&gt; tags should be
	 *            in
	 * @param langStrings
	 *            the LangStrings object to be converted to XML
	 */
	public static void buildLangStringsXML(Document document, Element root, LangStrings langStrings) {
		buildLangStringsXML(document, root, LANG_STRING, langStrings);
	}

	/**
	 * Builds XML from a given LangStrings object with a custom langString tagName
	 * 
	 * @param document
	 *            the XML document
	 * @param root
	 *            the root Element where the &lt;langString&gt; tags should be in
	 * @param tagName
	 *            the custom &lt;*langString*&gt; tagName
	 * @param langStrings
	 *            the LangStrings object to be converted to XML
	 */
	public static void buildLangStringsXML(Document document, Element root, String tagName, LangStrings langStrings) {
		if (langStrings != null) {
			Set<String> languages = langStrings.getLanguages();
			for (String language : languages) {
				Element langStringRoot = document.createElement(tagName);
				String text = langStrings.get(language);

				langStringRoot.setAttribute(LANG, language);
				langStringRoot.appendChild(document.createTextNode(text));

				root.appendChild(langStringRoot);
			}
		}
	}
}
