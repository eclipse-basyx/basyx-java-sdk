/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api;

import java.io.InputStream;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;

/**
 * Interface for the Asset Administration Shell Aggregator API <br>
 * with AASX upload support
 * 
 * @author haque
 *
 */
public interface IAASAggregatorAASXUpload extends IAASAggregator {
	/**
	 * Uploads an AASX in the AAS Aggregator
	 * 
	 * @param aasxStream stream of the given AASX
	 */
	public void uploadAASX(InputStream aasxStream);
}
