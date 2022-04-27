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
package org.eclipse.basyx.aas.factory.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleFactory;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;

/**
 * Creates multiple {@link AASBundle} from a JSON containing several AAS and
 * Submodels <br>
 * TODO: ConceptDescriptions
 * 
 * @author espen
 *
 */
public class JSONAASBundleFactory {
	private String content;

	/**
	 * 
	 * @param jsonContent
	 *            the content of the JSON
	 */
	public JSONAASBundleFactory(String jsonContent) {
		this.content = jsonContent;
	}

	public JSONAASBundleFactory(Path jsonFile) throws IOException {
		content = new String(Files.readAllBytes(jsonFile));
	}

	/**
	 * Creates the set of {@link AASBundle} contained in the JSON string.
	 * 
	 * @return
	 */
	public Set<AASBundle> create() {
		JSONToMetamodelConverter converter = new JSONToMetamodelConverter(content);

		Collection<AssetAdministrationShell> shells = converter.parseAAS();
		Collection<Submodel> submodels = converter.parseSubmodels();
		Collection<Asset> assets = converter.parseAssets();

		return new AASBundleFactory().create(shells, submodels, assets);
	}
}
