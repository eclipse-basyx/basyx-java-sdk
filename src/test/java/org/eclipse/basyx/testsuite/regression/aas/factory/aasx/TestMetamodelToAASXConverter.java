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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.factory.aasx.InMemoryFile;
import org.eclipse.basyx.aas.factory.aasx.MetamodelToAASXConverter;
import org.eclipse.basyx.aas.factory.aasx.Thumbnail;
import org.eclipse.basyx.aas.factory.aasx.Thumbnail.ThumbnailExtension;
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
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Test for the AASXFactory
 * 
 * @author conradi, danish
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
	
	private static final String PROPERTY_ID_SHORT_IV = "input-variable";
	private static final String PROPERTY_ID_SHORT_OV = "output-variable";
	private static final String PROPERTY_ID_SHORT_IOV = "input-output-variable";
	private static final String OPERATION_ID_SHORT = "operation";

	private static final String THUMBNAIL_FILENAME = "Thumbnail.png";
	private static final byte[] THUMBNAIL = { 22, 23, 24, 25, 26 };

	private AssetAdministrationShell aas;
	private Submodel sm1;
	private Submodel sm2;
	private Submodel sm3;

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
		sm3 = new Submodel("sm3", new ModelUrn("SM3_ID"));

		File file1 = new File(EXTERNAL_FILE_URL, "image/png");
		file1.setIdShort(FILE_ID_SHORT_1);
		File file2 = new File(INTERNAL_FILE_PATH_1, "application/pdf");
		file2.setIdShort(FILE_ID_SHORT_2);
		File file3 = new File(INTERNAL_FILE_PATH_2, "application/pdf");
		file3.setIdShort(FILE_ID_SHORT_3);

		SubmodelElementCollection collection = new SubmodelElementCollection(COLLECTION_ID_SHORT);
		collection.addSubmodelElement(file2);

		Operation operation = new Operation(OPERATION_ID_SHORT);
		operation.setInputVariables(singletonList(createOperationVariable(PROPERTY_ID_SHORT_IV)));
		operation.setOutputVariables(singletonList(createOperationVariable(PROPERTY_ID_SHORT_OV)));
		operation.setInOutputVariables(singletonList(createOperationVariable(PROPERTY_ID_SHORT_IOV)));

		sm1.addSubmodelElement(file1);
		sm1.addSubmodelElement(collection);
		sm2.addSubmodelElement(file3);
		sm3.addSubmodelElement(operation);

		aas.addSubmodel(sm1);
		aas.addSubmodel(sm2);
		aas.addSubmodel(sm3);

		aasList.add(aas);
		submodelList.add(sm1);
		submodelList.add(sm2);
		submodelList.add(sm3);
		assetList.add(asset);

		byte[] content1 = { 5, 6, 7, 8, 9 };
		InMemoryFile file = new InMemoryFile(content1, "/aasx/Document/docu.pdf");
		fileList.add(file);

		byte[] content2 = { 10, 11, 12, 13, 14 };
		file = new InMemoryFile(content2, "aasx/Document/docu2.pdf");
		fileList.add(file);
	}

	@Test
	public void testBuildAASX() throws IOException, TransformerException, ParserConfigurationException, InvalidFormatException, SAXException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);
		
		assertAASXContainsExpectedElements(out);
	}
	
	@Test
	public void buildAASXWithThumbnail() throws IOException, TransformerException, ParserConfigurationException, InvalidFormatException, SAXException {
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Thumbnail thumbnail = new Thumbnail(ThumbnailExtension.PNG, new ByteArrayInputStream(THUMBNAIL));
		
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, thumbnail, out);
		
		assertAASXThumbnailIsPresent(out);
		
		assertAASXThumbnailContentIsSame(out);
	}

	@Test
	public void testFilePathsAreCorrectlyChanged() throws IOException, TransformerException, ParserConfigurationException, InvalidFormatException, SAXException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);

		Set<AASBundle> aasBundle = deserializeAASX(out);
		assertFilepathsAreCorrect(aasBundle);
	}
	
	@Test
	public void testOperationWithVariables() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);

		Set<AASBundle> aasBundles = deserializeAASX(out);
		assertOperationContainsExpectedVariables(aasBundles);
	}

	@Test
	public void testOperationWithoutVariables() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Operation operation = findOperation(submodelList);
		operation.setInputVariables(Collections.emptyList());
		operation.setOutputVariables(Collections.emptyList());
		operation.setInOutputVariables(Collections.emptyList());
		MetamodelToAASXConverter.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);

		Set<AASBundle> aasBundles = deserializeAASX(out);
		assertOperationContainsNoVariables(aasBundles);
	}

	private void assertOperationContainsNoVariables(Set<AASBundle> aasBundles) {
		AASBundle aasBundle = extractAASBundleFromAASBundleSet(aasBundles, aas.getIdentification());
		Operation operation = findOperation(aasBundle.getSubmodels());
		assertEquals(0L, countOperationVariables(operation.getInputVariables(), PROPERTY_ID_SHORT_IV));
		assertEquals(0L, countOperationVariables(operation.getOutputVariables(), PROPERTY_ID_SHORT_OV));
		assertEquals(0L, countOperationVariables(operation.getInOutputVariables(), PROPERTY_ID_SHORT_IOV));
	}

	private void assertOperationContainsExpectedVariables(Set<AASBundle> aasBundles) {
		AASBundle aasBundle = extractAASBundleFromAASBundleSet(aasBundles, aas.getIdentification());
		Operation operation = findOperation(aasBundle.getSubmodels());
		assertEquals(1L, countOperationVariables(operation.getInputVariables(), PROPERTY_ID_SHORT_IV));
		assertEquals(1L, countOperationVariables(operation.getOutputVariables(), PROPERTY_ID_SHORT_OV));
		assertEquals(1L, countOperationVariables(operation.getInOutputVariables(), PROPERTY_ID_SHORT_IOV));
	}

	private long countOperationVariables(Collection<? extends IOperationVariable> operationVariables, String propertyIdShort) {
		return operationVariables.stream().map(IOperationVariable::getValue).filter(sme -> sme.getIdShort().equals(propertyIdShort)).count();
	}

	private OperationVariable createOperationVariable(String propertyIdShort) {
		Property property = new Property(propertyIdShort, ValueType.String);
		return new OperationVariable(property);
	}

	private Operation findOperation(Collection<ISubmodel> submodelCollection) {
		return (Operation) submodelCollection.stream()
			.filter(sm -> "sm3".equals(sm.getIdShort()))
			.map(sm -> sm.getSubmodelElement(OPERATION_ID_SHORT))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private void assertAASXContainsExpectedElements(ByteArrayOutputStream out) throws IOException {
		List<String> filePaths = getFilePaths(out);
		
		assertTrue(filePaths.contains(XML_PATH));
		assertTrue(filePaths.contains(ORIGIN_PATH));
		
		assertExpectedFileElementsArePresent(filePaths);
	}

	private void assertAASXThumbnailIsPresent(ByteArrayOutputStream out) throws IOException {
		ArrayList<String> filePaths = getFilePaths(out);
		
		assertTrue(filePaths.contains(FilenameUtils.getName(THUMBNAIL_FILENAME)));
	}
	
	public void assertAASXThumbnailContentIsSame(ByteArrayOutputStream out) throws InvalidFormatException, IOException, ParserConfigurationException, SAXException {
		assertTrue(IOUtils.contentEquals(new ByteArrayInputStream(THUMBNAIL), getThumbnailStreamFromAASX(out)));
	}

	private ArrayList<String> getFilePaths(ByteArrayOutputStream byteStream) throws IOException {
		ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
		ZipEntry zipEntry = null;

		ArrayList<String> filePaths = new ArrayList<>();

		while ((zipEntry = in.getNextEntry()) != null) {
			if (isExpectedXMLPath(zipEntry)) {
				assertIsXML(in);
			}
			filePaths.add(zipEntry.getName());
		}
		
		return filePaths;
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

	private Set<AASBundle> deserializeAASX(ByteArrayOutputStream byteStream) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
		AASXToMetamodelConverter aasxDeserializer = getDeserializedAASX(byteStream);
		
		return aasxDeserializer.retrieveAASBundles();
	}
	
	private InputStream getThumbnailStreamFromAASX(ByteArrayOutputStream byteStream) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
		AASXToMetamodelConverter aasxDeserializer = getDeserializedAASX(byteStream);
		
		return aasxDeserializer.retrieveThumbnail();
	}

	private AASXToMetamodelConverter getDeserializedAASX(ByteArrayOutputStream byteStream) {
		InputStream in = new ByteArrayInputStream(byteStream.toByteArray());

		AASXToMetamodelConverter aasxDeserializer = new AASXToMetamodelConverter(in);
		return aasxDeserializer;
	}

	private void assertFilepathsAreCorrect(Set<AASBundle> aasBundles) {
		AASBundle aasBundle = extractAASBundleFromAASBundleSet(aasBundles, aas.getIdentification());
		Set<ISubmodel> deserializedSubmodels = aasBundle.getSubmodels();

		ISubmodel deserializedSm1 = extractSubmodelFromSubmodelSet(deserializedSubmodels, sm1.getIdentification());
		ISubmodel deserializedSm2 = extractSubmodelFromSubmodelSet(deserializedSubmodels, sm2.getIdentification());

		ISubmodelElementCollection deserializedCollection = (ISubmodelElementCollection) deserializedSm1.getSubmodelElement(COLLECTION_ID_SHORT);

		IFile deserializedFile1 = (IFile) deserializedSm1.getSubmodelElement(FILE_ID_SHORT_1);
		IFile deserializedFile2 = (IFile) deserializedCollection.getSubmodelElement(FILE_ID_SHORT_2);
		IFile deserializedFile3 = (IFile) deserializedSm2.getSubmodelElement(FILE_ID_SHORT_3);

		assertEquals(EXTERNAL_FILE_URL, deserializedFile1.getValue());
		assertEquals(harmonizePrefixSlash(INTERNAL_FILE_PATH_1), deserializedFile2.getValue());
		assertEquals(harmonizePrefixSlash(INTERNAL_FILE_PATH_2), deserializedFile3.getValue());
	}

	private AASBundle extractAASBundleFromAASBundleSet(Set<AASBundle> aasBundles, IIdentifier identifier) {
		return aasBundles.stream().filter(aasB -> aasB.getAAS().getIdentification().equals(identifier)).findAny().get();
	}

	private Object harmonizePrefixSlash(String path) {
		return path.startsWith("/") ? path : "/" + path;
	}

	private ISubmodel extractSubmodelFromSubmodelSet(Set<ISubmodel> submodelSet, IIdentifier identifier) {
		return submodelSet.stream().filter(sm -> sm.getIdentification().equals(identifier)).findAny().get();
	}

}
