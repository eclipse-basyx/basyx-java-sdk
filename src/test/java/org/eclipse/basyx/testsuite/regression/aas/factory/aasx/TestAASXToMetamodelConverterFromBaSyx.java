/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.testsuite.regression.aas.factory.aasx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.factory.aasx.InMemoryFile;
import org.eclipse.basyx.aas.factory.aasx.MetamodelToAASXConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * J-Unit tests AASX-Files created by the BaSyx middleware itself.
 * 
 * @author zhangzai, conradi, fischer, jungjan
 *
 */
public class TestAASXToMetamodelConverterFromBaSyx {
	private static final String CREATED_AASX_FILE_PATH = "test.aasx";

	private static final String AAS_IDSHORT = "assIdShort";
	private static final String AAS_IDENTIFICATION = "assIdentification";
	private static final String ASSET_IDSHORT = "assetIdShort";
	private static final String ASSET_IDENTIFICATION = "assetIdentification";
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_IDENTIFICATION = "submodelIdentification";
	private static final String SUBMODEL_COLLECTION_IDSHORT = "submodelCollectionIdShort";

	private static final String BOOLEAN_PROPERTY_IDSHORT = "bool";
	private static final String INTEGER_PROPERTY_IDSHORT = "int";
	private static final String DOUBLE_PROPERTY_IDSHORT = "decimal";
	private static final String STRING_PROPERTY_IDSHORT = "string";
	private static final Boolean EXPECTED_BOOLEAN_VALUE = true;
	private static final int EXPECTED_INTEGER_VALUE = 42;
	private static final double EXPECTED_DOUBLE_VALUE = 3.14159265359;
	private static final String EXPECTED_STRING_VALUE = "test";

	private static final String IMAGE_PATH = "/icon.png";
	private static final String IMAGE_MIMETYPE = "image/png";
	private static final String IMAGE_IDSHORT = "image";
	private static final String PDF_PATH = "/aasx/Document/docu.pdf";
	private static final String PDF_MIMETYPE = "application/pdf";
	private static final String PDF_IDSHORT = "pdf";
	private static final String TARGET_PATH = "target/files"; // gets set by BaSyx
	private static final String[] EXPECTED_UNZIPPED_FILES = { TARGET_PATH + PDF_PATH, TARGET_PATH + IMAGE_PATH };

	private static final String REL_PATH = "_rels/.rels";
	private static final String ORIGIN_REL_PATH = "aasx/_rels/aasx-origin.rels";

	private static final String ID_REGEX_START = "Id=.*";
	// only letters or underscore as start of id allowed
	// https://www.w3.org/TR/1999/REC-xml-names-19990114/#ns-qualnames
	private static final String ID_REGEX_FULL = "Id=\"([A-z]|_).*";

	private static final String TARGET_PATH_REGEX_START = "Target=.*";
	private static final String TARGET_PATH_REGEX_FULL = "Target=\"/.*";
	private static final String DOCPROPS_PATH_REGEX = "Target=\"docProps.*";

	private int bundleSize;
	private int submodelSize;
	private int submodelElementsSize;

	private AASXToMetamodelConverter packageManager;

	@Before
	public void setup() throws IOException, TransformerException, ParserConfigurationException {
		createAASXFile(CREATED_AASX_FILE_PATH);

		packageManager = new AASXToMetamodelConverter(CREATED_AASX_FILE_PATH);
	}

