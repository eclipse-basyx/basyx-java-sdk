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

import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.basyx.registry.restapi.RegistryModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A registry servlet based on an InMemory Registry. The servlet therefore provides an implementation
 * for the IAASRegistryService interface without a permanent storage capability.
 * 
 * @author espen
 */
public class RegistryServlet extends VABHTTPInterface<RegistryModelProvider> {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor with ModelProvider based on an InMemoryRegistry
	 */
	public RegistryServlet() {
		super(new RegistryModelProvider(new InMemoryRegistry()));

	}
}

