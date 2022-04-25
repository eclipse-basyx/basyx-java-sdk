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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.function.Function;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Snippet to test get functionality of a IModelProvider
 * 
 * @author kuhn, schnicke, espen
 *
 */
public class MapRead {

	@SuppressWarnings("unchecked")
	public static void test(VABConnectionManager connManager) {
		// Connect to VAB element with ID "urn:fhg:es.iese:vab:1:1:simplevabelement"
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");

		// Test path access
		Object slashA = connVABElement.getValue("/primitives/integer");
		Object slashB = connVABElement.getValue("primitives/integer/");
		Object slashC = connVABElement.getValue("/primitives/integer/");
		Object slashD = connVABElement.getValue("/primitives/integer/");
		assertEquals(slashA, 123);
		assertEquals(slashB, 123);
		assertEquals(slashC, 123);
		assertEquals(slashD, 123);

		// Test reading different data types
		Object map = connVABElement.getValue("primitives");
		Object doubleValue = connVABElement.getValue("primitives/double");
		Object string = connVABElement.getValue("primitives/string");
		assertEquals(3, ((Map<?, ?>) map).size());
		assertEquals(3.14d, doubleValue);
		assertEquals("TestValue", string);

		// Test case sensitivity
		Object caseSensitiveA = connVABElement.getValue("special/casesensitivity");
		Object caseSensitiveB = connVABElement.getValue("special/caseSensitivity");
		assertEquals(true, caseSensitiveA);
		assertEquals(false, caseSensitiveB);

		// Test reading null value
		Object nullValue = connVABElement.getValue("special/null");
		assertNull(nullValue);

		// Test reading serializable functions
		Object serializableFunction = connVABElement.getValue("operations/serializable");
		Function<Object[], Object> testFunction = (Function<Object[], Object>) serializableFunction;
		assertEquals(3, testFunction.apply(new Object[] { 1, 2 }));

		// Non-existing parent element
		try {
			connVABElement.getValue("unknown/x");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Non-existing target element
		try {
			connVABElement.getValue("primitives/unkown");
			fail();
		} catch (ResourceNotFoundException e) {
		}
		try {
			connVABElement.getValue("unkown");
			fail();
		} catch (ResourceNotFoundException e) {
		}

		// Nested access
		assertEquals(100, connVABElement.getValue("special/nested/nested/value"));

		// Empty path
		Object rootValueA = connVABElement.getValue("");
		Object rootValueB = connVABElement.getValue("/");
		assertEquals(4, ((Map<?, ?>) rootValueA).size());
		assertEquals(4, ((Map<?, ?>) rootValueB).size());

		// Null path - should throw exception
		try {
			connVABElement.getValue(null);
			fail();
		} catch (MalformedRequestException e) {
		}
	}
}
