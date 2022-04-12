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
