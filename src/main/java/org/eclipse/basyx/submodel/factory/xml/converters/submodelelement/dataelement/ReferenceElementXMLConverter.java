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

import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:referenceElement&gt; and builds the ReferenceElement object from it <br>
 * Builds &lt;aas:referenceElement&gt; from a given ReferenceElement object
 * 
 * @author conradi
 *
 */
public class ReferenceElementXMLConverter extends SubmodelElementXMLConverter {
		
	public static final String REFERENCE_ELEMENT = "aas:referenceElement";

	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:referenceElement&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:referenceElement&gt;
	 * @return the parsed ReferenceElement
	 */
	@SuppressWarnings("unchecked")
	public static ReferenceElement parseReferenceElement(Map<String, Object> xmlObject) {
		Map<String, Object> valueElementObj = (Map<String, Object>) xmlObject.get(VALUE);
		ReferenceElement refElement = new ReferenceElement(ReferenceXMLConverter.parseReference(valueElementObj));
		populateSubmodelElement(xmlObject, refElement);
		return refElement;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:referenceElement&gt; XML tag for a ReferenceElement
	 * 
	 * @param document the XML document
	 * @param refElem the IReferenceElement to build the XML for
	 * @return the &lt;aas:referenceElement&gt; XML tag for the given ReferenceElement
	 */
	public static Element buildReferenceElement(Document document, IReferenceElement refElem) {
		Element refElemRoot = document.createElement(REFERENCE_ELEMENT);
		
		populateSubmodelElement(document, refElemRoot, refElem);

		IReference value = refElem.getValue();
		if(value != null) {
			Element derivedFromRoot = document.createElement(VALUE);
			derivedFromRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, value)); 
			refElemRoot.appendChild(derivedFromRoot);
		}		
		return refElemRoot;
	}
}
