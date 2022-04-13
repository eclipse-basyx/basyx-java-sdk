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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.dataelement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedBlob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedBlob can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedBlob {
	public final String BLOB_CONTENT = "BLOB_VALUE";

	protected ConnectedBlob connectedBlob;
	protected Blob blob;

	@Before
	public void build() {
		blob = new Blob("testIdShort", "mimeType");

		byte[] value = BLOB_CONTENT.getBytes(StandardCharsets.UTF_8);

		blob.setByteArrayValue(value);

		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(blob);

		connectedBlob = new ConnectedBlob(elementProxy);
	}

	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		assertEquals(blob.getValue(), connectedBlob.getValue());
		byte[] byteArray = BLOB_CONTENT.getBytes(StandardCharsets.UTF_8);
		assertEquals(Base64.getEncoder().encodeToString(byteArray), blob.getValue());
	}

	/**
	 * Tests if getByteArrayValue() returns the correct value
	 */
	@Test
	public void testGetByteArrayValue() {
		assertArrayEquals(blob.getByteArrayValue(), connectedBlob.getByteArrayValue());
		assertArrayEquals(BLOB_CONTENT.getBytes(StandardCharsets.UTF_8), connectedBlob.getByteArrayValue());
	}

	/**
	 * Tests if getMimeType() returns the correct value
	 */
	@Test
	public void testGetMimeType() {
		assertEquals(blob.getMimeType(), connectedBlob.getMimeType());
	}

	/**
	 * Tests if getUTF8String() returns the correct value
	 */
	@Test
	public void testGetUTF8() {
		assertEquals(BLOB_CONTENT, connectedBlob.getUTF8String());
	}

	/**
	 * Tests if SetUTF8 sets the correct value
	 */
	@Test
	public void testSetUTF8() {
		connectedBlob.setUTF8String("NEW");
		assertEquals("NEW", connectedBlob.getUTF8String());
		assertArrayEquals("NEW".getBytes(StandardCharsets.UTF_8), connectedBlob.getByteArrayValue());
	}

	/**
	 * Tests if SetByteArrayValue sets the correct value
	 */
	@Test
	public void testSetByteArray() {
		byte[] newArrayValue = "NEW".getBytes(StandardCharsets.UTF_8);
		connectedBlob.setByteArrayValue(newArrayValue);
		assertEquals("NEW", connectedBlob.getUTF8String());
		assertArrayEquals("NEW".getBytes(StandardCharsets.UTF_8), connectedBlob.getByteArrayValue());
	}

	/**
	 * Tests if setValue sets the correct value
	 */
	@Test
	public void testSetValue() {
		byte[] newArrayValue = "NEW".getBytes(StandardCharsets.UTF_8);
		String newStringValue = Base64.getEncoder().encodeToString(newArrayValue);
		connectedBlob.setValue(newStringValue);
		assertEquals("NEW", connectedBlob.getUTF8String());
	}

	@Test
	public void setValueUpdatesValueCorrectly() {
		triggerCachingOfSubmodelElement();

		byte[] expected = BLOB_CONTENT.getBytes(StandardCharsets.US_ASCII);

		connectedBlob.setValue(Base64.getEncoder().encodeToString(expected));

		assertEquals(Base64.getEncoder().encodeToString(expected), connectedBlob.getValue());
	}

	private void triggerCachingOfSubmodelElement() {
		connectedBlob.getElem();
	}
}
