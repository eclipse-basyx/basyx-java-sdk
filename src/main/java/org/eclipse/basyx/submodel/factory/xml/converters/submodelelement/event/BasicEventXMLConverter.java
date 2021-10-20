/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.event;

import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.event.IBasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:basicEvent&gt; and builds the BasicEvent object from it <br>
 * Builds &lt;aas:basicEvent&gt; from a given BasicEvent object
 * 
 * @author conradi
 *
 */
public class BasicEventXMLConverter extends SubmodelElementXMLConverter {
	
	public static final String BASIC_EVENT = "aas:basicEvent";
	public static final String OBSERVED = "aas:observed";
	

	/**
	 * Parses a Map containing the content of XML tag &lt;aas:basicEvent&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:basicEvent&gt;
	 * @return the parsed BasicEvent
	 */
	@SuppressWarnings("unchecked")
	public static BasicEvent parseBasicEvent(Map<String, Object> xmlObject) {
		Map<String, Object> xmlObserved = (Map<String, Object>) xmlObject.get(OBSERVED);
		Reference observed = ReferenceXMLConverter.parseReference(xmlObserved);
		BasicEvent basicEvent = new BasicEvent(observed);
		populateSubmodelElement(xmlObject, basicEvent);
		return basicEvent;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:basicEvent&gt; XML tag for a BasicEvent
	 * 
	 * @param document the XML document
	 * @param basicEvent the IBasicEvent to build the XML for
	 * @return the &lt;aas:basicEvent&gt; XML tag for the given BasicEvent
	 */
	public static Element buildBasicEvent(Document document, IBasicEvent basicEvent) {
		Element basicEventRoot = document.createElement(BASIC_EVENT);
		
		populateSubmodelElement(document, basicEventRoot, basicEvent);
		
		IReference observed = basicEvent.getObserved();
		if(observed != null) {
			Element observedRoot = document.createElement(OBSERVED);
			observedRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, observed));
			basicEventRoot.appendChild(observedRoot);
		}
		
		return basicEventRoot;
	}
}
