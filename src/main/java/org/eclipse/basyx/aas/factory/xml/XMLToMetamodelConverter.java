/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.factory.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.factory.xml.api.parts.AssetXMLConverter;
import org.eclipse.basyx.aas.factory.xml.converters.AssetAdministrationShellXMLConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.submodel.factory.xml.api.parts.ConceptDescriptionXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.SubmodelXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.vab.factory.xml.XmlParser;
import org.xml.sax.SAXException;

/**
 * This class can be used to parse XML to Metamodel Objects
 * 
 * @author conradi
 *
 */
public class XMLToMetamodelConverter {

	private Map<String, Object> root = new HashMap<>();
	
	/**
	 * Initializes the Parser with XML given as a String
	 * 
	 * @param xmlContent
	 *            the XML content to be parsed
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public XMLToMetamodelConverter(String xmlContent) throws ParserConfigurationException, SAXException, IOException {		
		root.putAll((Map<? extends String, ? extends Object>) XmlParser.buildXmlMap(xmlContent)
				.get(MetamodelToXMLConverter.AASENV));
	}

	
	/**
	 * Parses the AASs form the XML
	 * 
	 * @return the AASs parsed form the XML
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<IAssetAdministrationShell> parseAAS() throws ParserConfigurationException, SAXException, IOException {
		Map<String, Object> xmlAASs = (Map<String, Object>) root
				.get(AssetAdministrationShellXMLConverter.ASSET_ADMINISTRATION_SHELLS);
		// First, parse all conceptDescriptions
		List<IConceptDescription> conceptDescriptions = parseConceptDescriptions();
		// Then parse the AAS -> the available conceptDescriptions have to be mapped to the contained concept
		// dictionaries
		return AssetAdministrationShellXMLConverter.parseAssetAdministrationShells(xmlAASs, conceptDescriptions);
	}

	
	/**
	 * Parses the Assets form the XML
	 * 
	 * @return the Assets parsed form the XML
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<IAsset> parseAssets() throws ParserConfigurationException, SAXException, IOException {
		Map<String, Object> xmlAssets = (Map<String, Object>) root.get(AssetXMLConverter.ASSETS);
		return AssetXMLConverter.parseAssets(xmlAssets);
	}

	
	/**
	 * Parses the Submodels form the XML
	 * 
	 * @return the Submodels parsed form the XML
	 */
	@SuppressWarnings("unchecked")
	public List<ISubmodel> parseSubmodels() {
		Map<String, Object> xmlSubmodels = (Map<String, Object>) root.get(SubmodelXMLConverter.SUBMODELS);		
		return SubmodelXMLConverter.parseSubmodels(xmlSubmodels);		
	}

	
	/**
	 * Parses the ConceptDescriptions form the XML
	 * 
	 * @return the ConceptDescriptions parsed form the XML
	 */
	@SuppressWarnings("unchecked")
	public List<IConceptDescription> parseConceptDescriptions() {
		Map<String, Object> xmlConceptDescriptions = (Map<String, Object>) root.get(ConceptDescriptionXMLConverter.CONCEPT_DESCRIPTIONS);		
		return ConceptDescriptionXMLConverter.parseConceptDescriptions(xmlConceptDescriptions);
	}
	
}
