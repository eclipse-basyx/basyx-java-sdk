/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.factory.aasx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.basyx.aas.factory.aasx.AASXFactory;
import org.eclipse.basyx.aas.factory.aasx.InMemoryFile;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the AASXFactory
 * 
 * @author conradi
 *
 */
public class AASXFactoryTest {

	private static final String XML_PATH = "aasx/xml/content.xml";
	private static final String ORIGIN_PATH = "aasx/aasx-origin";
	private static final String EXTERNAL_FILE_URL = "http://localhost:8080/image.png";
	
	
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
		file1.setIdShort("file1");
		File file2 = new File("aasx/Document/docu.pdf", "application/pdf");
		file2.setIdShort("file2");
		File file3 = new File("/aasx/Document/docu2.pdf", "application/pdf");
		file3.setIdShort("file3");
		
		
		
		SubmodelElementCollection collection = new SubmodelElementCollection("collection");
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
		
		
		byte[] content1 = {5,6,7,8,9};
		InMemoryFile file = new InMemoryFile(content1, "/aasx/Document/docu.pdf");
		fileList.add(file);
		
		byte[] content2 = {10,11,12,13,14};
		file = new InMemoryFile(content2, "aasx/Document/docu2.pdf");
		fileList.add(file);
	}
	
	
	@Test
	public void testBuildAASX() throws IOException, TransformerException, ParserConfigurationException {
		
		// This stream can be used to write the .aasx directly to a file
		// FileOutputStream out = new FileOutputStream("path/to/test.aasx");
		
		// This stream keeps the output of the AASXFactory only in memory
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		AASXFactory.buildAASX(aasList, assetList, conceptDescriptionList, submodelList, fileList, out);
		
		validateAASX(out);
		
	}
	
	
	private void validateAASX(ByteArrayOutputStream byteStream) throws IOException {
		ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(byteStream.toByteArray()));
		ZipEntry zipEntry = null;

		ArrayList<String> filePaths = new ArrayList<>();
		
		while((zipEntry = in.getNextEntry()) != null) {
			if(zipEntry.getName().equals(XML_PATH)) {
				
				// Read the first 5 bytes of the XML file to make sure it is in fact XML file
				// No further test of XML file necessary as XML-Converter is tested separately
				byte[] buf = new byte[5];
				in.read(buf);
				assertEquals("<?xml", new String(buf));
				
			}
			
			// Write the paths of all files contained in the .aasx into filePaths
			filePaths.add(zipEntry.getName());
		}
		
		assertTrue(filePaths.contains(XML_PATH));
		assertTrue(filePaths.contains(ORIGIN_PATH));
		
		// Check if all expected files are present
		// Needs to strip the first slash of the paths, as ZipEntry gives paths without it
		for(InMemoryFile file: fileList) {
			assertTrue(filePaths.contains(VABPathTools.stripSlashes(file.getPath())));
		}
		
		assertFalse(filePaths.contains(VABPathTools.stripSlashes(EXTERNAL_FILE_URL)));	
	}
	
}
