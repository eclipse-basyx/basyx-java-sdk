/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.aggregator.api;

import java.util.Collection;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;


/**
 * Interface for the Asset Administration Shell Aggregator API <br>
 * It is used to manage multiple AASs at the same endpoint
 * 
 * @author conradi
 *
 */
public interface IAASAggregator {
	
	/**
	 * Retrieves all Asset Administration Shells from the endpoint
	 * 
	 * @return a List of all found Asset Administration Shells
	 */
	public Collection<IAssetAdministrationShell> getAASList();
	
	/**
	 * Retrieves a specific Asset Administration Shell
	 * 
	 * @param aasId the ID of the AAS
	 * @return the requested AAS
	 */
	public IAssetAdministrationShell getAAS(IIdentifier aasId) throws ResourceNotFoundException;
	
	/**
	 * Retrieves the provider for a specific Asset Administration Shell
	 * 
	 * @param aasId the ID of the AAS
	 * @return the requested AAS provider
	 */
	public IModelProvider getAASProvider(IIdentifier aasId) throws ResourceNotFoundException;

	/**
	 * Creates a new Asset Administration Shell at the endpoint
	 * 
	 * @param aas the AAS to be created
	 */
	public void createShell(AssetAdministrationShell aas);
	
	/**
	 * Updates a specific Asset Administration Shell
	 * 
	 * @param aas the updated AAS
	 */
	public void updateAAS(AssetAdministrationShell aas) throws ResourceNotFoundException;
	
	/**
	 * Deletes a specific Asset Administration Shell
	 * 
	 * @param aasId the ID of the AAS to be deleted
	 */
	public void deleteAAS(IIdentifier aasId);

}
