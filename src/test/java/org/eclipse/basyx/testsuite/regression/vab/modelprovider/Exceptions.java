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
import static org.junit.Assert.fail;

import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Result;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
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
	}
}
