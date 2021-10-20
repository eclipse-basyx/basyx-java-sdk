/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.directory.restapi;

import org.eclipse.basyx.testsuite.regression.vab.directory.proxy.TestDirectory;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;
import org.eclipse.basyx.vab.registry.proxy.VABRegistryProxy;
import org.eclipse.basyx.vab.registry.restapi.VABRegistryModelProvider;

/**
 * Tests the directory provider using the TestDirectory Suite
 * 
 * @author schnicke
 *
 */
public class TestDirectoryProvider extends TestDirectory {

	@Override
	protected IVABRegistryService getRegistry() {
		VABRegistryModelProvider provider = new VABRegistryModelProvider();
		return new VABRegistryProxy(provider);
	}

}
