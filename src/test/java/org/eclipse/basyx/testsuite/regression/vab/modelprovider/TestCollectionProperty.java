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
package org.eclipse.basyx.testsuite.regression.vab.modelprovider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Tests a IModelProvider's capability to handle collections
 * 
 * @author schnicke, espen
 */
public class TestCollectionProperty {
	
	@SuppressWarnings("unchecked")
	public static void testRead(VABConnectionManager connManager) {
		// Connect to VAB element with ID "urn:fhg:es.iese:vab:1:1:simplevabelement"
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
	
		// Adding elements to tested list
		connVABElement.createValue("/structure/list/", 5);
		connVABElement.createValue("/structure/list/", 12);
	
		// Test reading whole lists
		Collection<Object> collection = (Collection<Object>) connVABElement.getValue("/structure/list/");
		Iterator<Object> iterator = collection.iterator();
		assertEquals(5, iterator.next());
		assertEquals(12, iterator.next());
		assertEquals(2, collection.size());

		// Test invalid list access - single list elements cannot be accessed directly
		try {
			connVABElement.getValue("/structure/list/0");
			fail();
		} catch (ResourceNotFoundException e) {}
	
		// Delete remaining entries
		connVABElement.deleteValue("/structure/list", 5);
		connVABElement.deleteValue("/structure/list", 12);
	}

	@SuppressWarnings("unchecked")
	public static void testUpdate(VABConnectionManager connManager) {
		// Connect to VAB element with ID "urn:fhg:es.iese:vab:1:1:simplevabelement"
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
	
		// Read original collection
		Collection<Object> original = (Collection<Object>) connVABElement.getValue("/structure/list/");
	
		// Replace complete value of the collection property
		Collection<Object> replacement = new ArrayList<>();
		replacement.add(100);
		replacement.add(200);
		replacement.add(300);
		connVABElement.setValue("/structure/list/", replacement);
	
		// Read values back
		Collection<Object> collection = (Collection<Object>) connVABElement.getValue("/structure/list/");
	
		// Check test case results
		assertEquals(3, collection.size());
		assertEquals(replacement, collection);

		// Test invalid list access - single list elements cannot be accessed directly
		try {
			connVABElement.setValue("/structure/list/0", 3);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	
		// Write original back
		connVABElement.setValue("/structure/list/", original);
	}

	public static void testCreateDelete(VABConnectionManager connManager) {
		// Connect to VAB element with ID "urn:fhg:es.iese:vab:1:1:simplevabelement"
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");
	
		// Create element in Set (no key provided)
		connVABElement.createValue("/structure/set/", true);
		Object toTest = connVABElement.getValue("/structure/set/");
		assertTrue(((Collection<?>) toTest).contains(true));
	
		// Delete at Set
		// - by index - should not work in sets, as they do not have an ordering
		try {
			connVABElement.deleteValue("/structure/set/0/");
			fail();
		} catch (ResourceNotFoundException e) {}
		toTest = connVABElement.getValue("/structure/set/");
		assertEquals(1, ((Collection<?>) toTest).size());
		// - by object
		connVABElement.deleteValue("/structure/set/", true);
		toTest = connVABElement.getValue("/structure/set/");
		assertEquals(0, ((Collection<?>) toTest).size());
	
		// Create elements in List (no key provided)
		connVABElement.createValue("/structure/list/", 56);
		toTest = connVABElement.getValue("/structure/list/");
		assertTrue(((List<?>) toTest).contains(56));
	
		// Delete at List
		// by object
		connVABElement.deleteValue("/structure/list/", 56);
		toTest = connVABElement.getValue("/structure/list/");
		assertEquals(0, ((List<?>) toTest).size());
	
		// Create a list element
		connVABElement.createValue("listInRoot", Arrays.asList(1, 1, 2, 3, 5));
		// Test whole list
		toTest = connVABElement.getValue("listInRoot");
		assertTrue(toTest instanceof List);
		assertEquals(5, ((List<?>) toTest).size());
		assertEquals(2, ((List<?>) toTest).get(2));
	
		// Delete whole list
		connVABElement.deleteValue("listInRoot");
		try {
			connVABElement.getValue("listInRoot");
			fail();
		} catch (ResourceNotFoundException e) {}
	
		// Delete at List
		// - referring to new list: [10, 20, 40, 80]
		connVABElement.createValue("/structure/list/", 10);
		connVABElement.createValue("/structure/list/", 20);
		connVABElement.createValue("/structure/list/", 40);
		connVABElement.createValue("/structure/list/", 80);
		// - by index - is not possible, as list access is only allowed using references
		// - in contrast to indices, references always point to the same object in the list
		try {
			connVABElement.deleteValue("/structure/list/3");
			fail();
		} catch (ResourceNotFoundException e) {}
	
		toTest = connVABElement.getValue("/structure/list/");
		assertEquals(4, ((List<?>) toTest).size());
	
		// Delete half of the elements
		connVABElement.deleteValue("/structure/list/", 10);
		connVABElement.deleteValue("/structure/list/", 40);
		toTest = connVABElement.getValue("/structure/list/");
		assertEquals(2, ((List<?>) toTest).size());

		// Delete remaining elements
		connVABElement.deleteValue("/structure/list/", 20);
		connVABElement.deleteValue("/structure/list/", 80);
		toTest = connVABElement.getValue("/structure/list/");
		assertEquals(0, ((List<?>) toTest).size());
	}
}
