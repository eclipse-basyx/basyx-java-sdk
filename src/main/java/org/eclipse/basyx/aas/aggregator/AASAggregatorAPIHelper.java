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

package org.eclipse.basyx.aas.aggregator;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * API helper for AAS Aggregator
 * 
 * @author haque
 *
 */
public class AASAggregatorAPIHelper {
	public static final String AAS_SUFFIX = "aas";

	public static String getAggregatorPath() {
		return AASAggregatorProvider.PREFIX;
	}

	/**
	 * Retrieves access path for creating, updating, deleting single AAS
	 * 
	 * @param aasId
	 * @return
	 */
	public static String getAASEntryPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getAggregatorPath(), VABPathTools.encodePathElement(aasId.getId()));
	}

	/**
	 * Retrieves access path for getting single AAS
	 * 
	 * @param aasId
	 * @return
	 */
	public static String getAASAccessPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getAASEntryPath(aasId), AASAggregatorAPIHelper.AAS_SUFFIX);
	}
}
