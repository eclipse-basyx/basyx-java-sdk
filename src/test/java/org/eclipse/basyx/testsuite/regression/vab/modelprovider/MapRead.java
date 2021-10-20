/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		} catch (ResourceNotFoundException e) {}
		
		// Non-existing target element
		try {
			connVABElement.getValue("primitives/unkown");
			fail();
		} catch (ResourceNotFoundException e) {}
		try {
			connVABElement.getValue("unkown");
			fail();
		} catch (ResourceNotFoundException e) {}
		
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
		} catch (MalformedRequestException e) {}
	}
}
