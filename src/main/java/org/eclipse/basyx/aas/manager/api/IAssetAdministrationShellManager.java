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
package org.eclipse.basyx.aas.manager.api;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;



/**
 * Technology independent interface to technology specific Asset Administration Shell (AAS) manager class. 
 * 
 * @author schoeffler, ziesche, kuhn
 *
 */
public interface IAssetAdministrationShellManager {
	/**
	 * Retrieve an AAS based on its ID
	 */
	public IAssetAdministrationShell retrieveAAS(IIdentifier aasId) throws Exception;
	
	/**
	 * Retrieve all local AAS from the technology layer
	 */
	public Collection<IAssetAdministrationShell> retrieveAASAll();
	
	/**
	 * Creates an AAS on a remote server
	 * 
	 * @param aas
	 * @param endpoint
	 */
	void createAAS(AssetAdministrationShell aas, String endpoint);

	/**
	 * Unlink an AAS from the system
	 */
	void deleteAAS(IIdentifier id) throws Exception;

	/**
	 * Retrieves a submodel
	 */
	ISubmodel retrieveSubmodel(IIdentifier aasId, IIdentifier subModelId);

	/**
	 * Creates a submodel on a remote server. Assumes that the AAS is already
	 * registered in the directory
	 */
	void createSubmodel(IIdentifier aasId, Submodel submodel);

	/**
	 * Deletes a submodel on a remote server and removes its registry entry
	 * 
	 * @param aasId
	 * @param submodelId
	 */
	void deleteSubmodel(IIdentifier aasId, IIdentifier submodelId);

	/**
	 * Retrieves all submodels in a specific AAS
	 */
	Map<String, ISubmodel> retrieveSubmodels(IIdentifier aasId);
}
