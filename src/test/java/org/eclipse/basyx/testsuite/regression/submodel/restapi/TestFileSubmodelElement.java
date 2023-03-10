/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for testing the upload of a File to a File SubmodelElement
 * 
 * @author fried
 *
 */
public class TestFileSubmodelElement {

	private static final String FILE_SME_ID_SHORT = "fileSmeIdShort";
	private Submodel submodel;
	private ISubmodelAPI submodelAPI;

	@Before
	public void setUp() {
		this.submodel = new Submodel("submodelIdShort", new Identifier(IdentifierType.CUSTOM, "submodelIdentifier"));
		this.submodelAPI = new VABSubmodelAPIFactory().create(submodel);
		File fileSubmodelElement = new File("xml");
		fileSubmodelElement.setIdShort(FILE_SME_ID_SHORT);
		submodelAPI.addSubmodelElement(fileSubmodelElement);
	}

	@Test
	public void fileIsUploadedCorrectly() throws IOException {
		java.io.File tempFile = uploadDummyFile(FILE_SME_ID_SHORT);

		java.io.File file = submodelAPI.getSubmodelElementFile(FILE_SME_ID_SHORT);

		assertEquals("fileSmeIdShort.xml", file.getName());
		assertEquals(tempFile.length(), file.length());
	}

	@Test
	public void fileIsDeletedWhenSubmodelElementIsDeleted() throws FileNotFoundException {
		uploadDummyFile(FILE_SME_ID_SHORT);
		File fileSubmodelElement = getFileSubmodelElement(FILE_SME_ID_SHORT);
		String filePath = fileSubmodelElement.getValue();
		submodelAPI.deleteSubmodelElement(FILE_SME_ID_SHORT);
		java.io.File file = new java.io.File(filePath);
		assertFalse(file.exists());
	}

	@Test
	public void fileSubmodelElementValueIsAdapted() throws FileNotFoundException {
		uploadDummyFile(FILE_SME_ID_SHORT);

		File fileSubmodelElement = getFileSubmodelElement(FILE_SME_ID_SHORT);

		String filePath = fileSubmodelElement.getValue();
		String expected = getExpectedFileName(fileSubmodelElement);

		assertPathNotEmpty(filePath);
		assertEndsWithFilename(filePath, expected);
	}

	private void assertPathNotEmpty(String filePath) {
		assertFalse(filePath.isEmpty());
	}

	private void assertEndsWithFilename(String filePath, String expected) {
		assertTrue(filePath.endsWith(expected));
	}

	private String getExpectedFileName(File fileSubmodelElement) {
		return fileSubmodelElement.getIdShort() + "." + fileSubmodelElement.getMimeType();
	}

	@SuppressWarnings("unchecked")
	private File getFileSubmodelElement(String idShort) {
		Map<String, Object> submodelElementMap = (Map<String, Object>) submodelAPI.getSubmodelElement(idShort);
		File fileSubmodelElement = File.createAsFacade(submodelElementMap);
		return fileSubmodelElement;
	}

	private java.io.File uploadDummyFile(String idShortPath) throws FileNotFoundException {
		java.io.File tempFile = new java.io.File("src/test/resources/testfile.xml");
		submodelAPI.uploadSubmodelElementFile(idShortPath, new FileInputStream(tempFile));
		return tempFile;
	}
}
