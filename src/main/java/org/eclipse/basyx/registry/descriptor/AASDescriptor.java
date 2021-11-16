/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.registry.descriptor.parts.GlobalAssetId;
import org.eclipse.basyx.registry.descriptor.parts.SpecificAssetId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Shell descriptor class
 *
 * @author kuhn, pschorn, espen
 *
 */
public class AASDescriptor extends ModelDescriptor {
	// TODO: Builder Pattern
	public static final String MODELTYPE = "AssetAdministrationShellDescriptor";
	public static final String GLOBAL_ASSET_ID = "globalAssetId";
	public static final String SPECIFIC_ASSET_IDS = "specificAssetId";

	protected AASDescriptor() {
		super();
	}

	/**
	 * Create a new shell descriptor with minimal information.
	 *
	 * @param idShort
	 * @param shellIdentifier
	 * @param endpoints
	 */
	public AASDescriptor(String idShort, IIdentifier shellIdentifier, Collection<Endpoint> endpoints) {
		super(idShort, shellIdentifier, endpoints);

		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new shell descriptor that retrieves the necessary information from a
	 * passed AssetAdministrationShell.
	 *
	 * @param assetAdministrationShell
	 * @param endpoints
	 */
	public AASDescriptor(IAssetAdministrationShell assetAdministrationShell, Collection<Endpoint> endpoints) {
		this(assetAdministrationShell.getIdShort(), assetAdministrationShell.getIdentification(), endpoints);
	}

	/**
	 * Create a new shell descriptor with shellId, idShort, globalAssetId, and
	 * endpoint.
	 *
	 * @param idShort
	 * @param aasid
	 * @param globalAssetId
	 * @param endpoints
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, GlobalAssetId globalAssetId, Collection<Endpoint> endpoints) {
		this(idShort, aasid, endpoints);
		put(GLOBAL_ASSET_ID, globalAssetId);
	}

	/**
	 * Create a new shell descriptor with shellId, idShort, specificAssetIds, and
	 * endpoint.
	 *
	 * @param idShort
	 * @param aasid
	 * @param specificAssetIds
	 * @param endpoints
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, Collection<SpecificAssetId> specificAssetIds, Collection<Endpoint> endpoints) {
		this(idShort, aasid, endpoints);
		put(SPECIFIC_ASSET_IDS, specificAssetIds);
	}

	/**
	 * Creates a shell descriptor from a given map
	 * 
	 * @param map
	 * @return
	 */
	public static AASDescriptor createAsFacade(Map<String, Object> map) {
		if (!isValid(map)) {
			throw new MalformedRequestException("The given map '" + map + "' is not valid.");
		}

		if (!hasMapSubmodels(map)) {
			map.put(AssetAdministrationShell.SUBMODELS, new HashSet<>());
		}

		AASDescriptor facade = new AASDescriptor();
		facade.setMap(map);
		return facade;
	}

	/**
	 * Add a sub model descriptor to this shell descriptor
	 *
	 * @param submodelDescriptor
	 * @return this AASDescriptor (Enables method chaining)
	 */
	public AASDescriptor addSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) {
		Collection<Map<String, Object>> submodelDescriptors = getSubmodelsDescriptorMapAsCollection();

		submodelDescriptors.add(submodelDescriptor);
		put(AssetAdministrationShell.SUBMODELS, submodelDescriptors);

		return this;
	}

	/**
	 * Tries to remove a submodel descriptor from a shell using a given submodel
	 * idShort.
	 *
	 * @param submodelIdShort
	 */
	public void removeSubmodelDescriptor(String submodelIdShort) {
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptorFromIdShort(submodelIdShort);
		getSubmodelsDescriptorMapAsCollection().remove(submodelDescriptor);
	}

	/**
	 * Tries to remove a submodel descriptor from a shell using a given submodel
	 * identifier.
	 *
	 * @param submodelIdentifier
	 */
	public void removeSubmodelDescriptor(IIdentifier submodelIdentifier) {
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptorFromIdentifier(submodelIdentifier);
		getSubmodelsDescriptorMapAsCollection().remove(submodelDescriptor);
	}