	/**
	 * Tests the AAS and its submodels of the parsed AASX file
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	@Test
	public void testLoadGeneratedAASX() throws InvalidFormatException, IOException, ParserConfigurationException, SAXException {
		Set<AASBundle> bundles = packageManager.retrieveAASBundles();

		assertEquals(bundleSize, bundles.size());
		AASBundle bundle = bundles.stream().findFirst().get();

		IAssetAdministrationShell parsedAAS = bundle.getAAS();
		assertEquals(AAS_IDSHORT, parsedAAS.getIdShort());
		assertEquals(AAS_IDENTIFICATION, parsedAAS.getIdentification().getId());

		assertEquals(submodelSize, bundle.getSubmodels().size());

		ISubmodel parsedSubmodel = bundle.getSubmodels().stream().findFirst().get();
		assertEquals(SUBMODEL_IDSHORT, parsedSubmodel.getIdShort());
		assertEquals(SUBMODEL_IDENTIFICATION, parsedSubmodel.getIdentification().getId());
		assertEquals(submodelElementsSize, parsedSubmodel.getSubmodelElements().size());
	}

	/**
	 * Check if the values and value types of Property submodelElements a parsed
	 * *.aasx file are returned correctly
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	@Test
	public void submodelElementPropertyValues() throws InvalidFormatException, IOException, ParserConfigurationException, SAXException {
		Set<AASBundle> bundles = packageManager.retrieveAASBundles();
		AASBundle bundle = bundles.stream().findFirst().get();

		ISubmodel parsedSubmodel = bundle.getSubmodels().stream().findFirst().get();
		Map<String, ISubmodelElement> submodelElements = parsedSubmodel.getSubmodelElements();

		ISubmodelElement boolProperty = submodelElements.get(BOOLEAN_PROPERTY_IDSHORT);
		ISubmodelElement intProperty = submodelElements.get(INTEGER_PROPERTY_IDSHORT);
		ISubmodelElement doubleProperty = submodelElements.get(DOUBLE_PROPERTY_IDSHORT);
		ISubmodelElement stringProperty = submodelElements.get(STRING_PROPERTY_IDSHORT);

		assertEquals(EXPECTED_BOOLEAN_VALUE, boolProperty.getValue());
		assertEquals(EXPECTED_INTEGER_VALUE, intProperty.getValue());
		assertEquals(EXPECTED_DOUBLE_VALUE, doubleProperty.getValue());
		assertEquals(EXPECTED_STRING_VALUE, stringProperty.getValue());
	}

	/**
	 * Tests the connected files of the parsed AASX file.
	 * 
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws URISyntaxException
	 */
	@Test
	public void testFilesOfGeneratedAASX() throws InvalidFormatException, IOException, ParserConfigurationException, SAXException, URISyntaxException {
		// Unzip files from the .aasx
		packageManager.unzipRelatedFiles();

		// Check if all expected files are present
		for (String path : EXPECTED_UNZIPPED_FILES) {
			assertTrue(new java.io.File(path).exists());
		}
	}

	/**
	 * Tests if the ids start with a letter or underscore and if the paths are
	 * absolute.
	 * 
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	@Test
	public void testRelationshipsOfGeneratedAASX() throws IOException {
		ZipInputStream in = unzipFromPath(CREATED_AASX_FILE_PATH);

		String relString = getFileContentFromZipInputStream(REL_PATH, in);
		String originRelString = getFileContentFromZipInputStream(ORIGIN_REL_PATH, in);

		List<String> relStringIds = findIdCandidates(relString);
		List<String> originRelStringIds = findIdCandidates(originRelString);

		assertIdsBeginWithLetterOrUnderscore(relStringIds);
		assertIdsBeginWithLetterOrUnderscore(originRelStringIds);

		// this step is necessary for compatibility reasons with AASXPackageExplorer
		List<String> relStringPaths = findPathCandidates(relString);
		List<String> originRelStringPaths = findPathCandidates(originRelString);

		assertPathsAreAbsolute(relStringPaths);
		assertPathsAreAbsolute(originRelStringPaths);
	}

	/**
	 * Check if elements of the given path list adhere to the target path regEx.
	 * This step is necessary for compatibility reasons with AASXPackageExplorer.
	 * 
	 * @param pathStringList
	 */
	private void assertPathsAreAbsolute(List<String> pathStringList) {
		assertStringListRegExCheck(pathStringList, TARGET_PATH_REGEX_FULL);
	}

	/**
	 * Split the given string at every whitespace and returns all elements that
	 * start with "Target=". This step is necessary for compatibility reasons with
	 * AASXPackageExplorer.
	 * 
	 * @param stringToCheck
	 * @return List<String>
	 */
	private List<String> findPathCandidates(String stringToCheck) {
		List<String> potentialPaths = findRegExParts(stringToCheck, TARGET_PATH_REGEX_START);

		// remove docProps path, because docProps is not relevant for
		// AASXPackageExplorer
		return removeDocPropsPath(potentialPaths);
	}

