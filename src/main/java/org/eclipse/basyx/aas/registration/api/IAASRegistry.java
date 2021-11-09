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
	 * Registers a ShellDescriptor in the registry, if not already existing.
	 *
	 * @param shellDescriptor
	 * @throws ProviderException
	 */
	public void register(AASDescriptor shellDescriptor) throws ProviderException;

	/**
	 * Registers a SubmodelDescriptor in the registry, if not already existing.
	 *
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	public void register(SubmodelDescriptor submodelDescriptor) throws ProviderException;

	/**
	 * Registers a SubmodelDescriptor in the registry for the shell with the given
	 * identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	public void registerSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException;

	/**
	 * Updates the ShellDescriptor in the registry with the given identifier.
	 *
	 * @param shellIdentifier
	 * @param shellDescriptor
	 * @throws ProviderException
	 */
	public void updateShell(IIdentifier shellIdentifier, AASDescriptor shellDescriptor) throws ProviderException;

	/**
	 * Updates the SubmodelDescriptor in the registry with the given identifier.
	 *
	 * @param submodelIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	public void updateSubmodel(IIdentifier submodelIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException;

	/**
	 * Updates the SubmodelDescriptor in the registry for the shell with the given
	 * identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelDescriptor
	 * @throws ProviderException
	 */
	public void updateSubmodelForShell(IIdentifier shellIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException;

	/**
	 * Deletes the Model from the registry with the given identifier.
	 * 
	 * @param identifier
	 * @throws ProviderException
	 */
	public void deleteModel(IIdentifier identifier) throws ProviderException;

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	public void deleteSubmodel(IIdentifier submodelIdentifier) throws ProviderException;

	/**
	 * Deletes the SubmodelDescriptor from the registry with the given identifier.
	 *
	 * @param shellIdentifier
	 * @param submodelIdentifier
	 * @throws ProviderException
	 */
	public void deleteSubmodelFromShell(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException;

	/**
	 * Looks up the ShellDescriptor for the given aasIdentifier.
	 *
	 * @param shellIdentifier
	 * @return AASDescriptor with the given aasIdentifier
	 * @throws ProviderException
	 */
	public AASDescriptor lookupShell(IIdentifier shellIdentifier) throws ProviderException;

	/**
	 * Retrieves all registered ShellDescriptors.
	 *
	 * @return List of all registered ShellDescriptors
	 * @throws ProviderException
	 */
	public List<AASDescriptor> lookupAllShells() throws ProviderException;

	/**
	 * Retrieves the SubmodelDescriptor for the given submodelIdentifier.
	 *
	 * @param submodelIdentifier
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	public SubmodelDescriptor lookupSubmodel(IIdentifier submodelIdentifier) throws ProviderException;

	/**
	 * Retrieves the SubmodelDescriptor for the given submodelIdentifier.
	 *
	 * @return List of all registered SubmodelDescriptors
	 * @throws ProviderException
	 */
	public List<SubmodelDescriptor> lookupAllSubmodels() throws ProviderException;

	/**
	 * Retrieves the SubmodelDescriptor with the given submodelIdentifier that is
	 * part of the Shell with the given aasIdentifier.
	 *
	 * @param shellIdentifier
	 * @param submodelIdentifier
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	public SubmodelDescriptor lookupSubmodel(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) throws ProviderException;

	/**
	 * Retrieves all SubmodelDescriptors for the Shell with the given aasIdentifier.
	 *
	 * @param shellIdentifier
	 * @return the list of all SubmodelDescriptors part of the given Shell
	 * @throws ProviderException
	 */
	public List<SubmodelDescriptor> lookupAllSubmodelsForShell(IIdentifier shellIdentifier) throws ProviderException;
}
