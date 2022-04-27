/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
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
