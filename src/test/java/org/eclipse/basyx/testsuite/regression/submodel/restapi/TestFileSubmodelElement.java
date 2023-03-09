package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

public class TestFileSubmodelElement {

	private Submodel submodel;
	private ISubmodelAPI submodelAPI;

	@Before
	public void setUp() {
		this.submodel = new Submodel("submodelIdShort", new Identifier(IdentifierType.CUSTOM, "submodelIdentifier"));
		this.submodelAPI = new VABSubmodelAPIFactory().create(submodel);
		File fileSubmodelElement = new File("xml");
		fileSubmodelElement.setIdShort("fileSmeIdShort");
		submodelAPI.addSubmodelElement(fileSubmodelElement);
	}

	@Test
	public void fileIsUploadedCorrectly() throws IOException {
		java.io.File tempFile = uploadDummyFile("fileSmeIdShort");

		java.io.File file = (java.io.File) submodelAPI.getSubmodelElementFile("fileSmeIdShort");

		assertEquals("fileSmeIdShort.xml", file.getName());
		assertEquals(tempFile.length(), file.length());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void fileIsDeletedWhenSubmodelElementIsDeleted() throws FileNotFoundException {
		java.io.File tempFile = uploadDummyFile("fileSmeIdShort");
		Map<String, Object> submodelElementMap = (Map<String, Object>) submodelAPI.getSubmodelElement("fileSmeIdShort");
		File fileSubmodelElement = File.createAsFacade(submodelElementMap);
		String filePath = fileSubmodelElement.getValue();
		submodelAPI.deleteSubmodelElement("fileSmeIdShort");
		java.io.File file = new java.io.File(filePath);
		assertFalse(file.exists());
	}

	private java.io.File uploadDummyFile(String idShortPath) throws FileNotFoundException {
		java.io.File tempFile = new java.io.File("src/test/resources/testfile.xml");
		submodelAPI.uploadSubmodelElementFile(idShortPath, new FileInputStream(tempFile));
		return tempFile;
	}
}
