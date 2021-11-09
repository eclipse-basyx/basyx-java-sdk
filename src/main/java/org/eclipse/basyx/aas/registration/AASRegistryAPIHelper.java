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
	 * Retrieves base access path for shellDescriptors
	 *
	 * @return base access path for shellDescriptors
	 */
	public static String getAllShellDescriptorsPath() {
		return VABPathTools.concatenatePaths(BaSyxRegistryPath.PREFIX, BaSyxRegistryPath.SHELL_DESCRIPTORS);
	}

	/**
	 * Retrieves base access path for submodelDescriptors
	 *
	 * @return base access path for submodelDescriptors
	 */
	public static String getAllSubmodelDescriptorsPath() {
		return VABPathTools.concatenatePaths(BaSyxRegistryPath.PREFIX, BaSyxRegistryPath.SUBMODEL_DESCRIPTORS);
	}

	/**
	 * Retrieves an access path for a shell
	 *
	 * @param shellIdentifier
	 * @return access path for a shell
	 */
	public static String getSingleShellDescriptorPath(IIdentifier shellIdentifier) {
		return VABPathTools.concatenatePaths(getAllShellDescriptorsPath(), VABPathTools.encodePathElement(shellIdentifier.getId()));
	}

	/**
	 * Retrieves an access path for a submodel
	 *
	 * @param submodelIdentifier
	 * @return access path for a submodel
	 */
	public static String getSingleSubmodelDescriptorPath(IIdentifier submodelIdentifier) {
		return VABPathTools.concatenatePaths(getAllSubmodelDescriptorsPath(), VABPathTools.encodePathElement(submodelIdentifier.getId()));
	}

	/**
	 * Retrieves an access path for all the submodels of a given shell
	 *
	 * @param shellIdentifier
	 * @return access path for all submodels of a given shell
	 */
	public static String getSingleShellDescriptorAllSubmodelDescriptorsPath(IIdentifier shellIdentifier) {
		return VABPathTools.concatenatePaths(getSingleShellDescriptorPath(shellIdentifier), BaSyxRegistryPath.SUBMODEL_DESCRIPTORS);
	}

	/**
	 * Retrieves an access path for a submodel
	 *
	 * @param shellIdentifier
	 * @param submodelId
	 * @return access path for a submodel
	 */
	public static String getSingleShellDescriptorSingleSubmodelDescriptorPath(IIdentifier shellIdentifier, IIdentifier submodelId) {
		return VABPathTools.concatenatePaths(getSingleShellDescriptorAllSubmodelDescriptorsPath(shellIdentifier), VABPathTools.encodePathElement(submodelId.getId()));
	}
}
