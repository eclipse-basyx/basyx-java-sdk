/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.map.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Endpoint;
import org.eclipse.basyx.aas.metamodel.map.parts.GlobalAssetId;
import org.eclipse.basyx.aas.metamodel.map.parts.SpecificAssetId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * AAS descriptor class
 * 
 * @author kuhn, pschorn, espen
 *
 */
public class AASDescriptor extends ModelDescriptor {
	public static final String MODELTYPE = "AssetAdministrationShellDescriptor";
	public static final String GLOBAL_ASSET_ID = "globalAssetId";
	public static final String SPECIFIC_ASSET_IDS = "specificAssetId";

	/**
	 * Create descriptor from existing hash map.
	 * 
	 * @param map
	 *            The map, the AASDesbriptor is to be built from
	 */
	public AASDescriptor(Map<String, Object> map) {
		super(map);
		validate(map);

		putAll(map);
	}

	protected AASDescriptor() {
		super();
	}

	/**
	 * Create a new aas descriptor that retrieves the necessary information from a
	 * passed AssetAdministrationShell.
	 * 
	 * @param assetAdministrationShell
	 * @param endpoint
	 */
	public AASDescriptor(IAssetAdministrationShell assetAdministrationShell, Endpoint endpoint) {
		this(assetAdministrationShell.getIdShort(), assetAdministrationShell.getIdentification(), endpoint);
	}

	/**
	 * Create a new aas descriptor with shellId, idShort, globalAssetId,
	 * specificAssetId, and endpoint.
	 * 
	 * @param idShort
	 * @param aasid
	 * @param globalAssetId
	 * @param specificAssetIds
	 * @param endpoint
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, GlobalAssetId globalAssetId, SpecificAssetId specificAssetIds, Endpoint endpoint) {
		super(idShort, aasid, endpoint);

		put(GLOBAL_ASSET_ID, globalAssetId);
		put(SPECIFIC_ASSET_IDS, specificAssetIds);
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new aas descriptor with minimal information.
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
	 * Create a new aas descriptor with minimal information (idShort is assumed to
	 * be set to "").
	 * 
	 * @param aasid
	 * @param endpoint
	 */
	public AASDescriptor(IIdentifier aasid, Endpoint endpoint) {
		this("", aasid, endpoint);
	}

	/**
	 * Add a sub model descriptor to this aas descriptor
	 * 
	 * @param desc
	 * @return this AASDescriptor (Enables method chaining)
	 */
	public AASDescriptor addSubmodelDescriptor(SubmodelDescriptor desc) {
		Object submodels = extractShellSubmodels();
		Collection<Map<String, Object>> submodelDescriptors = convertSubmodelsToList(submodels);

		submodelDescriptors.add(desc);
		put(AssetAdministrationShell.SUBMODELS, submodelDescriptors);

		return this;
	}

	/**
	 * Removes a submodel descriptor from a shell using a given submodel idShort.
	 * 
	 * @param idShort
	 */
	public void removeSubmodelDescriptor(String idShort) {
		tryRemoveSubmodelDescriptor(idShort);
	}

	/**
	 * Removes a submodel descriptor from a shell using a given submodel identifier.
	 * 
	 * @param identifier
	 */
	public void removeSubmodelDescriptor(IIdentifier identifier) {
		tryRemoveSubmodelDescriptor(identifier);
	}

	/**
	 * Tries to remove a submodel descriptor from a shell using a given submodel
	 * idShort.
	 * 
	 * @param submodelDescriptor
	 *            The SubmodelDescriptor to be removed
	 * @param idShort
	 *            The submodel idShort to be used
	 */
	private void tryRemoveSubmodelDescriptor(String idShort) {
		try {
			Optional<SubmodelDescriptor> submodelDescriptor = getSubmodelDescriptors().stream().filter(x -> x.getIdShort().equals(idShort)).findAny();
			removeSubmodelDescriptorHelper(submodelDescriptor);
		} catch (NullPointerException e) {
			throw new ResourceNotFoundException("SubmodelDescriptor with idShort '" + idShort + "' does not exist.");
		}
	}

	/**
	 * Tries to remove a submodel descriptor from a shell using a given submodel
	 * identifier.
	 * 
	 * @param submodelDescriptor
	 *            The submodel descriptor to be removed
	 * @param identifier
	 *            The submodel identifier to be used
	 */
	private void tryRemoveSubmodelDescriptor(IIdentifier identifier) {
		try {
			Optional<SubmodelDescriptor> submodelDescriptor = getSubmodelDescriptors().stream().filter(x -> x.getIdentifier().getId().equals(identifier.getId())).findAny();
			removeSubmodelDescriptorHelper(submodelDescriptor);
		} catch (NullPointerException e) {
			throw new ResourceNotFoundException("SubmodelDescriptor with identifier '" + identifier.getId() + "' does not exist.");
		}
	}

