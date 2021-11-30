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
