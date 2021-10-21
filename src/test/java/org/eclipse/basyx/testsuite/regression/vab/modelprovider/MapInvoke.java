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
