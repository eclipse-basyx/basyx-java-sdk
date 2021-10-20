/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
			hasKind.setModelingKind(ModelingKind.fromString(hasKindValue));	
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
		if(hasKind.getModelingKind() != null) {
			Element kindRoot = document.createElement(KIND);
			kindRoot.appendChild(document.createTextNode(hasKind.getModelingKind().toString()));
			root.appendChild(kindRoot);
		}
	}
}
