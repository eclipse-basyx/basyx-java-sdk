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
	 * @param jsonContent the JSON content to be parsed
	 */
	@SuppressWarnings("unchecked")
	public JSONToMetamodelConverter(String jsonContent) {
		Map<String, Object> root = (Map<String, Object>) new GSONTools(new DefaultTypeFactory())
				.deserialize(jsonContent);

		List<IAsset> assets = ((List<Object>) root.get(MetamodelToJSONConverter.ASSETS)).stream()
				.map(aMap -> Asset.createAsFacade((Map<String, Object>) aMap)).collect(Collectors.toList());

		List<IAssetAdministrationShell> shells = ((List<Object>) root
				.get(MetamodelToJSONConverter.ASSET_ADMINISTRATION_SHELLS)).stream()
						.map(aasObject -> handleJSONAssetReference(aasObject, (List<Asset>) (List<?>) assets))
						.collect(Collectors.toList());

		List<IConceptDescription> conceptDescriptions = ((List<Object>) root
				.get(MetamodelToJSONConverter.CONCEPT_DESCRIPTIONS)).stream()
						.map(cdMap -> ConceptDescription.createAsFacade((Map<String, Object>) cdMap))
						.collect(Collectors.toList());

		List<ISubmodel> submodels = ((List<Object>) root.get(MetamodelToJSONConverter.SUBMODELS)).stream()
				.map(smMap -> Submodel.createAsFacade((Map<String, Object>) smMap)).collect(Collectors.toList());

		aasEnv = new AasEnv(shells, assets, conceptDescriptions, submodels);
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
	private AssetAdministrationShell handleReference(Object aasObject, Map<String, Object> aasMap,
			Map<String, Object> assetRefMap, List<Asset> assets) {
		aasMap.put(AssetAdministrationShell.ASSETREF, assetRefMap);
		
		IReference assetRef = Reference
				.createAsFacade((Map<String, Object>) aasMap.get(AssetAdministrationShell.ASSETREF));
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
