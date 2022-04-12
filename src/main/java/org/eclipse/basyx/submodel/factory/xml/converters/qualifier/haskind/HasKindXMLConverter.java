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
package org.eclipse.basyx.submodel.factory.xml.converters.qualifier.haskind;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.IHasKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Handles the conversion between an IHasKind object and the XML tag &lt;aas:kind&gt; in both directions
 * 
 * @author conradi
 *
 */
public class HasKindXMLConverter {
	
	public static final String KIND = "aas:kind";
	
	
	/**
	 * Populates a given HasKind object with the data form the given XML
	 * 
	 * @param xmlObject the XML map containing the &lt;aas:kind&gt; tag
	 * @param hasKind the HasKind object to be populated
	 */
	public static void populateHasKind(Map<String, Object> xmlObject, HasKind hasKind) {
		String hasKindValue = XMLHelper.getString(xmlObject.get(KIND));
		if (!Strings.isNullOrEmpty(hasKindValue)) {
			// Enables parsing external aasx-files with Type instead of Template
			if (hasKindValue.equals("Type")) {
				hasKindValue = ModelingKind.TEMPLATE.toString();
			}
			hasKind.setKind(ModelingKind.fromString(hasKindValue));
		}
	}
	
	
	/**
	 * Populates a given XML map with the data from a given IHasKind object<br>
	 * Creates the &lt;aas:kind&gt; tag in the given root
	 * 
	 * @param document the XML document
	 * @param root the XML root Element to be populated
	 * @param hasKind the IHasKind object to be converted to XML
	 */
	public static void populateHasKindXML(Document document, Element root, IHasKind hasKind) {
		if (hasKind.getKind() != null) {
			Element kindRoot = document.createElement(KIND);
			kindRoot.appendChild(document.createTextNode(hasKind.getKind().toString()));
			root.appendChild(kindRoot);
		}
	}
}
