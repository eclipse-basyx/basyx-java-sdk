/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.factory.xml.converters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.factory.xml.api.parts.ViewXMLConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.api.parts.IView;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.ConceptDictionary;
import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasDataSpecificationXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.IdentifiableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.ReferableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between IAssetAdministrationShell objects and the XML tag &lt;aas:assetAdministrationShells&gt; in both directions
 * 
 * @author conradi
 *
 */
public class AssetAdministrationShellXMLConverter {
	
	public static final String ASSET_ADMINISTRATION_SHELLS = "aas:assetAdministrationShells";
	public static final String ASSET_ADMINISTRATION_SHELL = "aas:assetAdministrationShell";
	public static final String DERIVED_FROM = "aas:derivedFrom";
	public static final String ASSET_REF = "aas:assetRef";
	public static final String SUBMODEL_REFS = "aas:submodelRefs";
	public static final String SUBMODEL_REF = "aas:submodelRef";
	public static final String CONCEPT_DICTIONARIES = "aas:conceptDictionaries";
	public static final String CONCEPT_DICTIONARY = "aas:conceptDictionary";
	public static final String CONCEPT_DESCRIPTION_REFS = "aas:conceptDescriptionRefs";
	public static final String CONCEPT_DESCRIPTION_REF = "aas:conceptDescriptionRef";
	
	
	/**
	 * Parses &lt;aas:assetAdministrationShells&gt; and builds the AssetAdministrationShell objects from it
	 * 
	 * @param xmlAASObject        a Map containing the content of the XML tag &lt;aas:assetAdministrationShells&gt;
	 * @param conceptDescriptions the available concept descriptions
	 * @return a List of IAssetAdministrationShell objects parsed form the given XML Map
	 */
	@SuppressWarnings("unchecked")
	public static List<IAssetAdministrationShell> parseAssetAdministrationShells(Map<String, Object> xmlAASObject,
			Collection<IConceptDescription> conceptDescriptions) {
		List<IAssetAdministrationShell> aasList = new ArrayList<>();
		
		if (xmlAASObject != null) {
			List<Map<String, Object>> xmlAASs = XMLHelper.getList(xmlAASObject.get(ASSET_ADMINISTRATION_SHELL));
			for (Map<String, Object> xmlAAS : xmlAASs) {
				AssetAdministrationShell adminShell = new AssetAdministrationShell();

				IdentifiableXMLConverter.populateIdentifiable(xmlAAS,
						Identifiable.createAsFacadeNonStrict(adminShell, KeyElements.ASSETADMINISTRATIONSHELL));
				HasDataSpecificationXMLConverter.populateHasDataSpecification(xmlAAS,
						HasDataSpecification.createAsFacade(adminShell));

				Collection<IView> views = ViewXMLConverter.parseViews(xmlAAS);
				Collection<IConceptDictionary> conceptDictionary = parseConceptDictionaries(xmlAAS,
						conceptDescriptions);

				Map<String, Object> xmlAssetRef = (Map<String, Object>) xmlAAS.get(ASSET_REF);
				Reference assetRef = ReferenceXMLConverter.parseReference(xmlAssetRef);

				Map<String, Object> xmlDerivedFrom = (Map<String, Object>) xmlAAS.get(DERIVED_FROM);
				IReference derivedFrom = ReferenceXMLConverter.parseReference(xmlDerivedFrom);
				adminShell.setDerivedFrom(derivedFrom);

				adminShell.setViews(views);
				adminShell.setConceptDictionary(conceptDictionary);
				adminShell.setAssetReference(assetRef);

				Collection<IReference> submodelRefs = parseSubmodelRefs(xmlAAS);
				adminShell.setSubmodelReferences(submodelRefs);

				aasList.add(adminShell);
			}
		}
		return aasList;
	}
	
	
	/**
	 * Parses &lt;aas:submodelRefs&gt; and builds {@link Reference} objects from it
	 * 
	 * @param xmlObject
	 *            a Map containing the XML tag &lt;aas:submodelRefs&gt;
	 * @return a Set of {@link IReference} objects parsed form the given XML Map
	 */
	@SuppressWarnings("unchecked")
	private static Collection<IReference> parseSubmodelRefs(Map<String, Object> xmlObject) {
		Set<IReference> refSet = new HashSet<>();
		
		Map<String, Object> refMap = (Map<String, Object>) xmlObject.get(SUBMODEL_REFS);

		if (refMap == null) {
			return new HashSet<>();
		}

		List<Map<String, Object>> xmlKeyList = XMLHelper.getList(refMap.get(SUBMODEL_REF));
		for (Map<String, Object> xmlKey : xmlKeyList) {
			refSet.add(ReferenceXMLConverter.parseReference(xmlKey));
		}
	
		return refSet;
	}
	
	
	/**
	 * Parses &lt;aas:conceptDictionaries&gt; and builds IConceptDictionary objects from it
	 * 
	 * @param xmlConceptDescriptionRefsObject a Map containing the XML tag &lt;aas:conceptDictionaries&gt;
	 * @param conceptDescriptions             the available concept descriptions
	 * @return a Set of IConceptDictionary objects parsed form the given XML Map
	 */
	@SuppressWarnings("unchecked")
	private static Collection<IConceptDictionary> parseConceptDictionaries(
			Map<String, Object> xmlConceptDescriptionRefsObject, Collection<IConceptDescription> conceptDescriptions) {
		Set<IConceptDictionary> conceptDictionarySet = new HashSet<>();
		if(xmlConceptDescriptionRefsObject == null) return conceptDictionarySet;
		
		Map<String, Object> xmlConceptDictionaries = (Map<String, Object>) xmlConceptDescriptionRefsObject.get(CONCEPT_DICTIONARIES);
		if(xmlConceptDictionaries == null) return conceptDictionarySet;
		
		List<Map<String, Object>> xmlConceptDictionaryList = XMLHelper.getList(xmlConceptDictionaries.get(CONCEPT_DICTIONARY));
		for (Map<String, Object> xmlConceptDictionary : xmlConceptDictionaryList) {
			ConceptDictionary conceptDictionary = new ConceptDictionary();
			ReferableXMLConverter.populateReferable(xmlConceptDictionary, Referable.createAsFacadeNonStrict(conceptDictionary, KeyElements.CONCEPTDICTIONARY));
			
			Map<String, Object> xmlConceptDescriptionRefs = (Map<String, Object>) xmlConceptDictionary.get(CONCEPT_DESCRIPTION_REFS);
			HashSet<IReference> referenceSet = new HashSet<>();
			List<Map<String, Object>> xmlConceptDescriptionRefsList = XMLHelper.getList(xmlConceptDescriptionRefs.get(CONCEPT_DESCRIPTION_REF));
			for (Map<String, Object> xmlConceptDescriptionRef : xmlConceptDescriptionRefsList) {
				referenceSet.add(ReferenceXMLConverter.parseReference(xmlConceptDescriptionRef));
			}
			
			conceptDictionary.setConceptDescriptions(getConceptDescriptions(referenceSet, conceptDescriptions));
			conceptDictionarySet.add(conceptDictionary);
		}
		
		return conceptDictionarySet;
	}
	
	
	
