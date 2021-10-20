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
 * @author kuhn
 *
 */
public interface IAASRegistry {
	
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

