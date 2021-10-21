/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
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
	public AASHTTPServerResource res = new AASHTTPServerResource(
			new BaSyxContext("/" + CONTEXT_PATH, "", SERVER, PORT)
					.addServletMapping("/*", new VABHTTPInterface<IModelProvider>(provider)));
	
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