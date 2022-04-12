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
package org.eclipse.basyx.testsuite.regression.vab.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.LinkedHashMap;
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
		Map<String, Object> b = new LinkedHashMap<>();
		b.put("c", 12);
		b.put("d", 13);

		Map<String, Object> a = new LinkedHashMap<>();
		a.put("b", b);
		
		Map<String, Object> root = new LinkedHashMap<>();
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

		Map<String, Object> map = new LinkedHashMap<>();
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
