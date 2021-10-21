/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.extensions.aas.aggregator.aasxupload.proxy;

import java.io.InputStream;

import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.restapi.AASAggregatorAASXUploadProvider;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.http.helper.HTTPUploadHelper;

/**
 * Proxy AASAggregator with the support of uploading AASX
 * @author haque
 *
 */
public class AASAggregatorAASXUploadProxy extends AASAggregatorProxy implements IAASAggregatorAASXUpload {
	private String aasAggregatorUrl;
	
	public AASAggregatorAASXUploadProxy(String url) {
		super(url);
		this.aasAggregatorUrl = url;
	}

	@Override
    public void uploadAASX(InputStream aasxStream) {
		String uploadUrl = VABPathTools.append(aasAggregatorUrl, AASAggregatorAASXUploadProvider.AASX_PATH);
		try {
			HTTPUploadHelper.uploadHTTPPost(aasxStream, uploadUrl);	
		} catch (Exception e) {
            throw new MalformedRequestException("invalid request to aasx path without valid aasx input stream");
        }
    }
}
