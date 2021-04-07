/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAasEnv;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * AasEnv class 
 * 
 * @author gordt
 */

public class AasEnv extends VABModelMap<Object> implements IAasEnv {
	
	public static final String ASSETS = "assets";
	public static final String ASSETADMINISTRATIONSHELLS = "assetAdministrationShells";
	public static final String SUBMODELS = "submodels";
	public static final String CONCEPTDESCRIPTIONS = "conceptDescriptions";
	public static final String MODELTYPE = "AasEnv";


	public AasEnv() {
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	public AasEnv(Collection<IAssetAdministrationShell> aasList, Collection<IAsset> assetList,
			Collection<IConceptDescription> conceptDescriptionList, Collection<ISubmodel> submodelList) {
		setAssetAdministrationShells(aasList);
		setAssets(assetList);
		setConceptDescriptions(conceptDescriptionList);
		setSubmodels(submodelList);
	}

	/**
	 * Creates a AssetAdministrationShell object from a map
	 * 
	 * @param map
	 *            a AssetAdministrationShell object as raw map
	 * @return a AssetAdministrationShell object, that behaves like a facade for the
	 *         given map
	 */
	@SuppressWarnings("unchecked")
	public static AasEnv createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		AasEnv ret = new AasEnv();
		
		Collection<IAsset> assetsTarget = new LinkedList<>();
		if (map.get(ASSETS) != null && map.get(ASSETS) instanceof Collection) {
			Collection<Map<String, Object>> objectMapCollection = (Collection<Map<String, Object>>) map.get(ASSETS);
			for(Map<String, Object> objectMap : objectMapCollection) {
				assetsTarget.add(Asset.createAsFacade(objectMap));
			}
		}
		ret.put(ASSETS, assetsTarget);
		
		Collection<IAssetAdministrationShell> aasTarget = new LinkedList<>();
		if (map.get(ASSETADMINISTRATIONSHELLS) != null && map.get(ASSETADMINISTRATIONSHELLS) instanceof Collection) {
			Collection<Map<String, Object>> objectMapCollection = (Collection<Map<String, Object>>) map.get(ASSETADMINISTRATIONSHELLS);
			for(Map<String, Object> objectMap : objectMapCollection) {
				aasTarget.add(AssetAdministrationShell.createAsFacade(objectMap));
			}
		}
		ret.put(ASSETADMINISTRATIONSHELLS, aasTarget);
		
		Collection<ISubmodel> submodelsTarget = new LinkedList<>();
		if (map.get(SUBMODELS) != null && map.get(SUBMODELS) instanceof Collection) {
			Collection<Map<String, Object>> objectMapCollection = (Collection<Map<String, Object>>) map.get(SUBMODELS);
			for(Map<String, Object> objectMap : objectMapCollection) {
				submodelsTarget.add(Submodel.createAsFacade(objectMap));
			}
		}
		ret.put(SUBMODELS, submodelsTarget);
		
		Collection<IConceptDescription> conceptDescriptionsTarget = new LinkedList<>();
		if (map.get(CONCEPTDESCRIPTIONS) != null && map.get(CONCEPTDESCRIPTIONS) instanceof Collection) {
			Collection<Map<String, Object>> objectMapCollection = (Collection<Map<String, Object>>) map.get(CONCEPTDESCRIPTIONS);
			for(Map<String, Object> objectMap : objectMapCollection) {
				conceptDescriptionsTarget.add(ConceptDescription.createAsFacade(objectMap));
			}
		}
		ret.put(CONCEPTDESCRIPTIONS, conceptDescriptionsTarget);
		
		return ret;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAsset> getAssets() {
		List<IAsset> internal = (List<IAsset>) get(ASSETS);
		List<IAsset> result = new ArrayList<IAsset>(internal.size());
		result.addAll(internal);
		return result;
	}

	public void setAssets(Collection<IAsset> assets) {
		put(ASSETS, assets);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAssetAdministrationShells() {
		List<IAssetAdministrationShell> internal = (List<IAssetAdministrationShell>) get(ASSETADMINISTRATIONSHELLS);
		List<IAssetAdministrationShell> result = new ArrayList<IAssetAdministrationShell>(internal.size());
		result.addAll(internal);
		return result;
	}

	public void setAssetAdministrationShells(Collection<IAssetAdministrationShell> assetAdministrationShells) {
		put(ASSETADMINISTRATIONSHELLS, assetAdministrationShells);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<ISubmodel> getSubmodels() {
		List<ISubmodel> internal = (List<ISubmodel>) get(SUBMODELS);
		List<ISubmodel> result = new ArrayList<ISubmodel>(internal.size());
		result.addAll(internal);
		return result;
	}

	public void setSubmodels(Collection<ISubmodel> submodels) {
		put(SUBMODELS, submodels);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IConceptDescription> getConceptDescriptions() {
		List<IConceptDescription> internal = (List<IConceptDescription>) get(CONCEPTDESCRIPTIONS);
		List<IConceptDescription> result = new ArrayList<IConceptDescription>(internal.size());
		result.addAll(internal);
		return result;
	}

	public void setConceptDescriptions(Collection<IConceptDescription> conceptDescriptions) {
		put(CONCEPTDESCRIPTIONS, conceptDescriptions);
	}
}
