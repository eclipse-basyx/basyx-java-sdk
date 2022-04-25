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

package org.eclipse.basyx.extensions.aas.aggregator.aasxupload.restapi;

import java.io.InputStream;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Provider class with support to upload an AASX file in the underlying
 * {@link IAASAggregator}
 * 
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
	 * 
	 * @param splitted
	 * @return true/false
	 */
	private boolean isAASXAccessPath(String[] splitted) {
		return splitted.length == 1 && splitted[0].equals(AASX_PATH);
	}

}
