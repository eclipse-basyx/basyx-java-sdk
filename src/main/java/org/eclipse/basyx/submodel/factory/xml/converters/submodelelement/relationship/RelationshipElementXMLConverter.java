/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.relationship;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:relationshipElement&gt; and builds the RelationshipElement object from it <br>
 * Builds &lt;aas:relationshipElement&gt; from a given RelationshipElement object
 * 
 * @author conradi
 *
 */
public class RelationshipElementXMLConverter extends SubmodelElementXMLConverter {
	
	public static final String RELATIONSHIP_ELEMENT = "aas:relationshipElement";
	public static final String FIRST = "aas:first";
	public static final String SECOND = "aas:second";
	
	
	/**
	 * Builds a RelationshipElement object from the given XML
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:relationshipElement&gt;
	 * @return the parsed RelationshipElement
	 */
	public static RelationshipElement parseRelationshipElement(Map<String, Object> xmlObject) {
		RelationshipElement relElement = new RelationshipElement();
		populateRelationshipElement(xmlObject, relElement);
		return relElement;
	}
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:relationshipElement&gt;
	 * and populates a given RelationshipElement object
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:relationshipElement&gt;
	 * @param relElement the RelationshipElement to be populated
	 */
	@SuppressWarnings("unchecked")
	public static void populateRelationshipElement(Map<String, Object> xmlObject, IRelationshipElement relElement) {
		Map<String, Object> firstMap = (Map<String, Object>) xmlObject.get(FIRST);
		Reference first = ReferenceXMLConverter.parseReference(firstMap);
		
		Map<String, Object> secondMap = (Map<String, Object>) xmlObject.get(SECOND);
		Reference second = ReferenceXMLConverter.parseReference(secondMap);
		
		relElement.setValue(new RelationshipElementValue(first, second));
		
		populateSubmodelElement(xmlObject, relElement);
	}
	
	
	/**
	 * Builds the &lt;aas:relationshipElement&gt; XML tag for a RelationshipElement
	 * 
	 * @param document the XML document
	 * @param relElem the IRelationshipElement to build the XML for
	 * @return the &lt;aas:relationshipElement&gt; XML tag for the given RelationshipElement
	 */
	public static Element buildRelationshipElement(Document document, IRelationshipElement relElem) {
		Element relElemRoot = document.createElement(RELATIONSHIP_ELEMENT);
		populateRelationshipElement(document, relElemRoot, relElem);
		return relElemRoot;
	}
	
	/**
	 * Builds the content for a given &lt;aas:relationshipElement&gt; XML tag
	 * 
	 * @param document the XML document
	 * @param root the root Element of the new RelationshipElement
	 * @param relElem the RelationShipElement
	 */
	public static void populateRelationshipElement(Document document, Element root, IRelationshipElement relElem) {
		populateSubmodelElement(document, root, relElem);
		
		RelationshipElementValue value = relElem.getValue();
		
		IReference first = value.getFirst();
		if(first != null) {
			Element derivedFromRoot = document.createElement(FIRST);
			derivedFromRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, first)); 
			root.appendChild(derivedFromRoot);
		}		
		IReference second = value.getSecond();
		if(second != null) {
			Element derivedFromRoot = document.createElement(SECOND);
			derivedFromRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, second)); 
			root.appendChild(derivedFromRoot);
		}
		
	}
}
