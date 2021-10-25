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

import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.basyx.aas.factory.xml.api.parts.AssetXMLConverter;
import org.eclipse.basyx.aas.factory.xml.converters.AssetAdministrationShellXMLConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.submodel.factory.xml.api.parts.ConceptDescriptionXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.SubmodelXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class can be used to build XML from Metamodel Objects
 * 
 * @author conradi
 *
 */
public class MetamodelToXMLConverter {
	public static final String AASENV = "aas:aasenv";
	
	/**
	 * Builds the XML for the given aasEnv
	 * 
	 * @param aasEnv
	 * @param result a Result object to write the XML to e.g. ResultStream
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	public static void convertToXML(AasEnv aasEnv, Result result)
			throws TransformerException, ParserConfigurationException {
		convertToXML(aasEnv.getAssetAdministrationShells(), aasEnv.getAssets(), aasEnv.getConceptDescriptions(),
				aasEnv.getSubmodels(), result);
	}

	/**
	 * Builds the XML for the given metamodel Objects
	 * 
	 * @param aasList                the AASs to build the XML for
	 * @param assetList              the Assets to build the XML for
	 * @param conceptDescriptionList the ConceptDescriptions to build the XML for
	 * @param submodelList           the Submodels to build the XML for
	 * @param result                 a Result object to write the XML to e.g.
	 *                               ResultStream
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	public static void convertToXML(Collection<IAssetAdministrationShell> aasList, Collection<IAsset> assetList, 
			Collection<IConceptDescription> conceptDescriptionList, Collection<ISubmodel> submodelList, Result result)
					throws TransformerException, ParserConfigurationException {
		
		Document document = createEmptyDocument();
		
		//creating the root tag <aas:aasenv>
		Element root = document.createElement(AASENV);
		
		//creating the Header information
		root.setAttribute("xmlns:aas", "http://www.admin-shell.io/aas/2/0");
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns:IEC61360", "http://www.admin-shell.io/IEC61360/2/0");
		root.setAttribute("xsi:schemaLocation", "http://www.admin-shell.io/aas/2/0 AAS.xsd http://www.admin-shell.io/IEC61360/2/0 IEC61360.xsd");
		document.appendChild(root);

		
		Element buildAssetadminsroot = AssetAdministrationShellXMLConverter.buildAssetAdministrationShellsXML(document, aasList);
		root.appendChild(buildAssetadminsroot);
		
		Element assetsObj = AssetXMLConverter.buildAssetsXML(document, assetList);
		root.appendChild(assetsObj);

		Element subModelsroot = SubmodelXMLConverter.buildSubmodelsXML(document, submodelList);
		root.appendChild(subModelsroot);
		
		Element conceptDescriptionObj = ConceptDescriptionXMLConverter.buildConceptDescriptionsXML(document, conceptDescriptionList);
		root.appendChild(conceptDescriptionObj);
		
		
		//create the xml file
		//transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource domSource = new DOMSource(document);

		transformer.transform(domSource, result);
	}

	private static Document createEmptyDocument() throws ParserConfigurationException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		return document;
	}
}
