/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.servlet;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.proxy.TaggedDirectoryProxy;
import org.eclipse.basyx.extensions.aas.directory.tagged.restapi.TaggedDirectoryProvider;
import org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.TestTaggedDirectorySuite;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.Rule;

public class TestTaggedDirectoryProviderHTTP extends TestTaggedDirectorySuite {

	private class TaggedDirectoryProviderServlet extends VABHTTPInterface<TaggedDirectoryProvider> {
		private static final long serialVersionUID = -4328151193003468895L;

		/**
		 * Constructor with ModelProvider based on an InMemoryRegistry
		 */
		public TaggedDirectoryProviderServlet() {
			super(new TaggedDirectoryProvider());

		}
	}

	/**
	 * Makes sure Tomcat Server is started after before each test case Initializes a
	 * new directory provider servlet
	 */
	@Rule
	public AASHTTPServerResource res = new AASHTTPServerResource(new BaSyxContext("/basys.sdk", System.getProperty("java.io.tmpdir")).addServletMapping("/Testsuite/directory/*", new TaggedDirectoryProviderServlet()));

	@Override
	protected IAASRegistry getRegistryService() {
		return getDirectory();
	}

	@Override
	protected IAASTaggedDirectory getDirectory() {
		return new TaggedDirectoryProxy("http://localhost:8080/basys.sdk/Testsuite/directory");
	}

}
