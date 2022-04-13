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

package org.eclipse.basyx.submodel.restapi;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Helper class for Submodel API
 * 
 * @author haque
 */
public class SubmodelAPIHelper {

	/**
	 * Retrieves base access path for Submodel API
	 * 
	 * @return
	 */
	public static String getSubmodelPath() {
		return "";
	}

	/**
	 * Retrieves base access path for submodel element
	 * 
	 * @return
	 */
	public static String getSubmodelElementsPath() {
		return Submodel.SUBMODELELEMENT;
	}

	/**
	 * Retrieves access path for given element
	 * 
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementPath(String idShortPath) {
		return VABPathTools.concatenatePaths(MultiSubmodelElementProvider.ELEMENTS, idShortPath);
	}

	/**
	 * Retrieves access path for async invocation of element's operation
	 * 
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementAsyncInvokePath(String idShortPath) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), Operation.INVOKE + OperationProvider.ASYNC);
	}

	/**
	 * Retrieves access path for synchroenous invocation of element's operation
	 * 
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementSyncInvokePath(String idShortPath) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), Operation.INVOKE);
	}

	/**
	 * Retrieves access path for element value
	 * 
	 * @param idShortPath
	 * @return
	 */
	public static String getSubmodelElementValuePath(String idShortPath) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), Property.VALUE);
	}

	/**
	 * Retrieves access path for Element operation's result by request id
	 * 
	 * @param idShortPath
	 * @param requestId
	 * @return
	 */
	public static String getSubmodelElementResultValuePath(String idShortPath, String requestId) {
		return VABPathTools.concatenatePaths(getSubmodelElementPath(idShortPath), OperationProvider.INVOCATION_LIST, requestId);
	}
}