	/**
	 * Remove every target path that matches with the docProps path, because
	 * docProps is not relevant for AASXPackageExplorer.
	 * 
	 * @param givenPathList
	 * @return List<String>
	 */
	private List<String> removeDocPropsPath(List<String> givenPathList) {
		for (int i = 0; i < givenPathList.size(); i++) {
			if (givenPathList.get(i).matches(DOCPROPS_PATH_REGEX)) {
				givenPathList.remove(i);
			}
		}

		return givenPathList;
	}

	/**
	 * Check if elements of the given id list adhere to the id regEx.
	 * 
	 * @param idStringList
	 */
	private void assertIdsBeginWithLetterOrUnderscore(List<String> idStringList) {
		assertStringListRegExCheck(idStringList, ID_REGEX_FULL);
	}

	/**
	 * Split the given string at every whitespace and returns all elements that
	 * start with "Id="
	 * 
	 * @param stringToCheck
	 * @return List<String>
	 */
	private List<String> findIdCandidates(String stringToCheck) {
		return findRegExParts(stringToCheck, ID_REGEX_START);
	}

	/**
	 * Check if elements of the given list adhere to the given regEx.
	 * 
	 * @param stringToCheck
	 * @param regEx
	 */
	private void assertStringListRegExCheck(List<String> stringToCheck, String regEx) {
		for (String part : stringToCheck) {
			assertTrue(part.matches(regEx));
		}
	}

	/**
	 * Split the given string at every whitespace and returns a list of all
	 * remaining elements that adhere to the given regEx.
	 * 
	 * @param stringToCheck
	 * @param regEx
	 * @return List<String>
	 */
	private List<String> findRegExParts(String stringToCheck, String regEx) {
		String[] stringParts = stringToCheck.split(" ");
		List<String> regExParts = new ArrayList<String>();

		for (String part : stringParts) {
			if (part.matches(regEx)) {
				regExParts.add(part);
			}
		}

		return regExParts;
	}

	/**
	 * Get the content of a file with a given path from a ZipInputStream as String
	 * 
	 * @param filePath
	 * @param in
	 * @return String of the file contents
	 * @throws IOException
	 */
	private String getFileContentFromZipInputStream(String filePath, ZipInputStream in) throws IOException {
		ZipEntry zipEntry = null;
		String contentString = "";

		while ((zipEntry = in.getNextEntry()) != null) {
			if (zipEntry.getName().equals(filePath)) {
				byte[] buf = new byte[1];

				while (in.read(buf) > 0) {
					contentString += new String(buf);
				}
			}
		}
		return contentString;
	}

	/**
	 * Unzip a file from path using a ByteArrayInputStream
	 * 
	 * @param path
	 * @return ZipInputStream
	 * @throws IOException
	 */
	private ZipInputStream unzipFromPath(String path) throws IOException {
		return new ZipInputStream(new ByteArrayInputStream(FileUtils.readFileToByteArray(new java.io.File(path))));
	}

