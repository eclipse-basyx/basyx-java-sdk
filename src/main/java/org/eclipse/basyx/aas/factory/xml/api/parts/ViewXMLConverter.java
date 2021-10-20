/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.factory.xml.api.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.api.parts.IView;
import org.eclipse.basyx.aas.metamodel.map.parts.View;
import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasDataSpecificationXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasSemanticsXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.ReferableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between IView objects and the XML tag &lt;aas:views&gt; in both directions
 * 
 * @author conradi
 *
 */
public class ViewXMLConverter {

	public static final String VIEWS = "aas:views";
	public static final String VIEW = "aas:view";
	public static final String CONTAINED_ELEMENTS = "aas:containedElements";
	public static final String CONTAINED_ELEMENT_REF = "aas:containedElementRef";

	/**
	 * Parses &lt;aas:views&gt; and builds the IView objects from it
	 * 
	 * @param xmlObject a Map containing the XML tag &lt;aas:views&gt;
	 * @return a Set of IView objects parsed form the given XML Map
	 */
	@SuppressWarnings("unchecked")
	public static Collection<IView> parseViews(Map<String, Object> xmlObject) {

		Collection<IView> viewSet = new HashSet<>();
		if(xmlObject == null) return viewSet;
		
		xmlObject = (Map<String, Object>) xmlObject.get(VIEWS);
		if(xmlObject == null) return viewSet;
		
		List<Map<String, Object>> xmlViewList = XMLHelper.getList(xmlObject.get(VIEW));
		
		for(Map<String, Object> xmlView: xmlViewList) {
			View view = new View();
			
			ReferableXMLConverter.populateReferable(xmlView, Referable.createAsFacadeNonStrict(view, KeyElements.VIEW));
			HasSemanticsXMLConverter.populateHasSemantics(xmlView, HasSemantics.createAsFacade(view));
			HasDataSpecificationXMLConverter.populateHasDataSpecification(xmlView, HasDataSpecification.createAsFacade(view));
			
			Map<String, Object> xmlContainedElementsObject = ((Map<String, Object>) xmlView.get(CONTAINED_ELEMENTS));
			Map<String, Object> xmlContainedElementObject = (Map<String, Object>) xmlContainedElementsObject.get(CONTAINED_ELEMENT_REF);
			
			Set<IReference> referenceSet = new HashSet<>();

			referenceSet.add(ReferenceXMLConverter.parseReference(xmlContainedElementObject));
			view.setContainedElement(referenceSet);
			
			viewSet.add(view);
		}
		
		return viewSet;
	}
	
	
	
	
	/**
	 * Builds &lt;aas:views&gt; from a given Collection of IView objects
	 * 
	 * @param document the XML document
	 * @param views a Collection of IView objects to build the XML for
	 * @return the &lt;aas:views&gt; XML tag for the given IView objects
	 */
	public static Element buildViewsXML(Document document, Collection<IView> views) {

		Element root = document.createElement(VIEWS);
		
		List<Element> viewList = new ArrayList<>();
		for(IView view: views) {
			Element viewRoot = document.createElement(VIEW);
			
			ReferableXMLConverter.populateReferableXML(document, viewRoot, view);
			HasSemanticsXMLConverter.populateHasSemanticsXML(document, viewRoot, view);
			HasDataSpecificationXMLConverter.populateHasDataSpecificationXML(document, viewRoot, view);
			buildContainedElements(document, viewRoot, view);
			
			viewList.add(viewRoot);
		}
		
		for(Element element: viewList) {
			root.appendChild(element);
		}
		return root;
	}
	
	
	/**
	 * Builds &lt;aas:containedElements&gt; from a given IView object
	 * 
	 * @param document the XML document
	 * @param xmlView the XML tag to be populated
	 * @param view the IView object to build the XML for
	 */
	private static void buildContainedElements(Document document, Element xmlView, IView view) {
		Collection<IReference> containedElement = view.getContainedElement();
		if(containedElement != null) {
			Element xmlContainedElements = document.createElement(CONTAINED_ELEMENTS);
			Element xmlContainedElementsRef = document.createElement(CONTAINED_ELEMENT_REF);
			xmlContainedElements.appendChild(xmlContainedElementsRef);
			xmlContainedElementsRef.appendChild(ReferenceXMLConverter.buildReferencesXML(document, containedElement)); 
			xmlView.appendChild(xmlContainedElements);
		}
	}
}