	/**
	 * Retrieves a submodel descriptor based on the globally unique id of the
	 * submodel.
	 *
	 * @param submodelIdentifier
	 * @return SubmodelDescriptor by identifier of the submodel
	 */
	public SubmodelDescriptor getSubmodelDescriptorFromIdentifier(IIdentifier submodelIdentifier) {
		Optional<SubmodelDescriptor> submodelDescriptor = getSubmodelDescriptors().stream().filter(x -> x.getIdentifier().getId().equals(submodelIdentifier.getId())).findAny();

		if (submodelDescriptor.isPresent()) {
			return submodelDescriptor.get();
		} else {
			throw new ResourceNotFoundException("The SubmodelDescriptor with given identifier '" + submodelIdentifier.getId() + "' does not exist.");
		}
	}

	/**
	 * Retrieves a submodel descriptor based on the idShort of the submodel.
	 *
	 * @param submodelIdShort
	 * @return SubmodelDescriptor by idShort of the submodel
	 */
	public SubmodelDescriptor getSubmodelDescriptorFromIdShort(String submodelIdShort) {
		Optional<SubmodelDescriptor> submodelDescriptor = getSubmodelDescriptors().stream().filter(x -> x.getIdShort().equals(submodelIdShort)).findAny();

		if (submodelDescriptor.isPresent()) {
			return submodelDescriptor.get();
		} else {
			throw new ResourceNotFoundException("The SubmodelDescriptor with given idShort '" + submodelIdShort + "' does not exist.");
		}
	}

	/**
	 * Retrieves all submodel descriptors of the shell described by this descriptor
	 *
	 * @return Collection of submodel descriptors from the current shell
	 */
	public Collection<SubmodelDescriptor> getSubmodelDescriptors() {
		Collection<Map<String, Object>> submodelDescriptorMapCollection = getSubmodelsDescriptorMapAsCollection();
		return submodelDescriptorMapCollection.stream().map(SubmodelDescriptor::createAsFacade).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	private Collection<Map<String, Object>> getSubmodelsDescriptorMapAsCollection() {
		Object submodelDescriptorMap = get(AssetAdministrationShell.SUBMODELS);
		return (Collection<Map<String, Object>>) submodelDescriptorMap;
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	protected static boolean isValid(Map<String, Object> map) {
		if (!ModelDescriptor.isValid(map)) {
			return false;
		}

		if (hasMapSubmodels(map) && !isInstanceOfCollection(map)) {
			return false;
		}

		return true;
	}

	private static boolean isInstanceOfCollection(Map<String, Object> map) {
		return map.get(AssetAdministrationShell.SUBMODELS) instanceof Collection<?>;
	}

	private static boolean hasMapSubmodels(Map<String, Object> map) {
		return map.containsKey(AssetAdministrationShell.SUBMODELS);
	}

	/**
	 * @return The specific asset ids of this shell.
	 */
	@SuppressWarnings("unchecked")
	public Collection<SpecificAssetId> getSpecificAssetIds() {
		Object specificAssetIds = get(SPECIFIC_ASSET_IDS);
		if (!(specificAssetIds instanceof Collection<?>)) {
			return new ArrayList<>();
		}
		Collection<SpecificAssetId> returnCollection = new ArrayList<SpecificAssetId>();
		for (Map<String, Object> specificAssetIdsMap : (Collection<Map<String, Object>>) specificAssetIds) {
			returnCollection.add(SpecificAssetId.createAsFacade(specificAssetIdsMap));
		}
		return returnCollection;
	}

	/**
	 * @return The global asset id of this shell.
	 */
	@SuppressWarnings("unchecked")
	public GlobalAssetId getGlobalAssetId() {
		Object globalAssetIdMap = get(GLOBAL_ASSET_ID);
		return GlobalAssetId.createAsFacade((Map<String, Object>) globalAssetIdMap);
	}
}
