/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
