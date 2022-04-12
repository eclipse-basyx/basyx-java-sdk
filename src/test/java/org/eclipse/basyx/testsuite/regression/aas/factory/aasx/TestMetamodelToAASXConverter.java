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
package org.eclipse.basyx.testsuite.regression.aas.factory.aasx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test for the AASXFactory
 * 
 * @author conradi
 *
 */
public class TestMetamodelToAASXConverter {

	private static final String XML_PATH = "aasx/xml/content.xml";
	private static final String ORIGIN_PATH = "aasx/aasx-origin";
	private static final String EXTERNAL_FILE_URL = "http://localhost:8080/image.png";
	private static final String INTERNAL_FILE_PATH_1 = "aasx/Document/docu.pdf";
	private static final String INTERNAL_FILE_PATH_2 = "/aasx/Document/docu2.pdf";

	private static final String FILE_ID_SHORT_1 = "file1";
	private static final String FILE_ID_SHORT_2 = "file2";
	private static final String FILE_ID_SHORT_3 = "file3";
	private static final String COLLECTION_ID_SHORT = "collection";

	private AssetAdministrationShell aas;
	private Submodel sm1;
	private Submodel sm2;

	private List<IAssetAdministrationShell> aasList = new ArrayList<>();
	private List<ISubmodel> submodelList = new ArrayList<>();
	private List<IAsset> assetList = new ArrayList<>();
	private List<IConceptDescription> conceptDescriptionList = new ArrayList<>();

	private List<InMemoryFile> fileList = new ArrayList<>();

	@Before
	public void setup() throws IOException {
		Asset asset = new Asset("asset-id", new ModelUrn("ASSET_IDENTIFICATION"), AssetKind.TYPE);
		aas = new AssetAdministrationShell("aas-id", new ModelUrn("AAS_IDENTIFICATION"), asset);

		sm1 = new Submodel("sm1", new ModelUrn("SM1_ID"));
		sm2 = new Submodel("sm2", new ModelUrn("SM2_ID"));

		File file1 = new File(EXTERNAL_FILE_URL, "image/png");
		file1.setIdShort(FILE_ID_SHORT_1);
		File file2 = new File(INTERNAL_FILE_PATH_1, "application/pdf");
		file2.setIdShort(FILE_ID_SHORT_2);
		File file3 = new File(INTERNAL_FILE_PATH_2, "application/pdf");
		file3.setIdShort(FILE_ID_SHORT_3);

		SubmodelElementCollection collection = new SubmodelElementCollection(COLLECTION_ID_SHORT);
		collection.addSubmodelElement(file2);

		sm1.addSubmodelElement(file1);
		sm1.addSubmodelElement(collection);
		sm2.addSubmodelElement(file3);

		aas.addSubmodel(sm1);
		aas.addSubmodel(sm2);

		aasList.add(aas);
		submodelList.add(sm1);
		submodelList.add(sm2);
		assetList.add(asset);

		byte[] content1 = { 5, 6, 7, 8, 9 };
		InMemoryFile file = new InMemoryFile(content1, "/aasx/Document/docu.pdf");
		fileList.add(file);

		byte[] content2 = { 10, 11, 12, 13, 14 };
		file = new InMemoryFile(content2, "aasx/Document/docu2.pdf");
		fileList.add(file);
	}