	/**
	 * Create an AASX file with default values.
	 * 
	 * @param filePath
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	private void createAASXFile(String filePath) throws IOException, TransformerException, ParserConfigurationException {
		List<IAssetAdministrationShell> aasList = new ArrayList<>();
		List<ISubmodel> submodelList = new ArrayList<>();
		List<IAsset> assetList = new ArrayList<>();
		List<IConceptDescription> conceptDescriptionList = new ArrayList<>();

		List<InMemoryFile> fileList = new ArrayList<>();

		Asset asset = new Asset(ASSET_IDSHORT, new ModelUrn(ASSET_IDENTIFICATION), AssetKind.INSTANCE);
		AssetAdministrationShell aas = new AssetAdministrationShell(AAS_IDSHORT, new ModelUrn(AAS_IDENTIFICATION), asset);
		aas.setAssetReference((Reference) asset.getReference());

		Submodel sm = new Submodel(SUBMODEL_IDSHORT, new ModelUrn(SUBMODEL_IDENTIFICATION));

		// Create SubmodelElements
		SubmodelElementCollection collection = new SubmodelElementCollection(SUBMODEL_COLLECTION_IDSHORT);
		collection.addSubmodelElement(createBaSyxFile(IMAGE_PATH, IMAGE_MIMETYPE, IMAGE_IDSHORT));

		sm.addSubmodelElement(collection);
		sm.addSubmodelElement(createBooleanProperty());
		sm.addSubmodelElement(createIntegerProperty());
		sm.addSubmodelElement(createDoubleProperty());
		sm.addSubmodelElement(createStringProperty());
		sm.addSubmodelElement(createBaSyxFile(PDF_PATH, PDF_MIMETYPE, PDF_IDSHORT));
		aas.addSubmodel(sm);

		// Add all AASs to the list that will be converted and set the size for the test
		// comparison
		aasList.add(aas);
		bundleSize = 1;

		// Add all Submodels to the list that will be converted and set the size for the
		// test comparison
		submodelList.add(sm);
		submodelSize = 1;

		assetList.add(asset);

		// Build InMemoryFiles, add them to the list that will be converted and set the
		// size for the test comparison
		fileList.add(createInMemoryFile(IMAGE_PATH));
		fileList.add(createInMemoryFile(PDF_PATH));
		submodelElementsSize = 6;

		try (FileOutputStream out = new FileOutputStream(filePath)) {
			MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);
		}
	}

	private ISubmodelElement createBooleanProperty() {
		Property booleanProperty = new Property();
		booleanProperty.setIdShort(BOOLEAN_PROPERTY_IDSHORT);
		booleanProperty.setCategory("CONSTANT");
		booleanProperty.setValueType(ValueType.Boolean);
		booleanProperty.setValue(EXPECTED_BOOLEAN_VALUE);
		return booleanProperty;
	}

	private ISubmodelElement createIntegerProperty() {
		Property intProperty = new Property();
		intProperty.setIdShort(INTEGER_PROPERTY_IDSHORT);
		intProperty.setCategory("CONSTANT");
		intProperty.setValueType(ValueType.Integer);
		intProperty.setValue(EXPECTED_INTEGER_VALUE);
		return intProperty;
	}

	private ISubmodelElement createDoubleProperty() {
		Property doubleProperty = new Property();
		doubleProperty.setIdShort(DOUBLE_PROPERTY_IDSHORT);
		doubleProperty.setCategory("CONSTANT");
		doubleProperty.setValueType(ValueType.Double);
		doubleProperty.setValue(EXPECTED_DOUBLE_VALUE);
		return doubleProperty;
	}

	private ISubmodelElement createStringProperty() {
		Property stringProperty = new Property();
		stringProperty.setIdShort(STRING_PROPERTY_IDSHORT);
		stringProperty.setCategory("CONSTANT");
		stringProperty.setValueType(ValueType.String);
		stringProperty.setValue(EXPECTED_STRING_VALUE);
		return stringProperty;
	}

	/**
	 * Delete created files
	 */
	@AfterClass
	public static void cleanUp() {
		for (String path : EXPECTED_UNZIPPED_FILES) {
			new java.io.File(path).delete();
		}
		new java.io.File(CREATED_AASX_FILE_PATH).delete();
	}

	/**
	 * Create a BaSyx File for given path, mimeType and idShort.
	 * 
	 * @param filePath
	 * @param fileMimeType
	 * @param fileIdShort
	 * @return BaSyx File
	 */
	private File createBaSyxFile(String filePath, String fileMimeType, String fileIdShort) {
		File file = new File(filePath, fileMimeType);
		file.setIdShort(fileIdShort);

		return file;
	}

	/**
	 * Create an inMemoryFile with default content for a given path.
	 * 
	 * @param filePath
	 * @return InMemoryFile
	 */
	private InMemoryFile createInMemoryFile(String filePath) {
		byte[] content = { 1, 2, 3, 4, 5 };
		InMemoryFile file = new InMemoryFile(content, filePath);

		return file;
	}
}