	/**
	 * Gets concept descriptions according to given references
	 * 
	 * @param referenceSet
	 * @param conceptDescriptions
	 * @return the matching concept descriptions
	 */
	private static Collection<IConceptDescription> getConceptDescriptions(Collection<IReference> referenceSet,
			Collection<IConceptDescription> conceptDescriptions) {
		Collection<IConceptDescription> result = new ArrayList<>();
		for (IReference ref : referenceSet) {
			if (ref.getKeys() == null || ref.getKeys().isEmpty()) {
				continue;
			}

			IKey firstKey = ref.getKeys().iterator().next();
			if ( firstKey.getType() == KeyElements.CONCEPTDESCRIPTION && firstKey.isLocal() ) {
				for (IConceptDescription description : conceptDescriptions) {
					if (description.getIdentification().getId().equals(firstKey.getValue())) {
						result.add(description);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Builds &lt;aas:assetAdministrationShells&gt; from a given Collection of IAssetAdministrationShell objects
	 * 
	 * @param document                  the XML document
	 * @param assetAdministrationShells a Collection of IAssetAdministrationShell objects to build the XML for
	 * @return the &lt;aas:assetAdministrationShells&gt; XML tag for the given IAssetAdministrationShell objects
	 */
	public static Element buildAssetAdministrationShellsXML(Document document, Collection<IAssetAdministrationShell> assetAdministrationShells) {
		Element root = document.createElement(ASSET_ADMINISTRATION_SHELLS);
		
		List<Element> xmlAASList = new ArrayList<Element>();
		for (IAssetAdministrationShell aas : assetAdministrationShells) {
			Element aasRoot = document.createElement(ASSET_ADMINISTRATION_SHELL);

			IdentifiableXMLConverter.populateIdentifiableXML(document, aasRoot, aas);
			HasDataSpecificationXMLConverter.populateHasDataSpecificationXML(document, aasRoot, aas);
			
			buildDerivedFrom(document, aasRoot, aas);
			buildAssetRef(document, aasRoot, aas);
			buildSubmodelRef(document, aasRoot, aas);
			Collection<IView> views = aas.getViews();
			
			Element buildViews = ViewXMLConverter.buildViewsXML(document, views);
			aasRoot.appendChild(buildViews);
			aasRoot.appendChild(buildConceptDictionary(document, aas));

			xmlAASList.add(aasRoot);
		}
		
		for (Element element : xmlAASList) {
			root.appendChild(element);
		}
		return root;
	}
	
	
	/**
	 * Builds &lt;aas:derivedFrom&gt; from a given IAssetAdministrationShell object
	 * 
	 * @param document the XML document
	 * @param root the XML tag to be populated
	 * @param aas the IAssetAdministrationShell object to build the XML for
	 */
	private static void buildDerivedFrom(Document document, Element root, IAssetAdministrationShell aas) {
		IReference derivedFrom = aas.getDerivedFrom();
		if(derivedFrom != null) {
			Element derivedFromRoot = document.createElement(DERIVED_FROM);
			derivedFromRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, derivedFrom)); 
			root.appendChild(derivedFromRoot);
		}
	}
	

	/**
	 * Builds &lt;aas:assetRef&gt; from a given IAssetAdministrationShell object
	 * 
	 * @param document the XML document
	 * @param root the XML tag to be populated
	 * @param aas the IAssetAdministrationShell object to build the XML for
	 */
	private static void buildAssetRef(Document document, Element root, IAssetAdministrationShell aas) {
		IReference assetRef = aas.getAssetReference();
		if(assetRef!=null) {
			Element assetrefRoot = document.createElement(ASSET_REF);
			assetrefRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, assetRef));
			root.appendChild(assetrefRoot);
		}
	}
	

	/**
	 * Builds &lt;aas:submodelRefs&gt; from a given IAssetAdministrationShell object
	 * 
	 * @param document the XML document
	 * @param root the XML tag to be populated
	 * @param aas the IAssetAdministrationShell object to build the XML for
	 */
	private static void buildSubmodelRef(Document document, Element root, IAssetAdministrationShell aas) {
		Collection<IReference> submodelRef = aas.getSubmodelReferences();
		
		
		if (submodelRef != null && !submodelRef.isEmpty()) {
			Element submodelRefsRoot = document.createElement(SUBMODEL_REFS);
			for (IReference ref : submodelRef) {
				Element submodelRefRoot = document.createElement(SUBMODEL_REF);
				submodelRefsRoot.appendChild(submodelRefRoot);
				submodelRefRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, ref));
			}

			root.appendChild(submodelRefsRoot);
		}
	}
	

	/**
	 * Builds &lt;aas:conceptDictionaries&gt; from a given IAssetAdministrationShell object
	 * 
	 * @param document the XML document
	 * @param aas the IAssetAdministrationShell object to build the XML for
	 * @return the &lt;aas:conceptDictionaries&gt; XML tag build from the IAssetAdministrationShell object
	 */
	private static Element buildConceptDictionary(Document document, IAssetAdministrationShell aas) {
		Collection<IConceptDictionary> conceptDicionary = aas.getConceptDictionary();
		Element conceptDicts = document.createElement(CONCEPT_DICTIONARIES);
		for(IConceptDictionary iConceptDictionary: conceptDicionary) {
			Element conceptDict = document.createElement(CONCEPT_DICTIONARY);
			Element concDescRoot = document.createElement(CONCEPT_DESCRIPTION_REFS);
			if (iConceptDictionary.getIdShort() != null) {
				Element idShort = document.createElement(ReferableXMLConverter.ID_SHORT);
				idShort.appendChild(document.createTextNode(iConceptDictionary.getIdShort()));
				conceptDict.appendChild(idShort);
			}
			conceptDict.appendChild(concDescRoot);
			conceptDicts.appendChild(conceptDict);
			Collection<IReference> conceptDescriptionRef = iConceptDictionary.getConceptDescriptionReferences();
			for (IReference ref: conceptDescriptionRef) {
				if(ref != null) {
					Element conceptDescriptionRefRoot = document.createElement(CONCEPT_DESCRIPTION_REF);
					concDescRoot.appendChild(conceptDescriptionRefRoot);
					conceptDescriptionRefRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, ref));
				}
			}
			
		}
		return conceptDicts;
	}

}