	@Test
	public void testBuildAASX() throws IOException, TransformerException, ParserConfigurationException,
			InvalidFormatException, SAXException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);
		validateAASX(out);
	}

	@Test
	public void testFilePathsAreCorrectlyChanged() throws IOException, TransformerException,
			ParserConfigurationException, InvalidFormatException, SAXException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);

		Set<AASBundle> aasBundle = deserializeAASX(out);
		assertFilepathsAreCorrect(aasBundle);
	}

	private void validateAASX(ByteArrayOutputStream byteStream) throws IOException {
		ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
		ZipEntry zipEntry = null;

		ArrayList<String> filePaths = new ArrayList<>();

		while ((zipEntry = in.getNextEntry()) != null) {
			if (isExpectedXMLPath(zipEntry)) {
				assertIsXML(in);
			}
			filePaths.add(zipEntry.getName());
		}

		assertTrue(filePaths.contains(XML_PATH));
		assertTrue(filePaths.contains(ORIGIN_PATH));
		assertExpectedFileElementsArePresent(filePaths);
	}

	private void assertExpectedFileElementsArePresent(List<String> filePaths) {
		fileList.stream().forEach(file -> assertFilePathsContainFile(filePaths, file));
		assertFilePathsDoNotContainExternalFileURL(filePaths);
	}

	private void assertFilePathsDoNotContainExternalFileURL(List<String> filePaths) {
		String strippedExternalFileURL = VABPathTools.stripSlashes(EXTERNAL_FILE_URL);
		assertFalse(filePaths.contains(strippedExternalFileURL));
	}

	private void assertFilePathsContainFile(List<String> filePaths, InMemoryFile file) {
		String strippedPath = VABPathTools.stripSlashes(file.getPath());
		assertTrue(filePaths.contains(strippedPath));
	}

	private boolean isExpectedXMLPath(ZipEntry zipEntry) {
		return zipEntry.getName().equals(XML_PATH);
	}

	private void assertIsXML(ZipInputStream in) throws IOException {
		byte[] buf = new byte[5];
		in.read(buf);
		assertEquals("<?xml", new String(buf));
	}

	private Set<AASBundle> deserializeAASX(ByteArrayOutputStream byteStream)
			throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
		InputStream in = new ByteArrayInputStream(byteStream.toByteArray());

		AASXToMetamodelConverter aasxDeserializer = new AASXToMetamodelConverter(in);
		return aasxDeserializer.retrieveAASBundles();
	}

	private void assertFilepathsAreCorrect(Set<AASBundle> aasBundles) {
		AASBundle aasBundle = extractedAASBundleFromAASBundleSet(aasBundles, aas.getIdentification());
		Set<ISubmodel> deserializedSubmodels = aasBundle.getSubmodels();

		ISubmodel deserializedSm1 = extractSubmodelFromSubmodelSet(deserializedSubmodels, sm1.getIdentification());
		ISubmodel deserializedSm2 = extractSubmodelFromSubmodelSet(deserializedSubmodels, sm2.getIdentification());

		ISubmodelElementCollection deserializedCollection = (ISubmodelElementCollection) deserializedSm1
				.getSubmodelElement(COLLECTION_ID_SHORT);

		IFile deserializedFile1 = (IFile) deserializedSm1.getSubmodelElement(FILE_ID_SHORT_1);
		IFile deserializedFile2 = (IFile) deserializedCollection.getSubmodelElement(FILE_ID_SHORT_2);
		IFile deserializedFile3 = (IFile) deserializedSm2.getSubmodelElement(FILE_ID_SHORT_3);

		assertEquals(EXTERNAL_FILE_URL, deserializedFile1.getValue());
		assertEquals(harmonizePrefixSlash(INTERNAL_FILE_PATH_1), deserializedFile2.getValue());
		assertEquals(harmonizePrefixSlash(INTERNAL_FILE_PATH_2), deserializedFile3.getValue());
	}

	private AASBundle extractedAASBundleFromAASBundleSet(Set<AASBundle> aasBundles, IIdentifier identifier) {
		return aasBundles.stream().filter(aasB -> aasB.getAAS().getIdentification().equals(identifier)).findAny().get();
	}

	private Object harmonizePrefixSlash(String path) {
		return path.startsWith("/") ? path : "/" + path;
	}

	private ISubmodel extractSubmodelFromSubmodelSet(Set<ISubmodel> submodelSet, IIdentifier identifier) {
		return submodelSet.stream().filter(sm -> sm.getIdentification().equals(identifier)).findAny().get();
	}

}
