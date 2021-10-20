/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
