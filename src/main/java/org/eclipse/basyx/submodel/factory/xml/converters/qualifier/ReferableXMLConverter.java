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

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IReferable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Handles the conversion between an IReferable object and the XML tags<br>
 * &lt;aas:idShort&gt;, &lt;aas:category&gt;, &lt;aas:parent&gt; and
 * &lt;aas:description&gt; in both directions
 * 
 * @author conradi
 *
 */
public class ReferableXMLConverter {
	private static final Logger logger = LoggerFactory.getLogger(AdministrativeInformation.class);

	public static final String ID_SHORT = "aas:idShort";
	public static final String CATEGORY = "aas:category";
	public static final String PARENT = "aas:parent";
	public static final String DESCRIPTION = "aas:description";

	/**
	 * Populates a given Referable object with the data form the given XML
	 * 
	 * @param xmlObject
	 *            the XML map containing the tag relevant for IReferable
	 * @param referable
	 *            the Referable object to be populated
	 */
	@SuppressWarnings("unchecked")
	public static void populateReferable(Map<String, Object> xmlObject, Referable referable) {
		String idShort = XMLHelper.getString(xmlObject.get(ID_SHORT));
		String category = XMLHelper.getString(xmlObject.get(CATEGORY));

		LangStrings description = parseDescription(xmlObject);

		Reference parent = ReferenceXMLConverter.parseReference((Map<String, Object>) xmlObject.get(PARENT));
		if (Strings.isNullOrEmpty(idShort)) {
			// Warning without exception for empty idshort for parsing external aasx-files
			logger.warn("Invalid XML of Referable. No valid idShort is present. " + xmlObject.toString());
		}
		referable.setIdShort(idShort);

		if (!Strings.isNullOrEmpty(category)) {
			referable.setCategory(category);
		}
		if (description != null) {
			referable.setDescription(description);
		}
		if (parent != null) {
			referable.setParent(parent);
		}
	}

	/**
	 * Parses the &lt;aas:description&gt; tag
	 * 
	 * @param xmlObject
	 *            the XML map containing the &lt;aas:description&gt; tag
	 * @return a LangStrings Object parsed form the XML
	 */
	private static LangStrings parseDescription(Map<String, Object> xmlObject) {
		Object descObj = xmlObject.get(DESCRIPTION);

		if (descObj == null) {
			return null;
		}

		return LangStringsXMLConverter.parseLangStrings(descObj, LangStringsXMLConverter.LANG_STRING);
	}

	/**
	 * Populates a given XML map with the data from a given IReferable object<br>
	 * Creates the relevant tags in the given root
	 * 
	 * @param document
	 *            the XML document
	 * @param root
	 *            the XML root Element to be populated
	 * @param referable
	 *            the IReferable object to be converted to XML
	 */
	public static void populateReferableXML(Document document, Element root, IReferable referable) {
		if (referable.getIdShort() != null) {
			Element idShortElem = document.createElement(ID_SHORT);
			idShortElem.appendChild(document.createTextNode(referable.getIdShort()));
			root.appendChild(idShortElem);
		}

		if (referable.getCategory() != null) {
			Element categoryElem = document.createElement(CATEGORY);
			categoryElem.appendChild(document.createTextNode(referable.getCategory()));
			root.appendChild(categoryElem);
		}

		if (referable.getDescription() != null && !referable.getDescription().isEmpty()) {
			Element descriptionRoot = document.createElement(DESCRIPTION);
			LangStringsXMLConverter.buildLangStringsXML(document, descriptionRoot, referable.getDescription());
			root.appendChild(descriptionRoot);
		}

		if (referable.getParent() != null) {
			Element xmlParent = ReferenceXMLConverter.buildReferenceXML(document, referable.getParent());
			Element parentElem = document.createElement(PARENT);
			parentElem.appendChild(xmlParent);
			root.appendChild(parentElem);
		}

	}

}
