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
import org.eclipse.basyx.registry.restapi.RegistryModelProvider;
import org.eclipse.basyx.testsuite.regression.registry.TestRegistryProviderSuite;

/**
 * Tests correct behaviour of the DirectoryModelProvider using an InMemory
 * database
 * 
 * @author schnicke
 *
 */
public class TestDirectoryModelProvider extends TestRegistryProviderSuite {
	@Override
	protected IRegistry getRegistryService() {
		RegistryModelProvider provider = new RegistryModelProvider();
		return new RegistryProxy(provider);
	}

}
