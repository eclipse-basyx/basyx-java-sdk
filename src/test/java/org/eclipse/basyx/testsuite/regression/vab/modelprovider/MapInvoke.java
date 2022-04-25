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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Snippet to test invoke functionality of a IModelProvider
 * 
 * @author kuhn, schnicke, espen
 *
 */
public class MapInvoke {

	public static void test(VABConnectionManager connManager) {
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");

		invokeComplexObjectReturningFunction(connVABElement);

		invokeSupportedFunctionalInterfaces(connVABElement);

		invokeNonexistantPath(connVABElement);

		invokeInvalidPath(connVABElement);

		invokeExceptionFunction(connVABElement);

		invokeNullPointerExceptionFunction(connVABElement);

		invokeEmptyPathException(connVABElement);

		invokeNullPathException(connVABElement);
	}

	private static void invokeComplexObjectReturningFunction(VABElementProxy connVABElement) throws ProviderException {
		Object complex = connVABElement.invokeOperation("operations/complex/", 12, 34);
		assertEquals(46, complex);
	}

	private static void invokeSupportedFunctionalInterfaces(VABElementProxy connVABElement) {
		boolean result = (boolean) connVABElement.invokeOperation("operations/supplier/" + Operation.INVOKE);
		assertTrue(result);

		Object[] toConsume = { 10 };
		connVABElement.invokeOperation("operations/consumer/" + Operation.INVOKE, toConsume);
		Object[] consumed = (Object[]) SimpleVABElement.getAndResetConsumed();
		assertArrayEquals(toConsume, consumed);

		connVABElement.invokeOperation("operations/runnable/" + Operation.INVOKE);
		assertTrue(SimpleVABElement.getAndResetRunnableRan());
	}

	private static void invokeNonexistantPath(VABElementProxy connVABElement) throws ProviderException {
		try {
			connVABElement.invokeOperation("operations/unknown/" + Operation.INVOKE);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	private static void invokeInvalidPath(VABElementProxy connVABElement) {
		try {
			connVABElement.invokeOperation("operations/invalid/" + Operation.INVOKE);
			fail();
		} catch (ProviderException e) {
		}
	}

	private static void invokeExceptionFunction(VABElementProxy connVABElement) {
		try {
			connVABElement.invokeOperation("operations/providerException/" + Operation.INVOKE);
			fail();
		} catch (ProviderException e) {
			// exception type not implemented, yet
			// assertEquals(e.getType(), "testExceptionType");
		}
	}

	private static void invokeNullPointerExceptionFunction(VABElementProxy connVABElement) {
		try {
			connVABElement.invokeOperation("operations/nullException/" + Operation.INVOKE);
			fail();
		} catch (ProviderException e) {
			// exception type not implemented, yet
			// assertEquals(e.getType(), "java.lang.NullPointerException");
		}
	}

	private static void invokeEmptyPathException(VABElementProxy connVABElement) {
		try {
			connVABElement.invokeOperation("", "");
			fail();
		} catch (ProviderException e) {
		}
	}

	private static void invokeNullPathException(VABElementProxy connVABElement) throws ProviderException {
		try {
			connVABElement.invokeOperation(null, "");
			fail();
		} catch (MalformedRequestException e) {
		}
	}

}
