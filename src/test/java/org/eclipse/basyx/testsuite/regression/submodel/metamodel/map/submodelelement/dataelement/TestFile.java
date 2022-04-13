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

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link File} for their correctness
 * 
 * @author haque
 *
 */
public class TestFile {
	private static final String VALUE = "testValue";
	private static final String MIME_TYPE = "testMime";
	private File file;

	@Before
	public void buildFile() {
		file = new File(VALUE, MIME_TYPE);
	}

	@Test
	public void testConstructor() {
		assertEquals(VALUE, file.getValue());
		assertEquals(MIME_TYPE, file.getMimeType());
		assertEquals(File.MODELTYPE, file.getModelType());
	}

	@Test
	public void testSetValue() {
		String newValue = "testNewValue";
		file.setValue(newValue);
		assertEquals(newValue, file.getValue());
	}

	@Test
	public void testSetMimeType() {
		String newMimeType = "testMimeType";
		file.setMimeType(newMimeType);
		assertEquals(newMimeType, file.getMimeType());
	}
}
