/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.aas.bundle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creation of AASBundles from Sets of AAS, Submodels and Assets
 * 
 * @author schnicke
 *
 */
public class AASBundleFactory {
	private static Logger logger = LoggerFactory.getLogger(AASBundleFactory.class);

	/**
	 * Creates from a collection of AAS, Submodels and Assets the appropriate set of
	 * AASBundles
	 * 
	 * @param shells
	 * @param submodels
	 * @param assets
	 * @return
	 */
	public Set<AASBundle> create(Collection<? extends IAssetAdministrationShell> shells,
			Collection<? extends ISubmodel> submodels, Collection<? extends IAsset> assets) {
		Set<AASBundle> bundles = new HashSet<>();

		for (IAssetAdministrationShell shell : shells) {
			if (shouldSetAsset(shell)) {
				setAsset(assets, shell);
			}

			// Retrieve submodels
			Set<ISubmodel> currentSM = retrieveSubmodelsForAAS(submodels, shell);
			bundles.add(new AASBundle(shell, currentSM));
		}

		return bundles;
	}

	private boolean shouldSetAsset(IAssetAdministrationShell shell) {
		return shell.getAsset() == null && shell.getAssetReference() != null;
	}

	private void setAsset(Collection<? extends IAsset> assets, IAssetAdministrationShell shell) {
		// Retrieve asset
		try {
			IReference assetRef = shell.getAssetReference();
			IAsset asset = getByReference(assetRef, assets);
			((AssetAdministrationShell) shell).setAsset((Asset) asset);
		} catch (ResourceNotFoundException e) {
			// Enables parsing external aasx-files without any keys in assetref
			if (shell.getAssetReference().getKeys().size() > 0) {
				logger.warn("Can't find asset with id " + shell.getAssetReference().getKeys().get(0).getValue() + " for AAS " + shell.getIdShort() + "; If the asset is not provided in another way, this is an error!");
			} else {
				logger.warn("Can't find asset for AAS " + shell.getIdShort() + "; If the asset is not provided in another way, this is an error!");
			}
		}
	}

	/**
	 * Retrieves the Submodels belonging to an AAS
	 * 
	 * @param submodels
	 * @param shell
	 * @return
	 */
	private Set<ISubmodel> retrieveSubmodelsForAAS(Collection<? extends ISubmodel> submodels,
			IAssetAdministrationShell shell) {
		Set<ISubmodel> currentSM = new HashSet<>();

		for (IReference submodelRef : shell.getSubmodelReferences()) {
			try {
				ISubmodel sm = getByReference(submodelRef, submodels);
				currentSM.add(sm);
				logger.debug("Found Submodel " + sm.getIdShort() + " for AAS " + shell.getIdShort());
			} catch (ResourceNotFoundException e) {
				// If there's no match, the submodel is assumed to be provided by different
				// means, e.g. it is already being hosted
				logger.warn("Could not find Submodel " + submodelRef.getKeys().get(0).getValue() + " for AAS " + shell.getIdShort() + "; If it is not hosted elsewhere this is an error!");
			}
		}
		return currentSM;
	}

	/**
	 * Retrieves an identifiable from a list of identifiable by its reference
	 * 
	 * @param submodelRef
	 * @param identifiable
	 * @return
	 * @throws ResourceNotFoundException
	 */
	private <T extends IIdentifiable> T getByReference(IReference ref, Collection<T> identifiable)
			throws ResourceNotFoundException {
		IKey lastKey = null;
		// It may be that only one key fits to the Submodel contained in the XML
		for (IKey key : ref.getKeys()) {
			lastKey = key;
			// There will only be a single submodel matching the identification at max
			Optional<T> match = identifiable.stream().filter(s -> s.getIdentification().getId().equals(key.getValue())).findFirst();
			if (match.isPresent()) {
				return match.get();
			}
		}
		if (lastKey == null) {
			throw new ResourceNotFoundException("Could not resolve reference without keys");
		} else {
			throw new ResourceNotFoundException("Could not resolve reference with last key '" + lastKey.getValue() + "'");
		}
	}
}
