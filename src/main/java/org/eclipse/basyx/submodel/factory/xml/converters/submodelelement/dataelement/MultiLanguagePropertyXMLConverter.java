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
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.LangStringsXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IMultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:multiLanguageProperty&gt; and builds the MultiLanguageProperty object from it <br>
 * Builds &lt;aas:multiLanguageProperty&gt; from a given MultiLanguageProperty object
 * 
 * @author conradi
 *
 */
public class MultiLanguagePropertyXMLConverter extends SubmodelElementXMLConverter {
	
	public static final String MULTI_LANGUAGE_PROPERTY = "aas:multiLanguageProperty";
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:multiLanguageProperty&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:multiLanguageProperty&gt;
	 * @return the parsed MultiLanguageProperty
	 */
	@SuppressWarnings("unchecked")
	public static MultiLanguageProperty parseMultiLanguageProperty(Map<String, Object> xmlObject) {
		Map<String, Object> xmlValueId = (Map<String, Object>) xmlObject.get(VALUE_ID);
		Reference valueId = ReferenceXMLConverter.parseReference(xmlValueId);
		Object xmlLangStrings = xmlObject.get(VALUE);
		LangStrings langStrings = LangStringsXMLConverter.parseLangStrings(xmlLangStrings, LangStringsXMLConverter.LANG_STRING);
		MultiLanguageProperty mLProperty = new MultiLanguageProperty(valueId, langStrings);
		populateSubmodelElement(xmlObject, mLProperty);
		return mLProperty;
	}


	
	/**
	 * Builds the &lt;aas:multiLanguageProperty&gt; XML tag for a MultiLanguageProperty
	 * 
	 * @param document the XML document
	 * @param mLProperty the IMultiLanguageProperty to build the XML for
	 * @return the &lt;aas:multiLanguageProperty&gt; XML tag for the given MultiLanguageProperty
	 */
	public static Element buildMultiLanguageProperty(Document document, IMultiLanguageProperty mLProperty) {
		Element mLPropertyRoot = document.createElement(MULTI_LANGUAGE_PROPERTY);
		populateSubmodelElement(document, mLPropertyRoot, mLProperty);
		
		IReference valueId = mLProperty.getValueId();
		if(valueId != null) {
			Element valueIdRoot = document.createElement(VALUE_ID);
			valueIdRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, valueId)); 
			mLPropertyRoot.appendChild(valueIdRoot);
		}	
		
		Element valueRoot = document.createElement(VALUE);
		LangStringsXMLConverter.buildLangStringsXML(document, valueRoot, mLProperty.getValue());
		mLPropertyRoot.appendChild(valueRoot);
		
		return mLPropertyRoot;
	}
}
