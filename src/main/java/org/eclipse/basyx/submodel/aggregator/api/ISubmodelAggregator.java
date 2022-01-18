/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.aggregator.api;

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Interface for the Submodel Aggregator API <br>
 * It is used to manage multiple Submodels at the same endpoint
 * 
 * @author espen
 *
 */
public interface ISubmodelAggregator {
	/**
	 * Retrieves all Submodels from the endpoint
	 * 
	 * @return a List of all found Submodels
	 */
	public Collection<ISubmodel> getSubmodelList();

	/**
	 * Retrieves a specific Submodel
	 * 
	 * @param identifier
	 *            the Id of the Submodel
	 * @return the requested Submodel
	 */
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException;

	/**
	 * Retrieves a specific Submodel
	 * 
	 * @param idShort
	 *            the IdShort of the Submodel
	 * @return the requested Submodel
	 */
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException;

	/**
	 * Retrieves the API for a specific Submodel
	 * 
	 * @param identifier
	 *            the ID of the Submodel
	 * @return the requested Submodel API
	 */
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException;

	/**
	 * Retrieves the API for a specific Submodel
	 * 
	 * @param idShort
	 *            the idShort of the Submodel
	 * @return the requested Submodel API
	 */
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException;

	/**
	 * Creates a new Submodel at the endpoint
	 * 
	 * @param submodel
	 *            the Submodel to be created
	 */
	public void createSubmodel(Submodel submodel);
	
	/**
	 * Creates a new Submodel using an API
	 * 
	 * @param submodelAPI
	 *            the Submodel API to be added
	 */
	public void createSubmodel(ISubmodelAPI submodelAPI);
	
	/**
	 * Updates a specific Submodel
	 * 
	 * @param submodel
	 *            the updated Submodel
	 */
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException;

	/**
	 * Deletes a specific Submodel
	 * 
	 * @param identifier
	 *            the ID of the Submodel to be deleted
	 */
	public void deleteSubmodelByIdentifier(IIdentifier identifier);

	/**
	 * Deletes a specific Submodel
	 * 
	 * @param idShort
	 *            the idShort of the Submodel to be deleted
	 */
	public void deleteSubmodelByIdShort(String idShort);
}
