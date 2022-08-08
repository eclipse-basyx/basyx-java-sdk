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
package org.eclipse.basyx.aas.registration.api;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * BaSys registry interface
 * 
 * @author kuhn
 *
 */
public interface IAASRegistry {
	
	public default String getRegistryId() {
		return "default";
	}

	/**
	 * Register AAS descriptor in registry, delete old registration
	 */
	public void register(AASDescriptor deviceAASDescriptor) throws ProviderException;

	/**
	 * Register SM descriptor in registry, delete old registration
	 */
	public void register(IIdentifier aas, SubmodelDescriptor smDescriptor) throws ProviderException;

	/**
	 * Delete AAS descriptor from registry
	 */
	public void delete(IIdentifier aasId) throws ProviderException;

	/**
	 * Delete SM descriptor from registry
	 */
	public void delete(IIdentifier aasId, IIdentifier smId) throws ProviderException;

	/**
	 * Lookup AAS
	 */
	public AASDescriptor lookupAAS(IIdentifier aasId) throws ProviderException;

	/**
	 * Retrieve all registered AAS
	 * 
	 * @return
	 */
	public List<AASDescriptor> lookupAll() throws ProviderException;

	/**
	 * Retrieves all SubmodelDescriptors of submodels of an AAS
	 * 
	 * @param aasId
	 *            of the AAS
	 * @return list of SubmodelDescriptors
	 * @throws ProviderException
	 */
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasId) throws ProviderException;

	/**
	 * Retrieves the SubmodelDescriptor of a specific submodel of an AAS
	 * 
	 * @param aasId
	 *            of the AAS
	 * @param smId
	 *            of the Submodel
	 * @return the SubmodelDescriptor
	 * @throws ProviderException
	 */
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasId, IIdentifier smId) throws ProviderException;

}
