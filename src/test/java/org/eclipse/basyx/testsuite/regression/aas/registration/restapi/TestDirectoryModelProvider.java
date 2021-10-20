/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.registration.restapi;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.testsuite.regression.aas.registration.TestRegistryProviderSuite;

/**
 * Tests correct behaviour of the DirectoryModelProvider using an InMemory
 * database
 * 
 * @author schnicke
 *
 */
public class TestDirectoryModelProvider extends TestRegistryProviderSuite {
	@Override
	protected IAASRegistry getRegistryService() {
		AASRegistryModelProvider provider = new AASRegistryModelProvider();
		return new AASRegistryProxy(provider);
	}

}
