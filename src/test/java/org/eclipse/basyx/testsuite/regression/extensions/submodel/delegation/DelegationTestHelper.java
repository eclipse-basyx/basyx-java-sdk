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

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.util.Collections;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.extensions.submodel.delegation.PropertyDelegationManager;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;


/**
 * Helper class used in delegation test classes
 *
 * @author danish
 *
 */
public class DelegationTestHelper {
	public static final int EXPECTED_VALUE = 10;
	public static final int SERVER_PORT = 1080;
	
	public static final String SERVER_IP = "127.0.0.1";
	public static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT;
	public static final String ENDPOINT = "/valueEndpoint";
	
	public static Property createDelegatedProperty() {
		Property delegated = new Property("delegated", ValueType.Int32);
		delegated.setQualifiers(Collections.singleton(createQualifier(SERVER_URL, ENDPOINT)));
		return delegated;
	}
	
	public static Submodel createSubmodel() {
		Submodel submodel = new Submodel("testSubmodel", new CustomId("testSM"));
		
		return submodel;
	}
	
	public static IQualifier createQualifier(String serverUrl, String endpoint) {
		return PropertyDelegationManager.createDelegationQualifier(serverUrl + endpoint);
	}
	
	public static MockServerClient createExpectationForMockedGet() {
		MockServerClient mockServerClient = new MockServerClient(SERVER_IP, SERVER_PORT);
		
		mockServerClient.when(request().withMethod("GET").withPath(ENDPOINT))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "text/plain; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(Integer.toString(EXPECTED_VALUE)));
		
		return mockServerClient;
	}
}
