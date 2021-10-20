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

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link File} for their
 * correctness
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
