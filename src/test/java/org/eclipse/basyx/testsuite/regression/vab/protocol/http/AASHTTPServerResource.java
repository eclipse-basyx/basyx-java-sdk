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
package org.eclipse.basyx.testsuite.regression.vab.protocol.http;

import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.junit.rules.ExternalResource;

/**
 * This class initializes Tomcat server and required servlets for all HTTP test classes in this project.
 * 
 * @author espen
 *
 */
public class AASHTTPServerResource extends ExternalResource {
	private BaSyxHTTPServer server;
	private BaSyxContext context;

	/**
	 * Constructor taking the context of the requested server resource
	 */
	public AASHTTPServerResource(BaSyxContext context) {
		this.context = context;
    }

	/**
	 * Create a new AASHTTPServer before a test case runs
	 */
	@Override
    protected void before() {
    	server = new BaSyxHTTPServer(context);
		server.start();
    }

	/**
	 * Shutdown the created server after a test case
	 */
	@Override
    protected void after() {
		server.shutdown();
    }
}
