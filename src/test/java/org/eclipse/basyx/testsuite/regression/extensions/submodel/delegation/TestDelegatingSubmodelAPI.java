/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.delegation;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.concurrent.TimeUnit;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatingSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.delegation.PropertyDelegationManager;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests Submodel containing delegated Property with DelegatingSubmodelAPI
 *
 * @author danish
 *
 */
public class TestDelegatingSubmodelAPI {
	
	private static ClientAndServer mockServer;
	
	@BeforeClass
	public static void init() {
		configureAndStartMockHttpServer();
		
		createExpectationForGet();
	}
	
	@Test
	public void delegatedPropertyValueIsTransformedWhileConstructingDelegatedSubmodelAPI() {
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		submodel.addSubmodelElement(delegated);
		
		DelegatingSubmodelAPI delegatingSubmodelAPI = createDelegatingSubmodelAPI(submodel);
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, delegatingSubmodelAPI.getSubmodel().getSubmodelElement(delegated.getIdShort()).getValue());
	}
	
	@Test
	public void addDelegatedPropertyToSubmodelWithDelegatingSubmodelAPI() {
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		Submodel submodel = DelegationTestHelper.createSubmodel();
		
		DelegatingSubmodelAPI delegatingSubmodelAPI = createDelegatingSubmodelAPI(submodel);
		
		delegatingSubmodelAPI.addSubmodelElement(delegated);
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, delegatingSubmodelAPI.getSubmodel().getSubmodelElement(delegated.getIdShort()).getValue());
	}
	
	@Test(expected = MalformedRequestException.class)
	public void exceptionIsThrownWhenUpdateValueRequestOnDelegatedProperty() {
		Submodel submodel = DelegationTestHelper.createSubmodel();
		
		SubmodelElement delegated = DelegationTestHelper.createDelegatedProperty();
		
		submodel.addSubmodelElement(delegated);
		
		DelegatingSubmodelAPI delegatingSubmodelAPI = createDelegatingSubmodelAPI(submodel);
		
		delegatingSubmodelAPI.updateSubmodelElement(delegated.getIdShort(), 13);
	}

	private DelegatingSubmodelAPI createDelegatingSubmodelAPI(Submodel submodel) {
		VABSubmodelAPIFactory vabSubmodelAPIFactory = new VABSubmodelAPIFactory();
		
		DelegatingSubmodelAPI delegatingSubmodelAPI = new DelegatingSubmodelAPI(vabSubmodelAPIFactory.create(submodel),new PropertyDelegationManager(new HTTPConnectorFactory()));
		return delegatingSubmodelAPI;
	}
	
	private static void configureAndStartMockHttpServer() {
		mockServer = startClientAndServer(DelegationTestHelper.SERVER_PORT);
	}
	
	private static void createExpectationForGet() {
		new MockServerClient(DelegationTestHelper.SERVER_IP, DelegationTestHelper.SERVER_PORT).when(request().withMethod("GET").withPath(DelegationTestHelper.ENDPOINT))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "text/plain; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(Integer.toString(DelegationTestHelper.EXPECTED_VALUE)).withDelay(TimeUnit.SECONDS, 1));
	}
	
	@AfterClass
    public static void stopServer() {
        mockServer.stop();
    } 
}
