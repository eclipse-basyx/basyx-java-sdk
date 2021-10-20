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
import static org.junit.Assert.fail;

import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Result;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceAlreadyExistsException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Snippet to test the exception handling of an IModelProvider
 * 
 * @author zhangzai
 *
 */
public class Exceptions {
	/**
	 * Tests for handling an exception and its code
	 */
	public static void testHandlingException(VABConnectionManager connManager) {
		VABElementProxy connVABElement = connManager.connectToVABElement("urn:fhg:es.iese:vab:1:1:simplevabelement");

		// Empty paths - at "" is a Map. Therefore create should throw an Exception
		try {
			connVABElement.createValue("", "");
			fail();
		} catch (ResourceAlreadyExistsException e) {
			Result result = new Result(e);
			Message msg = result.getMessages().get(0);
			assertEquals("422", msg.getCode());
		}

		// Non-existing parent element
		try {
			connVABElement.getValue("unknown/x");
			fail();
		} catch (ResourceNotFoundException e) {
			Result result = new Result(e);
			Message msg = result.getMessages().get(0);
			assertEquals("404", msg.getCode());

		}

		// Null path - should throw exception
		try {
			connVABElement.createValue(null, "");
			fail();
		} catch (MalformedRequestException e) {
			Result result = new Result(e);
			Message msg = result.getMessages().get(0);
			assertEquals("400", msg.getCode());
		}

		// Invoke unsupported functional interface
		try {
			connVABElement.invokeOperation("operations/supplier/invoke");
			fail();
		} catch (MalformedRequestException e) {
			// this is for FileSystemProvider that does not support invoke
			Result result = new Result(e);
			Message msg = result.getMessages().get(0);
			assertEquals("400", msg.getCode());
		} catch (ProviderException e) {
			Result result = new Result(e);
			Message msg = result.getMessages().get(0);
			assertEquals("500", msg.getCode());
		}

	}
}
