/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.factory.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.basyx.aas.factory.json.JSONToMetamodelConverter;
import org.eclipse.basyx.aas.factory.json.MetamodelToJSONConverter;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for MetamodelToJSONConverter and JSONToMetamodelConverter
 * 
 * @author conradi
 *
 */
public class TestJSONConverter {

	private String jsonPath = "src/test/resources/aas/factory/json/aasJsonSchemaV2.0.1_Example.json";
	
	private JSONToMetamodelConverter converter;
	
	@Before
	public void buildConverter() throws IOException {
		String json = new String(Files.readAllBytes(Paths.get(jsonPath)));
		converter = new JSONToMetamodelConverter(json);
	}
	
	@Test
	public void testParseAAS() {
		checkAASs(converter.parseAAS());
	}
	
	@Test
	public void testParseSubmodels() {
		checkSubmodels(converter.parseSubmodels());		
	}
	
	@Test
	public void testParseAssets() {
		checkAssets(converter.parseAssets());
	}
	
	@Test
	public void testParseConceptDescriptions() {
		checkConceptDescriptions(converter.parseConceptDescriptions());
	}
	
	@Test
	public void testBuildJSON() {
		
		// Read Metamodel-Objects from JSON-File
		List<AssetAdministrationShell> aasList = converter.parseAAS();
		List<Asset> assetList = converter.parseAssets();
		List<ConceptDescription> conceptDescriptionList = converter.parseConceptDescriptions();
		List<Submodel> submodelList = converter.parseSubmodels();
		
		// Convert Metamodel-Objects to JSON
		String json = MetamodelToJSONConverter.convertToJSON(aasList, assetList, conceptDescriptionList, submodelList);
		
		// Convert new JSON back to Metamodel-Objects to check them
		JSONToMetamodelConverter converter2 = new JSONToMetamodelConverter(json);
		
		// Check if the Metamodel-Objects are still correct
		checkAASs(converter2.parseAAS());
		checkAssets(converter2.parseAssets());
		checkConceptDescriptions(converter2.parseConceptDescriptions());
		checkSubmodels(converter2.parseSubmodels());
	}
	
	

	private void checkAASs(List<AssetAdministrationShell> aasList) {
		assertEquals(1, aasList.size());
		AssetAdministrationShell aas = aasList.get(0);
		
		assertEquals("ExampleMotor", aas.getIdShort());
		assertEquals(3, aas.getSubmodelReferences().size());
		assertEquals("http://customer.com/aas/9175_7013_7091_9168", aas.getIdentification().getId());
	}
	
	private void checkSubmodels(List<Submodel> smList) {
		assertEquals(3, smList.size());
		
		Submodel sm = smList.stream().filter(c -> c.getIdShort().equals("TechnicalData")).findAny().get();
		
		assertEquals(1, sm.getSubmodelElements().size());
		
		Property smElement = (Property) sm.getSubmodelElements().get("MaxRotationSpeed");
		assertEquals("5000", smElement.getValue());
	}
	
	private void checkAssets(List<Asset> assetList) {
		assertEquals(1, assetList.size());
		Asset asset = assetList.get(0);
		
		assertEquals("ServoDCMotor", asset.getIdShort());
		assertEquals("http://customer.com/assets/KHBVZJSQKIY", asset.getIdentification().getId());
	}
	
	private void checkConceptDescriptions(List<ConceptDescription> conceptDescriptionList) {
		assertEquals(5, conceptDescriptionList.size());
		
		ConceptDescription cd = conceptDescriptionList.stream()
				.filter(c -> c.getIdShort().equals("DigitalFile")).findAny().get();
		
		assertEquals("www.vdi2770.com/blatt1/Entwurf/Okt18/cd/StoredDocumentRepresentation/DigitalFile",
				cd.getIdentification().getId());
	}
	
}
