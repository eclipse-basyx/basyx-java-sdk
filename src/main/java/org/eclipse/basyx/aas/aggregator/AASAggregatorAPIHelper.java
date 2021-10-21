/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.aas.aggregator;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * API helper for AAS Aggregator
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
	 * @param aasId
	 * @return
	 */
	public static String getAASEntryPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getAggregatorPath(), VABPathTools.encodePathElement(aasId.getId()));
	}
	
	/**
	 * Retrieves access path for getting single AAS
	 * @param aasId
	 * @return
	 */
	public static String getAASAccessPath(IIdentifier aasId) {
		return VABPathTools.concatenatePaths(getAASEntryPath(aasId), AASAggregatorAPIHelper.AAS_SUFFIX);
	}
}