	@SuppressWarnings("unchecked")
	private void removeSubmodelDescriptorHelper(Optional<SubmodelDescriptor> submodelDescriptor) throws NullPointerException {
		// Don't use getSubmodelDescriptors here since it returns a copy
		((Collection<Object>) extractShellSubmodels()).remove(submodelDescriptor.get());
	}

	/**
	 * Retrieves a submodel descriptor based on the globally unique id of the
	 * submodel.
	 * 
	 * @param subModelId
	 * @return SubmodelDescriptor by identifier from Shell
	 */
	public SubmodelDescriptor getSubmodelDescriptorFromIdentifierId(String subModelId) {
		// Sub model descriptors are stored in a list
		Object submodels = extractShellSubmodels();
		Collection<Map<String, Object>> smDescriptorMaps = convertSubmodelsToList(submodels);

		// Go through all descriptors (as maps) and find the one with the subModelId
		for (Map<String, Object> smDescriptorMap : smDescriptorMaps) {
			// Use a facade to access the identifier
			IIdentifier id = Identifiable.createAsFacade(smDescriptorMap, KeyElements.SUBMODEL).getIdentification();
			if (id.getId().equals(subModelId)) {
				return new SubmodelDescriptor(smDescriptorMap);
			}
		}

		// No descriptor found
		return null;
	}

	/**
	 * Retrieves a submodel descriptor based on the idShort of the submodel
	 * 
	 * @param idShort
	 * @return
	 */
	public SubmodelDescriptor getSubmodelDescriptorFromIdShort(String idShort) {
		return getSubmodelDescriptors().stream().filter(x -> x.getIdShort().equals(idShort)).findAny().orElse(null); // TODO: Exception
	}

	/**
	 * Get a specific sub model descriptor from a ModelUrn
	 */
	public SubmodelDescriptor getSubmodelDescriptor(ModelUrn submodelUrn) {
		return getSubmodelDescriptorFromIdentifierId(submodelUrn.getURN());
	}

	/**
	 * Retrieves all submodel descriptors of the aas described by this descriptor
	 * 
	 * @return
	 */
	public Collection<SubmodelDescriptor> getSubmodelDescriptors() {
		Object submodels = extractShellSubmodels();
		Collection<Map<String, Object>> descriptors = convertSubmodelsToList(submodels);
		return descriptors.stream().map(SubmodelDescriptor::new).collect(Collectors.toSet());
	}

	private Object extractShellSubmodels() {
		return get(AssetAdministrationShell.SUBMODELS);
	}

	@SuppressWarnings("unchecked")
	private Collection<Map<String, Object>> convertSubmodelsToList(Object submodelMap) {
		return (Collection<Map<String, Object>>) submodelMap;
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	/**
	 * Validates the aas descriptor by checking whether idShort, identification and
	 * endpoints key is present in the given map and checking the type of submodel
	 * descriptors.
	 *
	 * @param map
	 */
	@Override
	public void validate(Map<String, Object> map) {
		super.validate(map);
		if (!MapHasSubmodels(map)) {
			map.put(AssetAdministrationShell.SUBMODELS, new HashSet<>());
		} else if (MapHasSubmodels(map) && !isInstanceOfCollection(map)) {
			throw new MalformedRequestException("Passed entry for " + AssetAdministrationShell.SUBMODELS + " is not a list of submodelDescriptors!");
		}
	}

	private boolean isInstanceOfCollection(Map<String, Object> map) {
		return map.get(AssetAdministrationShell.SUBMODELS) instanceof Collection<?>;
	}

	private boolean MapHasSubmodels(Map<String, Object> map) {
		return map.containsKey(AssetAdministrationShell.SUBMODELS);
	}

	/**
	 * @return The specific asset ids of this aas.
	 */
	@SuppressWarnings("unchecked")
	public Collection<SpecificAssetId> getSpecificAssetIds() {
		Object specificAssetIds = get(SPECIFIC_ASSET_IDS);
		if (!(specificAssetIds instanceof Collection<?>)) {
			return new ArrayList<>();
		}
		Collection<SpecificAssetId> ret = new ArrayList<SpecificAssetId>();
		for (Map<String, Object> specificAssetIdsMap : (Collection<Map<String, Object>>) specificAssetIds) {
			ret.add(SpecificAssetId.createAsFacade(specificAssetIdsMap));
		}
		return ret;
	}

	/**
	 * @return The global asset id of this aas.
	 */
	@SuppressWarnings("unchecked")
	public GlobalAssetId getGlobalAssetId() {
		Object globalAssetIdMap = get(GLOBAL_ASSET_ID);
		return GlobalAssetId.createAsFacade((Map<String, Object>) globalAssetIdMap);
	}
}
