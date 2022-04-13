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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a Blob stores its data correctly
 * 
 * @author espen
 *
 */
public class TestBlob {
	public final String BLOB_CONTENT = "BLOB_VALUE";

	protected Blob blob;
	protected String testString = "NEW!";
	protected byte[] testBytes = testString.getBytes(StandardCharsets.UTF_8);
	protected String testBase64 = Base64.getEncoder().encodeToString(testBytes);

	@Before
	public void build() {
		blob = new Blob("testIdShort", "mimeType");
	}

	/**
	 * Tests if getMimeType() returns the correct value
	 */
	@Test
	public void testGetMimeType() {
		assertEquals("mimeType", blob.getMimeType());
	}

	/**
	 * Tests if getValue() returns the correct value if it hasn't been set before
	 */
	@Test
	public void testGetEmptyValue() {
		assertNull(blob.getValue());
	}

	/**
	 * Tests if setValue sets the correct values
	 */
	@Test
	public void testSetValue() {
		blob.setValue(testBase64);
		assertEquals(testString, blob.getUTF8String());
		assertArrayEquals(testBytes, blob.getByteArrayValue());
		assertEquals(testBase64, blob.getValue());
	}

	/**
	 * Tests if setUTF8 sets the correct value
	 */
	@Test
	public void testSetUTF8() {
		blob.setUTF8String(testString);
		assertEquals(testString, blob.getUTF8String());
		assertArrayEquals(testBytes, blob.getByteArrayValue());
		assertEquals(testBase64, blob.getValue());
	}

	/**
	 * Tests if SetByteArrayValue() sets the correct value
	 */
	@Test
	public void testSetByteArray() {
		blob.setByteArrayValue(testBytes);
		assertEquals(testString, blob.getUTF8String());
		assertArrayEquals(testBytes, blob.getByteArrayValue());
		assertEquals(testBase64, blob.getValue());
	}
}
