/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.submodel.restapi;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Helper class for Submodel API
 
 * @author haque
 */
public class SubmodelAPIHelper {
	
	/**
	 * Retrieves base access path for Submodel API
	 * @return
	 */
	public static String getSubmodelPath() {
		return "";
	}
	
	/**
	 * Retrieves base access path for submodel element
	 * @return
	 */
	public static String getSubmodelElementsPath() {
		return Submodel.SUBMODELELEMENT;
	}
	
	/**
	 * Retrieves access path for given element
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementPath(String idShortPath) {
		return VABPathTools.concatenatePaths(MultiSubmodelElementProvider.ELEMENTS, idShortPath); 
	}
	
	/**
	 * Retrieves access path for invocation of element's operation
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementInvokePath(String idShortPath) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), Operation.INVOKE + OperationProvider.ASYNC);
	}
	
	/**
	 * Retrieves access path for element value
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementValuePath(String idShortPath) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), Property.VALUE);
	}
	
	/**
	 * Retrieves access path for Element operation's result by request id
	 * @param idShortPath
	 * @param requestId
	 * @return
	 */
	public static String getSubmodelElementResultValuePath(String idShortPath, String requestId) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), OperationProvider.INVOCATION_LIST, requestId);
	}
}
