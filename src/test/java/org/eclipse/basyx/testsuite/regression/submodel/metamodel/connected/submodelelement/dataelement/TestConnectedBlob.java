/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
