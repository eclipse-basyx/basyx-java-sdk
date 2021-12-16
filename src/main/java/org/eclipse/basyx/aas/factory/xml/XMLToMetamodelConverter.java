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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.factory.xml.api.parts.AssetXMLConverter;
import org.eclipse.basyx.aas.factory.xml.converters.AssetAdministrationShellXMLConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
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
	
	private AasEnv aasEnv;

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
		Map<String, Object> root = new LinkedHashMap<>();
		root.putAll((Map<? extends String, ? extends Object>) XmlParser.buildXmlMap(xmlContent)
				.get(MetamodelToXMLConverter.AASENV));

		Map<String, Object> xmlAASs = (Map<String, Object>) root
				.get(AssetAdministrationShellXMLConverter.ASSET_ADMINISTRATION_SHELLS);
		
		Map<String, Object> xmlConceptDescriptions = (Map<String, Object>) root
				.get(ConceptDescriptionXMLConverter.CONCEPT_DESCRIPTIONS);
		
		List<IConceptDescription> conceptDescriptions = ConceptDescriptionXMLConverter
				.parseConceptDescriptions(xmlConceptDescriptions);
		
		List<IAssetAdministrationShell> shells = AssetAdministrationShellXMLConverter
				.parseAssetAdministrationShells(xmlAASs, conceptDescriptions);
		
		Map<String, Object> xmlSubmodels = (Map<String, Object>) root.get(SubmodelXMLConverter.SUBMODELS);
		List<ISubmodel> submodels = SubmodelXMLConverter.parseSubmodels(xmlSubmodels);
		Map<String, Object> xmlAssets = (Map<String, Object>) root.get(AssetXMLConverter.ASSETS);
		List<IAsset> assets = AssetXMLConverter.parseAssets(xmlAssets);

		aasEnv = new AasEnv(shells, assets, conceptDescriptions, submodels);
	}

	/**
	 * Parses the AasEnv from the XML
	 * 
	 * @return the AasEnv parsed from the XML
	 */
	public AasEnv parseAasEnv() {
		return aasEnv;
	}
	
	/**
	 * Parses the AASs from the XML
	 * 
	 * @return the AASs parsed from the XML
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<IAssetAdministrationShell> parseAAS() throws ParserConfigurationException, SAXException, IOException {
		return new ArrayList<>(aasEnv.getAssetAdministrationShells());
	}

	
	/**
	 * Parses the Assets from the XML
	 * 
	 * @return the Assets parsed from the XML
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public List<IAsset> parseAssets() throws ParserConfigurationException, SAXException, IOException {
		return new ArrayList<>(aasEnv.getAssets());
	}

	
	/**
	 * Parses the Submodels from the XML
	 * 
	 * @return the Submodels parsed from the XML
	 */
	public List<ISubmodel> parseSubmodels() {
		return new ArrayList<>(aasEnv.getSubmodels());
	}

	
	/**
	 * Parses the ConceptDescriptions from the XML
	 * 
	 * @return the ConceptDescriptions parsed from the XML
	 */
	public List<IConceptDescription> parseConceptDescriptions() {
		return new ArrayList<>(aasEnv.getConceptDescriptions());
	}
	
}
