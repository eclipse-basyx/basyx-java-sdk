/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:submodelElementCollection&gt; and builds the SubmodelElementCollection object from it <br>
 * Builds &lt;aas:submodelElementCollection&gt; from a given SubmodelElementCollection object
 * 
 * @author conradi
 *
 */
public class SubmodelElementCollectionXMLConverter extends SubmodelElementXMLConverter {

	public static final String SUBMODEL_ELEMENT_COLLECTION = "aas:submodelElementCollection";
	public static final String ORDERED = "aas:ordered";
	public static final String ALLOW_DUPLICATES = "aas:allowDuplicates";
	
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:submodelElementCollection&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:submodelElementCollection&gt;
	 * @return the parsed SubmodelElementCollection
	 */
	@SuppressWarnings("unchecked")
	public static SubmodelElementCollection parseSubmodelElementCollection(Map<String, Object> xmlObject) {
		boolean ordered = Boolean.valueOf(XMLHelper.getString(xmlObject.get(ORDERED)));
		boolean allowDuplicates = Boolean.valueOf(XMLHelper.getString(xmlObject.get(ALLOW_DUPLICATES)));
		Map<String, Object> valueMap = (Map<String, Object>) xmlObject.get(VALUE);
		List<ISubmodelElement> submodelElements = getSubmodelElements(valueMap);
		SubmodelElementCollection smElemColl = new SubmodelElementCollection(submodelElements, ordered, allowDuplicates);
		populateSubmodelElement(xmlObject, smElemColl);
		return smElemColl;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:submodelElementCollection&gt; XML tag for a SubmodelElementCollection
	 * 
	 * @param document the XML document
	 * @param smElemCollection the ISubmodelElementCollection to build the XML for
	 * @return the &lt;aas:submodelElementCollection&gt; XML tag for the given SubmodelElementCollection
	 */
	public static Element buildSubmodelElementCollection(Document document, ISubmodelElementCollection smElemCollection) {
		Element smElemCollectionRoot = document.createElement(SUBMODEL_ELEMENT_COLLECTION);
		
		populateSubmodelElement(document, smElemCollectionRoot, smElemCollection);
		
		String isAllowedDuplicates = Boolean.toString(smElemCollection.isAllowDuplicates());
		Element allowDuplicatesElem = document.createElement(ALLOW_DUPLICATES);
		allowDuplicatesElem.appendChild(document.createTextNode(isAllowedDuplicates));
		smElemCollectionRoot.appendChild(allowDuplicatesElem);
		
		String isOrdered = Boolean.toString(smElemCollection.isOrdered());
		Element orderedElem = document.createElement(ORDERED);
		orderedElem.appendChild(document.createTextNode(isOrdered));
		smElemCollectionRoot.appendChild(orderedElem);
		
		Collection<ISubmodelElement> elems = smElemCollection.getSubmodelElements().values();
		
		//recursively build the SubmodelElements contained in the ElementCollection
		if(elems != null) {
			Element valueRoot = document.createElement(VALUE);
			smElemCollectionRoot.appendChild(valueRoot);
			buildSubmodelElements(document, valueRoot, elems);
		}

		return smElemCollectionRoot;
	}
}
