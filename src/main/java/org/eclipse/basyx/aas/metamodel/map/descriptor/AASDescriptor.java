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
import org.eclipse.basyx.aas.metamodel.map.endpoint.Endpoint;
import org.eclipse.basyx.aas.metamodel.map.parts.GlobalAssetId;
import org.eclipse.basyx.aas.metamodel.map.parts.SpecificAssetId;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

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
	 * Create descriptor from existing hash map
	 */
	public AASDescriptor(Map<String, Object> map) {
		super(map);
		validate(map);

		// Set map again
		putAll(map);
	}

	protected AASDescriptor() {
		super();
	}

	/**
	 * Create a new aas descriptor that retrieves the necessary information from a
	 * passed AssetAdministrationShell
	 * 
	 * @param assetAdministrationShell
	 * @param endpoint
	 */
	public AASDescriptor(IAssetAdministrationShell assetAdministrationShell, Endpoint endpoint) {
		this(assetAdministrationShell.getIdShort(), assetAdministrationShell.getIdentification(), endpoint);
	}

	/**
	 * Create a new descriptor with shellId, idShort, globalAssetId,
	 * specificAssetId, and endpoint
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, GlobalAssetId globalAssetId, SpecificAssetId specificAssetIds, Endpoint endpoint) {
		super(idShort, aasid, endpoint);

		// Set Global Asset Id
		put(GLOBAL_ASSET_ID, globalAssetId);

		// Set Specific Asset Ids
		put(SPECIFIC_ASSET_IDS, specificAssetIds);

		// Set Submodels
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());

		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new descriptor with minimal information
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, Endpoint endpoint) {
		super(idShort, aasid, endpoint);

		// Set Submodels
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());

		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new descriptor with minimal information (idShort is assumed to be
	 * set to "")
	 */
	public AASDescriptor(IIdentifier aasid, Endpoint endpoint) {
		this("", aasid, endpoint);
	}

	/**
	 * Add a sub model descriptor
	 */
	@SuppressWarnings("unchecked")
	public AASDescriptor addSubmodelDescriptor(SubmodelDescriptor desc) {
		// Sub model descriptors are stored in a list
		Collection<Map<String, Object>> submodelDescriptors = (Collection<Map<String, Object>>) get(AssetAdministrationShell.SUBMODELS);

		// Add new sub model descriptor to list
		submodelDescriptors.add(desc);
		put(AssetAdministrationShell.SUBMODELS, submodelDescriptors);

		// Enable method chaining
		return this;
	}

	@SuppressWarnings("unchecked")
	public void removeSubmodelDescriptor(String idShort) {
		Optional<SubmodelDescriptor> toRemove = getSubmodelDescriptors().stream().filter(x -> x.getIdShort().equals(idShort)).findAny();

		// TODO: Exception in else case
		if (toRemove.isPresent()) {
			// Don't use getSubmodelDescriptors here since it returns a copy
			((Collection<Object>) get(AssetAdministrationShell.SUBMODELS)).remove(toRemove.get());
		}
	}

	@SuppressWarnings("unchecked")
	public void removeSubmodelDescriptor(IIdentifier id) {
		Optional<SubmodelDescriptor> toRemove = getSubmodelDescriptors().stream().filter(x -> x.getIdentifier().getId().equals(id.getId())).findAny();

		// TODO: Exception in else case
		if (toRemove.isPresent()) {
			// Don't use getSubmodelDescriptors here since it returns a copy
			((Collection<Object>) get(AssetAdministrationShell.SUBMODELS)).remove(toRemove.get());
		}
	}

	/**
	 * Retrieves a submodel descriptor based on the globally unique id of the
	 * submodel
	 * 
	 * @param subModelId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SubmodelDescriptor getSubmodelDescriptorFromIdentifierId(String subModelId) {
		// Sub model descriptors are stored in a list
		Collection<Map<String, Object>> smDescriptorMaps = (Collection<Map<String, Object>>) get(AssetAdministrationShell.SUBMODELS);

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
	@SuppressWarnings("unchecked")
	public Collection<SubmodelDescriptor> getSubmodelDescriptors() {
		Collection<Map<String, Object>> descriptors = (Collection<Map<String, Object>>) get(AssetAdministrationShell.SUBMODELS);
		return descriptors.stream().map(SubmodelDescriptor::new).collect(Collectors.toSet());
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	@Override
	public void validate(Map<String, Object> map) {
		super.validate(map);
		if (!map.containsKey(AssetAdministrationShell.SUBMODELS)) {
			map.put(AssetAdministrationShell.SUBMODELS, new HashSet<>());
		} else if (map.containsKey(AssetAdministrationShell.SUBMODELS) && !(map.get(AssetAdministrationShell.SUBMODELS) instanceof Collection<?>)) {
			throw new MalformedRequestException("Passed entry for " + AssetAdministrationShell.SUBMODELS + " is not a list of submodelDescriptors!");
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<SpecificAssetId> getSpecificAssetIds() {
		Object specificAssetIds = get(SPECIFIC_ASSET_IDS);
		if (specificAssetIds instanceof Collection<?>) {
			Collection<SpecificAssetId> ret = new ArrayList<SpecificAssetId>();
			for (Map<String, Object> specificAssetIdsMap : (Collection<Map<String, Object>>) specificAssetIds) {
				ret.add(SpecificAssetId.createAsFacade(specificAssetIdsMap));
			}
			return ret;
		} else {
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("unchecked")
	public GlobalAssetId getGlobalAssetId() {
		Object globalAssetIdMap = get(GLOBAL_ASSET_ID);
		return GlobalAssetId.createAsFacade((Map<String, Object>) globalAssetIdMap);
	}
}
