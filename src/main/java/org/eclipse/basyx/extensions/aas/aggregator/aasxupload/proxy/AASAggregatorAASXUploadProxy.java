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
