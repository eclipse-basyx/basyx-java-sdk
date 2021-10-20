/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;
import org.junit.Test;

/**
 * Tests the VABModelMap
 * 
 * @author schnicke
 *
 */
public class VABModelMapTest {

	/**
	 * Tests the behaviour of the getPath method
	 */
	@Test
	public void testGetPath() {
		// Build VABModelMap
		VABModelMap<Object> map = new VABModelMap<>();
		map.putPath("a/b/c", 12);
		map.putPath("a/b/d", 13);
		
		// Build expected output
		Map<String, Object> b = new HashMap<>();
		b.put("c", 12);
		b.put("d", 13);

		Map<String, Object> a = new HashMap<>();
		a.put("b", b);
		
		Map<String, Object> root = new HashMap<>();
		root.put("a", a);

		// Assert correct behaviour of getPath
		assertEquals(b, map.getPath("a/b"));
		assertEquals(a, map.getPath("a"));
		assertEquals(root, map.getPath(""));
	}

	@Test
	public void testEquals() {
		VABModelMap<Object> expected = new VABModelMap<>();
		expected.put("a", "b");
		expected.put("x", "y");

		Map<String, Object> map = new HashMap<>();
		map.put("a", "b");
		map.put("x", "y");

		assertEquals(expected, map);

		map.put("a", "c");
		assertNotEquals(expected, map);

		map.remove("a");
		assertNotEquals(expected, map);
	}

	@Test
	public void testEqualsFirstEntryNull() {
		VABModelMap<Object> expected = new VABModelMap<>();
		expected.put("a", null);
		expected.put("x", "b");

		VABModelMap<Object> map = new VABModelMap<>();
		map.put("a", null);
		map.put("x", "c");

		assertNotEquals(map, expected);
	}

	@Test
	public void testEqualsFirstEntrySame() {
		VABModelMap<Object> expected = new VABModelMap<>();
		expected.put("a", "1");
		expected.put("x", "b");

		VABModelMap<Object> map = new VABModelMap<>();
		map.put("a", "1");
		map.put("x", "c");

		assertNotEquals(map, expected);
	}

	@Test
	public void testEqualsMapEntrySame() {
		VABModelMap<Object> contained = new VABModelMap<>();
		contained.put("hello", "world");

		VABModelMap<Object> expected = new VABModelMap<>();
		expected.put("a", contained);
		expected.put("x", "b");

		VABModelMap<Object> map = new VABModelMap<>();
		map.put("a", contained);
		map.put("x", "c");

		assertNotEquals(map, expected);
	}

}
