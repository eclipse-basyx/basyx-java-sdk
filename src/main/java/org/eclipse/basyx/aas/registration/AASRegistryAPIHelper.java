/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.aas.registration;

import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * API helper for AAS Registry
 * @author haque
 *
 */
public class AASRegistryAPIHelper {
	
	/**
	 * Retrieves base access path
	 * @return
	 */
	public static String getRegistryPath() {
		return AASRegistryModelProvider.PREFIX;
	}
	
	/**
	 * Retrieves an access path for an AAS
	 * @param aasId
	 * @return
	 */
	public static String getAASPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getRegistryPath(), VABPathTools.encodePathElement(aasId.getId()));
	}
	
	/**
	 * Retrieves an access path for all the submodels inside an AAS
	 * @param aasId
	 * @return
	 */
	public static String getSubmodelListOfAASPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getRegistryPath(), buildSubmodelPath(aasId));
	}
	
	
	/**
	 * Retrieves an access path for a submodel
	 * @param aasId
	 * @param submodelId
	 * @return
	 */
	public static String getSubmodelAccessPath(IIdentifier aasId, IIdentifier submodelId) {
		return VABPathTools.concatenatePaths(getSubmodelListOfAASPath(aasId), VABPathTools.encodePathElement(submodelId.getId()));
	}
	
	private static String buildSubmodelPath(IIdentifier aas) throws ProviderException {
		// Encode id to handle usage of reserved symbols, e.g. /
		String encodedAASId = VABPathTools.encodePathElement(aas.getId());
		return VABPathTools.concatenatePaths(encodedAASId, AASRegistryModelProvider.SUBMODELS);
	}
}
