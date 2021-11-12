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
	 * Create shell descriptor from existing hash map.
	 *
	 * @param map
	 *            The map, the shell descriptor is to be built from
	 */
	public AASDescriptor(Map<String, Object> map) {
		super(map);
		validate(map);

		putAll(map);
	}

	/**
	 * Create a new shell descriptor with minimal information (idShort is assumed to
	 * be set to an empty string).
	 *
	 * @param shellIdentifier
	 * @param endpoint
	 */
	public AASDescriptor(IIdentifier shellIdentifier, Endpoint endpoint) {
		this("", shellIdentifier, endpoint);
	}

	/**
	 * Create a new shell descriptor with minimal information.
	 *
	 * @param idShort
	 * @param shellIdentifier
	 * @param endpoint
	 */
	public AASDescriptor(String idShort, IIdentifier shellIdentifier, Endpoint endpoint) {
		super(idShort, shellIdentifier, endpoint);

		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new shell descriptor that retrieves the necessary information from a
	 * passed AssetAdministrationShell.
	 *
	 * @param assetAdministrationShell
	 * @param endpoint
	 */
	public AASDescriptor(IAssetAdministrationShell assetAdministrationShell, Endpoint endpoint) {
		this(assetAdministrationShell.getIdShort(), assetAdministrationShell.getIdentification(), endpoint);
	}

	/**
	 * Create a new shell descriptor with shellId, idShort, globalAssetId,
	 * specificAssetId, and endpoint.
	 *
	 * @param idShort
	 * @param aasid
	 * @param globalAssetId
	 * @param specificAssetIds
	 * @param endpoint
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, GlobalAssetId globalAssetId, SpecificAssetId specificAssetIds, Endpoint endpoint) {
		// TODO: globalAssetId, specificAssetIds are mutually exclusive
		// TODO: constructor from Collection<SpecificAssetId> specificAssetIds
		super(idShort, aasid, endpoint);

		put(GLOBAL_ASSET_ID, globalAssetId);
		put(SPECIFIC_ASSET_IDS, specificAssetIds);
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Add a sub model descriptor to this shell descriptor
	 *
	 * @param submodelDescriptor
	 * @return this AASDescriptor (Enables method chaining)
	 */
	public AASDescriptor addSubmodelDescriptor(SubmodelDescriptor submodelDescriptor) {
		Collection<Map<String, Object>> submodelDescriptors = getSubmodelsDescriptorsAsCollection();

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
		getSubmodelsDescriptorsAsCollection().remove(submodelDescriptor);
	}

	/**
	 * Tries to remove a submodel descriptor from a shell using a given submodel
	 * identifier.
	 *
	 * @param submodelIdentifier
	 */
	public void removeSubmodelDescriptor(IIdentifier submodelIdentifier) {
		SubmodelDescriptor submodelDescriptor = getSubmodelDescriptorFromIdentifier(submodelIdentifier);
		getSubmodelsDescriptorsAsCollection().remove(submodelDescriptor);
	}

	/**
	 * Retrieves a submodel descriptor based on the globally unique id of the
	 * submodel.
	 *
	 * @param submodelIdentifier
	 * @return SubmodelDescriptor by identifier of the submodel
	 */
	public SubmodelDescriptor getSubmodelDescriptorFromIdentifier(IIdentifier submodelIdentifier) {
		Optional<SubmodelDescriptor> submodelDescriptor = getSubmodelDescriptors().stream().filter(x -> x.getIdentifier().equals(submodelIdentifier)).findAny();

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
		Collection<Map<String, Object>> submodelDescriptors = getSubmodelsDescriptorsAsCollection();
		return submodelDescriptors.stream().map(SubmodelDescriptor::new).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	private Collection<Map<String, Object>> getSubmodelsDescriptorsAsCollection() {
		Object submodelDescriptorMap = get(AssetAdministrationShell.SUBMODELS);
		return (Collection<Map<String, Object>>) submodelDescriptorMap;
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	/**
	 * Validates the shell descriptor by checking whether idShort, identification
	 * and endpoints key is present in the given map and checking the type of
	 * submodel descriptors.
	 *
	 * @param map
	 */
	@Override
	public void validate(Map<String, Object> map) {
		super.validate(map);
		if (!hasMapSubmodels(map)) {
			map.put(AssetAdministrationShell.SUBMODELS, new HashSet<>());
		} else if (hasMapSubmodels(map) && !isInstanceOfCollection(map)) {
			throw new MalformedRequestException("Passed entry for " + AssetAdministrationShell.SUBMODELS + " is not a list of submodelDescriptors!");
		}
	}

	private boolean isInstanceOfCollection(Map<String, Object> map) {
		return map.get(AssetAdministrationShell.SUBMODELS) instanceof Collection<?>;
	}

	private boolean hasMapSubmodels(Map<String, Object> map) {
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
