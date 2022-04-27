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

package org.eclipse.basyx.aas.registration;

import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * API helper for AAS Registry
 * 
 * @author haque
 *
 */
public class AASRegistryAPIHelper {

	/**
	 * Retrieves base access path
	 * 
	 * @return
	 */
	public static String getRegistryPath() {
		return AASRegistryModelProvider.PREFIX;
	}

	/**
	 * Retrieves an access path for an AAS
	 * 
	 * @param aasId
	 * @return
	 */
	public static String getAASPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getRegistryPath(), VABPathTools.encodePathElement(aasId.getId()));
	}

	/**
	 * Retrieves an access path for all the submodels inside an AAS
	 * 
	 * @param aasId
	 * @return
	 */
	public static String getSubmodelListOfAASPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getRegistryPath(), buildSubmodelPath(aasId));
	}

	/**
	 * Retrieves an access path for a submodel
	 * 
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
