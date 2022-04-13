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

import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasSemantics;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between an IHasSemantics object and the XML tag
 * &lt;aas:semanticId&gt; in both directions
 * 
 * @author conradi
 *
 */
public class HasSemanticsXMLConverter {

	public static final String SEMANTIC_ID = "aas:semanticId";

	/**
	 * Populates a given HasSemantics object with the data form the given XML
	 * 
	 * @param xmlObject
	 *            the XML map containing the &lt;aas:semanticId&gt; tag
	 * @param hasSemantics
	 *            the HasSemantics object to be populated
	 */
	@SuppressWarnings("unchecked")
	public static void populateHasSemantics(Map<String, Object> xmlObject, HasSemantics hasSemantics) {
		Map<String, Object> xmlSemanticIDObj = (Map<String, Object>) xmlObject.get(SEMANTIC_ID);
		if (xmlSemanticIDObj != null) {
			hasSemantics.setSemanticId(ReferenceXMLConverter.parseReference(xmlSemanticIDObj));
		}
	}

	/**
	 * Populates a given XML map with the data from a given IHasSemantics object<br>
	 * Creates the &lt;aas:semanticId&gt; tag in the given root
	 * 
	 * @param document
	 *            the XML document
	 * @param root
	 *            the XML root Element to be populated
	 * @param hasSemantics
	 *            the IHasSemantics object to be converted to XML
	 */
	public static void populateHasSemanticsXML(Document document, Element root, IHasSemantics hasSemantics) {
		IReference semanticId = hasSemantics.getSemanticId();
		if (semanticId != null) {
			Element semanticIdRoot = document.createElement(SEMANTIC_ID);
			semanticIdRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, semanticId));
			root.appendChild(semanticIdRoot);
		}
	}
}
