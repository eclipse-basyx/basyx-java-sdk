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

package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.aasxupload;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.AASAggregatorAASXUpload;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.proxy.AASAggregatorAASXUploadProxy;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.restapi.AASAggregatorAASXUploadProvider;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.TestAASAggregatorProxy;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for the {@link AASAggregatorAASXUploadProxy}
 * 
 * @author haque
 *
 */
public class TestAASAggregatorProxyWithAASXProvider extends TestAASAggregatorProxy {
	private static final String SERVER = "localhost";
	private static final int PORT = 4000;
	private static final String CONTEXT_PATH = "aggregator";
	private static final String API_URL = "http://" + SERVER + ":" + PORT + "/" + CONTEXT_PATH + "/shells";
	private AASAggregatorAASXUploadProvider provider = new AASAggregatorAASXUploadProvider(new AASAggregatorAASXUpload(new AASAggregator()));

	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(new BaSyxContext("/" + CONTEXT_PATH, "", SERVER, PORT).addServletMapping("/*", new VABHTTPInterface<IModelProvider>(provider)));

	@Override
	protected IAASAggregator getAggregator() {
		return new AASAggregatorAASXUploadProxy(API_URL);
	}

	@Test
	public void testClientUpload() throws ClientProtocolException, IOException {
		AASAggregatorAASXUploadProxy proxy = new AASAggregatorAASXUploadProxy(API_URL);
		proxy.uploadAASX(new FileInputStream(Paths.get(TestAASAggregatorAASXUploadSuite.AASX_PATH).toFile()));

		Collection<IAssetAdministrationShell> uploadedShells = proxy.getAASList();
		TestAASAggregatorAASXUploadSuite.checkAASX(uploadedShells);
	}
}