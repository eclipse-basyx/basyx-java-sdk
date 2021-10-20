/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.memory;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * An interface for a registry handler for different types of registry datasources.
 * 
 * @author espen
 *
 */
public interface IRegistryHandler {
	/**
	 * Queries the registry datasource to check, if an entry with the given identifier exists.
	 * 
	 * @param id The asset- or AAS-identifier that will be checked
	 * @return True, if an entry with the given identifier exists
	 */
	public boolean contains(IIdentifier id);

	/**
	 * Removes an entry with a given identifier from the registry datasource
	 * 
	 * @param id The asset- or AAS-identifier that will be removed
	 */
	public void remove(IIdentifier id);

	/**
	 * Inserts a new descriptor into the registry datasource.
	 * 
	 * @param descriptor The descriptor that will be inserted.
	 */
	public void insert(AASDescriptor descriptor);

	/**
	 * Updates a given descriptor. It is assumed that an entry with the same AAS id already exists in the registry
	 * datasource.
	 * 
	 * @param descriptor The descriptor that will be inserted.
	 */
	public void update(AASDescriptor descriptor);

	/**
	 * Queries the registry datasource for a entry with the given identifier.
	 * 
	 * @param id The asset- or AAS-identifier for which the descriptor should be retrieved.
	 * @return The found descriptor from the registry datasource matching the id.
	 */
	public AASDescriptor get(IIdentifier id);

	/**
	 * Returns a list of all descriptors contained in the registry datasource.
	 * 
	 * @return The list of AASDescriptors.
	 */
	public List<AASDescriptor> getAll();
}
