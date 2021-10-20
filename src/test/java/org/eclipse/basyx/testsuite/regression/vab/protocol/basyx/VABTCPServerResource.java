/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.protocol.basyx;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.basyx.server.BaSyxTCPServer;
import org.junit.rules.ExternalResource;

/**
 * This class initializes a TCP Server and adds a provider to it. Note that this server can only provide one
 * model per port and new servers have to be started for each model.
 *
 * @author pschorn, espen
 *
 */
public class VABTCPServerResource extends ExternalResource {
	private IModelProvider provider;
	private BaSyxTCPServer<IModelProvider> server;

	/**
	 * Constructor taking the provider of the requested server resource
	 */
	public VABTCPServerResource(IModelProvider provider) {
		this.provider = provider;
	}

	@Override
	protected void before() {
		server = new BaSyxTCPServer<IModelProvider>(provider);
		server.start();
	}

	@Override
	protected void after() {
		server.stop();
	}
}
