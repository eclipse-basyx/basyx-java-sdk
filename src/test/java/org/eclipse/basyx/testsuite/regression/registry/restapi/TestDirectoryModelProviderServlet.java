/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.registry.restapi;

import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.proxy.RegistryProxy;
import org.eclipse.basyx.testsuite.regression.registry.TestRegistryProviderSuite;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.junit.Rule;

/**
 * Test
 * 
 * @author espen
 *
 */
public class TestDirectoryModelProviderServlet extends TestRegistryProviderSuite {
	/**
	 * Makes sure Tomcat Server is started after before each test case
	 * Initializes a new directory provider servlet
	 */
	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(
			new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir"))
					.addServletMapping("/Testsuite/directory/*", new RegistryServlet()));

	@Override
	protected IRegistry getRegistryService() {
		return new RegistryProxy("http://localhost:8080/basys.sdk/Testsuite/directory");
	}

}
