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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceAlreadyExistsException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Snippet to test create and delete functionality of an IModelProvider
 * 
 * @author kuhn, schnicke, espen
 *
 */
public class MapCreateDelete {

	public static void test(VABConnectionManager connManager) {
		// Connect to VAB element with ID "urn:fhg:es.iese:vab:1:1:simplevabelement"
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");

		testCreateElements(connVABElement);
		testDeleteElements(connVABElement);
	}

	@SuppressWarnings("unchecked")
	private static void testCreateElements(VABElementProxy connVABElement) {
		// Create property directly in root element
		connVABElement.createValue("inRoot", 1.2);
		Object toTest = connVABElement.getValue("inRoot");
		assertEquals(1.2, toTest);

		// Create element in Map (with new key contained in the path)
		connVABElement.createValue("/structure/map/inMap", "34");
		toTest = connVABElement.getValue("/structure/map/inMap");
		assertEquals("34", toTest);

		// Create map element
		LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
		newMap.put("entryA", 3);
		newMap.put("entryB", 4);
		connVABElement.createValue("mapInRoot", newMap);
		toTest = connVABElement.getValue("mapInRoot");
		assertTrue(toTest instanceof Map<?, ?>);
		assertEquals(2, ((Map<String, Object>) toTest).size());
		assertEquals(3, ((Map<String, Object>) toTest).get("entryA"));

		// Try to overwrite existing element (should throw Exception, already exists)
		try {
			connVABElement.createValue("inRoot", 0);
			fail();
		} catch (ResourceAlreadyExistsException e) {
			// If inRoot would have been a list 0 could be added here
			// => 1.2 has an "invalid" type for creating values in it
		}
		toTest = connVABElement.getValue("inRoot");
		assertEquals(1.2, toTest);

		// Check case-sensitivity
		connVABElement.createValue("inroot", 78);
		toTest = connVABElement.getValue("inRoot");
		assertEquals(1.2, toTest);
		toTest = connVABElement.getValue("inroot");
		assertEquals(78, toTest);

		// Non-existing parent element
		try {
			connVABElement.createValue("unkown/x", 5);
			fail();
		} catch (ResourceNotFoundException e) {
		}
		try {
			connVABElement.getValue("unknown/x");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Empty paths - at "" is a Map. Therefore create should throw an Exception
		try {
			connVABElement.createValue("", "");
			fail();
		} catch (ResourceAlreadyExistsException e) {
		}

		// Null path - should throw exception
		try {
			connVABElement.createValue(null, "");
			fail();
		} catch (MalformedRequestException e) {
		}
	}

	private static void testDeleteElements(VABElementProxy connVABElement) {
		// Delete at Root
		// - by object - should not work, root is a map
		try {
			connVABElement.deleteValue("inRoot", 1.2);
			fail();
		} catch (MalformedRequestException e) {
		}
		Object toTest = connVABElement.getValue("inRoot");
		assertEquals(1.2, toTest);

		// - by index
		connVABElement.deleteValue("inRoot");
		try {
			// "inRoot" should not exist anymore
			connVABElement.getValue("inRoot");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Check case-sensitivity
		toTest = connVABElement.getValue("inroot");
		assertEquals(78, toTest);
		connVABElement.deleteValue("inroot");
		try {
			// "inroot" should not exist anymore
			connVABElement.getValue("inroot");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Delete at Map
		// - by object - should not work in maps, because object refers to a contained
		// object, not the index
		try {
			connVABElement.deleteValue("/structure/map/", "inMap");
			fail();
		} catch (MalformedRequestException e) {
		}

		toTest = connVABElement.getValue("/structure/map/inMap");
		assertEquals("34", toTest);
		// - by index
		connVABElement.deleteValue("/structure/map/inMap");
		toTest = connVABElement.getValue("/structure/map");
		assertEquals(0, ((Map<?, ?>) toTest).size());

		// Delete remaining complete Map
		connVABElement.deleteValue("mapInRoot");
		try {
			// "mapInRoot" should not exist anymore
			connVABElement.getValue("mapInRoot");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Empty paths - should not delete anything and throw Exception
		try {
			connVABElement.deleteValue("", "");
			fail();
		} catch (MalformedRequestException e) {
			// Can not delete an object ("") from a map (root map)
			// It would be possible to delete "" from a "root list"
			// => invalid type
		}
		toTest = connVABElement.getValue("/primitives/integer");
		assertEquals(123, toTest);

		// Null path - should throw exception
		try {
			connVABElement.deleteValue(null, "");
			fail();
		} catch (MalformedRequestException e) {
		}
		try {
			connVABElement.deleteValue(null);
			fail();
		} catch (MalformedRequestException e) {
		}
	}
}
