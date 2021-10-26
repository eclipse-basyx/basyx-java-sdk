/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.api.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasDataSpecificationXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.IdentifiableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between IConceptDescription objects and the XML tag
 * &lt;aas:conceptDescriptions&gt; in both directions
 * 
 * @author conradi
 *
 */
public class ConceptDescriptionXMLConverter {

	public static final String CONCEPT_DESCRIPTIONS = "aas:conceptDescriptions";
	public static final String CONCEPT_DESCRIPTION = "aas:conceptDescription";
	public static final String IS_CASE_OF = "aas:isCaseOf";

	/**
	 * Parses &lt;aas:conceptDescriptions&gt; and builds the IConceptDescription
	 * objects from it
	 * 
	 * @param xmlObject a Map containing the content of the XML tag
	 *                  &lt;aas:conceptDescriptions&gt;
	 * @return a List of IConceptDescription objects parsed form the given XML Map
	 */
	public static List<IConceptDescription> parseConceptDescriptions(Map<String, Object> xmlObject) {
		if (xmlObject == null) {
			return Collections.emptyList();
		}

		List<IConceptDescription> conceptDescriptions = new ArrayList<>();
		List<Map<String, Object>> xmlConceptDescriptionList = XMLHelper.getList(xmlObject.get(CONCEPT_DESCRIPTION));

		for (Map<String, Object> xmlConceptDescription : xmlConceptDescriptionList) {
			ConceptDescription conceptDescription = new ConceptDescription();

			IdentifiableXMLConverter.populateIdentifiable(xmlConceptDescription,
					Identifiable.createAsFacadeNonStrict(conceptDescription, KeyElements.CONCEPTDESCRIPTION));
			HasDataSpecificationXMLConverter.populateHasDataSpecification(xmlConceptDescription,
					HasDataSpecification.createAsFacade(conceptDescription));

			Collection<Reference> handleIsCaseOf = parseIsCaseOfRefs(xmlConceptDescription);
			conceptDescription.setIsCaseOf(handleIsCaseOf);

			conceptDescriptions.add(conceptDescription);
		}

		return conceptDescriptions;
	}

	/**
	 * Parses &lt;aas:isCaseOf&gt; and builds a Reference object from it
	 * 
	 * @param xmlObject a Map containing the XML tag &lt;aas:isCaseOf&gt;
	 * @return a Reference object parsed form the given XML Map
	 */
	private static Collection<Reference> parseIsCaseOfRefs(Map<String, Object> xmlObject) {
		Collection<Reference> references = new HashSet<>();
		List<Map<String, Object>> xmlKeyList = XMLHelper.getList(xmlObject.get(IS_CASE_OF));
		for (Map<String, Object> xmlKey : xmlKeyList) {
			references.add(ReferenceXMLConverter.parseReference(xmlKey));
		}
		return references;
	}

	/**
	 * Builds &lt;aas:conceptDescriptions&gt; from a given Collection of
	 * IConceptDescription objects
	 * 
	 * @param document            the XML document
	 * @param conceptDescriptions a Collection of IConceptDescription objects to
	 *                            build the XML for
	 * @return the &lt;aas:conceptDescriptions&gt; XML tag for the given
	 *         IConceptDescription objects
	 */
	public static Element buildConceptDescriptionsXML(Document document,
			Collection<IConceptDescription> conceptDescriptions) {
		Element root = document.createElement(CONCEPT_DESCRIPTIONS);

		List<Element> xmlConceptDescriptionList = new ArrayList<Element>();
		for (IConceptDescription conceptDescription : conceptDescriptions) {
			Element conceptDescriptionRoot = document.createElement(CONCEPT_DESCRIPTION);

			IdentifiableXMLConverter.populateIdentifiableXML(document, conceptDescriptionRoot, conceptDescription);
			HasDataSpecificationXMLConverter.populateHasDataSpecificationXML(document, conceptDescriptionRoot,
					conceptDescription);
			buildIsCaseOf(document, conceptDescriptionRoot, conceptDescription);
			xmlConceptDescriptionList.add(conceptDescriptionRoot);

		}

		for (Element element : xmlConceptDescriptionList) {
			root.appendChild(element);
		}
		return root;
	}

	/**
	 * Builds &lt;aas:isCaseOf&gt; from a given IConceptDescription object
	 * 
	 * @param document              the XML document
	 * @param xmlConceptDescription the XML tag to be populated
	 * @param conceptDescription    the IConceptDescription object to build the XML
	 *                              for
	 */
	private static void buildIsCaseOf(Document document, Element xmlConceptDescription,
			IConceptDescription conceptDescription) {
		Collection<IReference> references = conceptDescription.getIsCaseOf();
		Element xmlIsCaseOf = document.createElement(IS_CASE_OF);

		Element keysElement = ReferenceXMLConverter.buildReferencesXML(document, references);
		if (keysElement != null) {
			xmlIsCaseOf.appendChild(keysElement);
			xmlConceptDescription.appendChild(xmlIsCaseOf);
		}
	}
}
