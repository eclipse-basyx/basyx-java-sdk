/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.registry.memory;

import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.basyx.registry.memory.MapRegistryHandler;
import org.eclipse.basyx.testsuite.regression.registry.TestRegistryProviderSuite;

/**
 * Tests functionalities of {@link MapRegistryHandler} for their correctness
 * Includes test cases for exceptions
 * 
 * @author haque
 *
 */
public class TestMapRegistry extends TestRegistryProviderSuite {

	@Override
	protected IRegistry getRegistryService() {
		return new InMemoryRegistry();
	}
}
