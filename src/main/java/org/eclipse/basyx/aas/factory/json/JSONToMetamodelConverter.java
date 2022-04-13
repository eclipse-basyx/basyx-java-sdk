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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;

/**
 * This class can be used to parse JSON to Metamodel Objects
 * 
 * @author conradi, jungjan
 *
 */
public class JSONToMetamodelConverter {

	private AasEnv aasEnv;

	/**
	 * Initializes the parser with XML given as a String
	 * 
	 * @param jsonContent
	 *            the JSON content to be parsed
	 */
	public JSONToMetamodelConverter(String jsonContent) {
		Map<String, Object> root = createRoot(jsonContent);

		List<IAsset> assets = createAssets(root);

		List<IAssetAdministrationShell> shells = createShells(root, assets);

		List<IConceptDescription> conceptDescriptions = createConceptDescriptions(root);

		List<ISubmodel> submodels = createSubmodels(root);

		aasEnv = new AasEnv(shells, assets, conceptDescriptions, submodels);
	}

	@SuppressWarnings("unchecked")
	private List<ISubmodel> createSubmodels(Map<String, Object> root) {
		return ((List<Object>) root.get(MetamodelToJSONConverter.SUBMODELS)).stream().map(smMap -> Submodel.createAsFacade((Map<String, Object>) smMap)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<IConceptDescription> createConceptDescriptions(Map<String, Object> root) {
		return ((List<Object>) root.get(MetamodelToJSONConverter.CONCEPT_DESCRIPTIONS)).stream().map(cdMap -> ConceptDescription.createAsFacade((Map<String, Object>) cdMap)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<IAssetAdministrationShell> createShells(Map<String, Object> root, List<IAsset> assets) {
		return ((List<Object>) root.get(MetamodelToJSONConverter.ASSET_ADMINISTRATION_SHELLS)).stream().map(aasObject -> handleJSONAssetReference(aasObject, (List<Asset>) (List<?>) assets)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private List<IAsset> createAssets(Map<String, Object> root) {
		return ((List<Object>) root.get(MetamodelToJSONConverter.ASSETS)).stream().map(aMap -> Asset.createAsFacade((Map<String, Object>) aMap)).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createRoot(String jsonContent) {
		return (Map<String, Object>) new GSONTools(new DefaultTypeFactory()).deserialize(jsonContent);
	}

	/**
	 * Parses the AasEnv from the JSON
	 * 
	 * @return the AasEnv parsed from the JSON
	 */
	public AasEnv parseAasEnv() {
		return aasEnv;
	}

	/**
	 * Parses the AASs from the JSON
	 * 
	 * @return the AASs parsed from the JSON
	 */
	@SuppressWarnings("unchecked")
	public List<AssetAdministrationShell> parseAAS() {
		return new ArrayList<>((List<AssetAdministrationShell>) (List<?>) aasEnv.getAssetAdministrationShells());
	}

	@SuppressWarnings("unchecked")
	private AssetAdministrationShell handleJSONAssetReference(Object aasObject, List<Asset> assets) {
		Map<String, Object> aasMap = (Map<String, Object>) aasObject;
		Map<String, Object> assetRefMap = (Map<String, Object>) aasMap.get(AssetAdministrationShell.ASSET);

		if (isAssetReference(assetRefMap)) {
			return handleReference(aasObject, aasMap, assetRefMap, assets);
		} else {
			return AssetAdministrationShell.createAsFacade((Map<String, Object>) aasObject);
		}

	}

	@SuppressWarnings("unchecked")
	private AssetAdministrationShell handleReference(Object aasObject, Map<String, Object> aasMap, Map<String, Object> assetRefMap, List<Asset> assets) {
		aasMap.put(AssetAdministrationShell.ASSETREF, assetRefMap);

		IReference assetRef = Reference.createAsFacade((Map<String, Object>) aasMap.get(AssetAdministrationShell.ASSETREF));
		IKey lastKey = assetRef.getKeys().get(assetRef.getKeys().size() - 1);
		String idValue = lastKey.getValue();
		for (Asset asset : assets) {
			if (asset.getIdentification().getId().equals(idValue)) {
				aasMap.put(AssetAdministrationShell.ASSET, asset);
				break;
			}
		}

		return AssetAdministrationShell.createAsFacade((Map<String, Object>) aasObject);
	}

	private boolean isAssetReference(Map<String, Object> assetRefMap) {
		return assetRefMap.get(Reference.KEY) != null && assetRefMap.get(Asset.KIND) == null;
	}

	/**
	 * Parses the Submodels from the JSON
	 * 
	 * @return the Submodels parsed from the JSON
	 */
	@SuppressWarnings("unchecked")
	public List<Submodel> parseSubmodels() {
		return new ArrayList<>((List<Submodel>) (List<?>) aasEnv.getSubmodels());
	}

	/**
	 * Parses the Assets from the JSON
	 * 
	 * @return the Assets parsed from the JSON
	 */
	@SuppressWarnings("unchecked")
	public List<Asset> parseAssets() {
		return new ArrayList<>((List<Asset>) (List<?>) aasEnv.getAssets());
	}

	/**
	 * Parses the ConceptDescriptions from the JSON
	 * 
	 * @return the ConceptDescriptions parsed from the JSON
	 */
	@SuppressWarnings("unchecked")
	public List<ConceptDescription> parseConceptDescriptions() {
		return new ArrayList<>((List<ConceptDescription>) (List<?>) aasEnv.getConceptDescriptions());
	}
}
