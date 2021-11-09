/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	 *                    the content of the JSON
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
