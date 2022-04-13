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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;

/**
 * This class can be used to build JSON from Metamodel Objects
 * 
 * @author conradi
 *
 */
public class MetamodelToJSONConverter {

	public static final String ASSET_ADMINISTRATION_SHELLS = "assetAdministrationShells";
	public static final String SUBMODELS = "submodels";
	public static final String ASSETS = "assets";
	public static final String CONCEPT_DESCRIPTIONS = "conceptDescriptions";

	/**
	 * Builds the JSON for the aasEnv
	 * 
	 * @param aasEnv
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String convertToJSON(AasEnv aasEnv) {
		return convertToJSON((List<AssetAdministrationShell>) (List<?>) aasEnv.getAssetAdministrationShells(), (List<Asset>) (List<?>) aasEnv.getAssets(), (List<ConceptDescription>) (List<?>) aasEnv.getConceptDescriptions(),
				(List<Submodel>) (List<?>) aasEnv.getSubmodels());
	}

	/**
	 * Builds the JSON for the given metamodel Objects. Not required parameters can
	 * be null.
	 * 
	 * @param aasList
	 *            the AASs to build the JSON for
	 * @param assetList
	 *            the Assets to build the JSON for
	 * @param conceptDescriptionList
	 *            the ConceptDescriptions to build the JSON for
	 * @param submodelList
	 *            the Submodels to build the JSON for
	 */
	public static String convertToJSON(Collection<AssetAdministrationShell> aasList, Collection<Asset> assetList, Collection<ConceptDescription> conceptDescriptionList, Collection<Submodel> submodelList) {

		List<Object> smMapList = submodelsToMapList(submodelList);

		Map<String, Object> root = new LinkedHashMap<>();

		root.put(ASSET_ADMINISTRATION_SHELLS, aasList == null ? new ArrayList<AssetAdministrationShell>() : aasList);
		root.put(SUBMODELS, smMapList);
		root.put(ASSETS, assetList == null ? new ArrayList<Asset>() : assetList);
		root.put(CONCEPT_DESCRIPTIONS, conceptDescriptionList == null ? new ArrayList<ConceptDescription>() : conceptDescriptionList);

		return new GSONTools(new DefaultTypeFactory()).serialize(root);
	}

	private static List<Object> submodelsToMapList(Collection<Submodel> submodelList) {
		if (submodelList != null) {
			return submodelList.stream().map(sm -> SubmodelElementMapCollectionConverter.smToMap(sm)).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}
}
