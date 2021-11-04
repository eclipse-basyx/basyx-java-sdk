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

import org.eclipse.basyx.aas.registration.restapi.BaSyxRegistryPath;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * API helper for AAS Registry
 *
 * @author haque, fischer
 *
 */
public class AASRegistryAPIHelper {

	private AASRegistryAPIHelper() {
	}

	/**
	 * Retrieves base access path for shell descriptors
	 *
	 * @return base access path for shell descriptors
	 */
	public static String getShellDescriptorPath() {
		return VABPathTools.concatenatePaths(BaSyxRegistryPath.PREFIX, BaSyxRegistryPath.SHELL_DESCRIPTORS);
	}

	/**
	 * Retrieves an access path for an AAS
	 *
	 * @param aasIdentifier
	 * @return access path for an AAS
	 */
	public static String getAASAccessPath(IIdentifier aasIdentifier) {
		return VABPathTools.concatenatePaths(getShellDescriptorPath(), VABPathTools.encodePathElement(aasIdentifier.getId()));
	}

	/**
	 * Retrieves an access path for all the submodels of a given AAS
	 *
	 * @param aasIdentifier
	 * @return access path for all submodels of a given AAS
	 */
	public static String getAASSubmodelsAccessPath(IIdentifier aasIdentifier) {
		return VABPathTools.concatenatePaths(getAASAccessPath(aasIdentifier), BaSyxRegistryPath.SUBMODEL_DESCRIPTORS);
	}

	/**
	 * Retrieves an access path for a submodel
	 *
	 * @param aasIdentifier
	 * @param submodelId
	 * @return access path for a submodel
	 */
	public static String getSubmodelAccessPath(IIdentifier aasIdentifier, IIdentifier submodelId) {
		return VABPathTools.concatenatePaths(getAASSubmodelsAccessPath(aasIdentifier), VABPathTools.encodePathElement(submodelId.getId()));
	}
}
