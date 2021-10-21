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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleFactory;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.xml.sax.SAXException;

/**
 * Creates multiple {@link AASBundle} from an XML containing several AAS and
 * Submodels <br>
 * TODO: ConceptDescriptions
 * 
 * @author schnicke
 *
 */
public class XMLAASBundleFactory {
	private String content;

	/**
	 * 
	 * @param xmlContent
	 *            the content of the XML
	 */
	public XMLAASBundleFactory(String xmlContent) {
		this.content = xmlContent;
	}

	public XMLAASBundleFactory(Path xmlFile) throws IOException {
		content = new String(Files.readAllBytes(xmlFile));
	}

	/**
	 * Creates the set of {@link AASBundle} contained in the XML string.
	 * 
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Set<AASBundle> create() throws ParserConfigurationException, SAXException, IOException {
		XMLToMetamodelConverter converter = new XMLToMetamodelConverter(content);
		Collection<IAssetAdministrationShell> shells = converter.parseAAS();
		Collection<ISubmodel> submodels = converter.parseSubmodels();
		Collection<IAsset> assets = converter.parseAssets();

		return new AASBundleFactory().create(shells, submodels, assets);
	}
}
