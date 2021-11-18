/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.memory;

import java.util.List;

import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * An interface for a registry handler for different types of registry
 * datasources.
 *
 * @author espen, fischer
 *
 */
public interface IRegistryHandler {
	/**
	 * Queries the registry datasource to check, if an entry with the given
	 * identifier exists.
	 *
	 * @param shellIdentifier
	 *            The shellIdentifier that will be checked
	 * @return True, if an entry with the given identifier exists
	 */
	public boolean containsShell(IIdentifier shellIdentifier);

	/**
	 * Queries the registry datasource to check, if an entry with the given
	 * identifier exists.
	 *
	 * @param submodelIdentifier
	 *            The submodelIdentifier that will be checked
	 * @return True, if an entry with the given identifier exists
	 */
	public boolean containsSubmodel(IIdentifier submodelIdentifier);

	/**
	 * Inserts a new AASDescriptor into the registry datasource.
	 *
	 * @param aasDescriptor
	 *            The descriptor that will be inserted.
	 */
	public void insertShell(AASDescriptor aasDescriptor);

	/**
	 * Inserts a new SubmodelDescriptor into the registry datasource.
	 *
	 * @param submodelDescriptor
	 *            The descriptor that will be inserted.
	 */
	public void insertSubmodel(SubmodelDescriptor submodelDescriptor);

	/**
	 * Updates a given shellDescriptor. It is assumed that an entry with the same
	 * shell id already exists in the registry datasource.
	 *
	 * @param shellDescriptor
	 *            The shellDescriptor that will be inserted.
	 */
	public void updateShell(AASDescriptor shellDescriptor);

	/**
	 * Updates a given SubmodelDescriptor. It is assumed that an entry with the same
	 * submodel id already exists in the registry datasource.
	 *
	 * @param submodelDescriptor
	 *            The submodelDescriptor that will be inserted.
	 */
	public void updateSubmodel(SubmodelDescriptor submodelDescriptor);

	/**
	 * Removes an entry with a given identifier from the registry datasource
	 *
	 * @param shellIdentifier
	 *            The shellIdentifier that will be removed
	 */
	public void removeShell(IIdentifier shellIdentifier);

	/**
	 * Removes an entry with a given identifier from the registry datasource
	 *
	 * @param submodelIdentifier
	 *            The submodelIdentifier that will be removed
	 */
	public void removeSubmodel(IIdentifier submodelIdentifier);

	/**
	 * Queries the registry datasource for a entry with the given identifier.
	 *
	 * @param shellIdentifier
	 *            The shellIdentifier for which the descriptor should be retrieved.
	 * @return The found descriptor from the registry datasource matching the id.
	 */
	public AASDescriptor getShell(IIdentifier shellIdentifier);

	/**
	 * Returns a list of all descriptors contained in the registry datasource.
	 *
	 * @return The list of AASDescriptors.
	 */
	public List<AASDescriptor> getAllShells();

	/**
	 * Queries the registry datasource for a entry with the given identifier.
	 *
	 * @param submodelIdentifier
	 *            The SubmodelIdentifier for which the descriptor should be
	 *            retrieved.
	 * @return The found descriptor from the registry datasource matching the id.
	 */
	public SubmodelDescriptor getSubmodel(IIdentifier submodelIdentifier);

	/**
	 * Returns a list of all descriptors contained in the registry datasource.
	 *
	 * @return The list of SubmodelDescriptors.
	 */
	public List<SubmodelDescriptor> getAllSubmodels();
}
