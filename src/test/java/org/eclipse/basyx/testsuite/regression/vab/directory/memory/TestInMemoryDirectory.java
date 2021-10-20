/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.directory.memory;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.testsuite.regression.vab.directory.proxy.TestDirectory;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;
import org.eclipse.basyx.vab.registry.memory.VABInMemoryRegistry;
import org.junit.Test;

/**
 * Tests the InMemory directory using the TestDirectory Suite
 * 
 * @author schnicke, haque
 *
 */
public class TestInMemoryDirectory extends TestDirectory {

	@Override
	protected IVABRegistryService getRegistry() {
		return new VABInMemoryRegistry();
	}

	@Test
	public void testConstructor() {
		IVABRegistryService registry = new VABInMemoryRegistry(getAddedValues());
		testElementsInMap(registry, getAddedValues());
	}

	@Test
	public void testAddMappingMultipleKey() {
		VABInMemoryRegistry registry = new VABInMemoryRegistry();
		String key3 = "key3";
		String value3 = "value3";
		String key4 = "key4";
		String value4 = "value4";
		Map<String, String> map = new HashMap<String, String>();
		map.put(key3, value3);
		map.put(key4, value4);
		registry.addMappings(map);
		testElementsInMap(registry, map);
	}

	private void testElementsInMap(IVABRegistryService registry, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			assertEquals(entry.getValue(), registry.lookup(entry.getKey()));
		}
	}

	private Map<String, String> getAddedValues() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		return map;
	}
}
