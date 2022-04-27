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

import static org.junit.Assert.assertEquals;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedFile;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedFile can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedFile {

	ConnectedFile connectedFile;
	File file;

	@Before
	public void build() {
		file = new File();
		file.setIdShort("testIdShort");
		file.setValue("FILE_VALUE");
		file.setMimeType("mimeType");

		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(file);

		connectedFile = new ConnectedFile(elementProxy);
	}

	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		assertEquals(file.getValue(), connectedFile.getValue());
	}

	/**
	 * Tests if getMimeType() returns the correct value
	 */
	@Test
	public void testGetMimeType() {
		assertEquals(file.getMimeType(), connectedFile.getMimeType());
	}

	@Test
	public void testSetValue() {
		String value = connectedFile.getValue();
		value += "TEST";
		connectedFile.setValue(value);
		assertEquals(value, connectedFile.getValue());
	}

	@Test
	public void setValueUpdatesValueCorrectly() {
		triggerCachingOfSubmodelElement();

		String expected = "Test File Value";

		connectedFile.setValue(expected);

		assertEquals(expected, connectedFile.getValue());
	}

	private void triggerCachingOfSubmodelElement() {
		connectedFile.getElem();
	}

}
