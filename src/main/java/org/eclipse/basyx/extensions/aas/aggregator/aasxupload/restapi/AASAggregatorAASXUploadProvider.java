/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.extensions.aas.aggregator.aasxupload.restapi;

import java.io.InputStream;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Provider class with support to upload an AASX file in the
 * underlying {@link IAASAggregator}
 * @author haque
 *
 */
public class AASAggregatorAASXUploadProvider extends AASAggregatorProvider {
	private IAASAggregatorAASXUpload uploadAggregator;
	public static final String AASX_PATH = "aasx";
	
	public AASAggregatorAASXUploadProvider(IAASAggregatorAASXUpload aggregator) {
		super(aggregator);
		this.uploadAggregator = aggregator;
	}
	
	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = stripPrefix(path);
		String[] splitted = VABPathTools.splitPath(path);
		
		if (isAASXAccessPath(splitted)) {
			try {
				this.uploadAggregator.uploadAASX((InputStream) newEntity);	
			} catch (Exception e) {
				throw new ProviderException("AASX upload failed");
			}
			
		} else {
			super.createValue(path, newEntity);	
		}
	}

	/**
	 * Checks if the path array is a valid AASX path
	 * @param splitted
	 * @return true/false
	 */
	private boolean isAASXAccessPath(String[] splitted) {
		return splitted.length == 1 && splitted[0].equals(AASX_PATH);
	}

}
