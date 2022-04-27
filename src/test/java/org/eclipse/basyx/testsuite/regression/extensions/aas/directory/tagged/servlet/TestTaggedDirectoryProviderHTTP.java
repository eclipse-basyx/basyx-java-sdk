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
