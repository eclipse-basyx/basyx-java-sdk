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
package org.eclipse.basyx.testsuite.regression.vab.directory.memory;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
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
		Map<String, String> map = new LinkedHashMap<String, String>();
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
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		return map;
	}
}
