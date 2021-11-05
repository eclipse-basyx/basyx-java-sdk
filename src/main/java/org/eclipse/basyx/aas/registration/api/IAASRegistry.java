/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.api;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * BaSys registry interface
 *
 * @author kuhn, fischer
 *
 */
public interface IAASRegistry {

	/**
	 * Registers an AASDescriptor in the registry, if not already existing.
	 *
	 * @param aasDescriptor
	 * @throws ProviderException
	 */
	public void register(AASDescriptor aasDescriptor) throws ProviderException;

	/**
	 * Updates the AASDescriptor in the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param aasDescriptor
	 * @throws ProviderException
	 */
	public void update(IIdentifier aasIdentifier, AASDescriptor aasDescriptor) throws ProviderException;

	/**
	 * Registers a SubmodelDescriptor in the registry, if not already existing.
	 *
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	// public void register(SubmodelDescriptor submodelDescriptor) throws
	// ProviderException;

	/**
	 * Registers a SubmodelDescriptor in the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	public void register(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException;

	/**
	 * Deletes the AASDescriptor from the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @throws ProviderException
	 */
	public void delete(IIdentifier aasIdentifier) throws ProviderException;

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param aasIdentifier
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	public void delete(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException;

	/**
	 * Looks up the AASDescriptor for the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @return AASDescriptor with the given aasIdentifier
	 * @throws ProviderException
	 */
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException;

	/**
	 * Retrieves all registered AASDescriptors.
	 *
	 * @return List of all registered AASDescriptors
	 * @throws ProviderException
	 */
	public List<AASDescriptor> lookupAll() throws ProviderException;

	/**
	 * Retrieves all SubmodelDescriptors for the AAS with the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @return the list of all SubmodelDescriptors part of the given AAS
	 * @throws ProviderException
	 */
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasIdentifier) throws ProviderException;

	/**
	 * Retrieves the SubmodelDescriptor with the given submodelIdentifier that is
	 * part of the AAS with the given aasIdentifier.
	 *
	 * @param aasIdentifier
	 * @param submodelIdentifier
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException;

}
