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
package org.eclipse.basyx.aas.metamodel.map.descriptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
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
	public static final String ASSET = "asset";

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
	public AASDescriptor(IAssetAdministrationShell assetAdministrationShell, String endpoint) {
		this(assetAdministrationShell.getIdShort(), assetAdministrationShell.getIdentification(), assetAdministrationShell.getAsset(), endpoint);
	}

	/**
	 * Create a new descriptor with aasid, idshort , assetid, and endpoint
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, IAsset asset, String httpEndpoint) {
		super(idShort, aasid, httpEndpoint);

		// Set Asset
		put(ASSET, asset);

		// Set Submodels
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());

		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new descriptor with minimal information
	 */
	public AASDescriptor(String idShort, IIdentifier aasid, String httpEndpoint) {
		super(idShort, aasid, httpEndpoint);

		// Set Submodels
		put(AssetAdministrationShell.SUBMODELS, new HashSet<SubmodelDescriptor>());

		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Create a new descriptor with minimal information (idShort is assumed to be
	 * set to "")
	 */
	public AASDescriptor(IIdentifier aasid, String httpEndpoint) {
		this("", aasid, httpEndpoint);
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

	/**
	 * Get asset
	 */
	@SuppressWarnings("unchecked")
	public IAsset getAsset() {
		Map<String, Object> assetModel = (Map<String, Object>) get(ASSET);
		return Asset.createAsFacade(assetModel);
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

	/**
	 * Creates the AASDescriptor from a given map
	 * 
	 * @param map
	 * @return
	 */
	public static AASDescriptor createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		AASDescriptor ret = new AASDescriptor();
		ret.setMap(map);
		return ret;
	}
}
