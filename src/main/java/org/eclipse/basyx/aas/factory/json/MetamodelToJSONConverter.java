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
import java.util.Collection;
import java.util.HashMap;
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
		return convertToJSON((List<AssetAdministrationShell>) (List<?>) aasEnv.getAssetAdministrationShells(),
				(List<Asset>) (List<?>) aasEnv.getAssets(),
				(List<ConceptDescription>) (List<?>) aasEnv.getConceptDescriptions(),
				(List<Submodel>) (List<?>) aasEnv.getSubmodels());
	}

	/**
	 * Builds the JSON for the given metamodel Objects.
	 * Not required parameters can be null.
	 * 
	 * @param aasList the AASs to build the JSON for
	 * @param assetList the Assets to build the JSON for
	 * @param conceptDescriptionList the ConceptDescriptions to build the JSON for
	 * @param submodelList the Submodels to build the JSON for
	 */
	public static String convertToJSON(Collection<AssetAdministrationShell> aasList, Collection<Asset> assetList,
			Collection<ConceptDescription> conceptDescriptionList, Collection<Submodel> submodelList) {
		
		List<Object> smMapList = submodelsToMapList(submodelList);
		
		Map<String, Object> root = new HashMap<>();
		
		root.put(ASSET_ADMINISTRATION_SHELLS, aasList == null ? new ArrayList<AssetAdministrationShell>() : aasList);
		root.put(SUBMODELS, smMapList);
		root.put(ASSETS, assetList==null ? new ArrayList<Asset>() : assetList);
		root.put(CONCEPT_DESCRIPTIONS,
				conceptDescriptionList == null ? new ArrayList<ConceptDescription>() : conceptDescriptionList);
		
		return new GSONTools(new DefaultTypeFactory()).serialize(root);
	}

	private static List<Object> submodelsToMapList(Collection<Submodel> submodelList) {
		List<Object> smMapList;
		if (submodelList != null) {
			smMapList = submodelList.stream().map(sm -> SubmodelElementMapCollectionConverter.smToMap(sm))
					.collect(Collectors.toList());
		} else {
			smMapList = new ArrayList<>();
		}
		return smMapList;
	}	
}
