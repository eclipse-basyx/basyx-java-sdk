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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * J-Unit tests for AASXPackageExplorer. This test checks the parsing of aas,
 * submodels, assets and concept-descriptions. It also checks whether the AAS
 * has correct references to the assets and submodels.
 * 
 * @author zhangzai, conradi, fischer
 *
 */
public class TestAASXToMetamodelConverterFromFile {
	/**
	 * Define constants and variables for test
	 */
	private static final String GIVEN_AASX_FILE_PATH = "src/test/resources/aas/factory/aasx/01_Festo.aasx";

	private static final int EXPECTED_AASBUNDLE_SIZE = 2;
	private static final String EXPECTED_AAS_IDSHORT = "Festo_3S7PM0CP4BD";
	private static final String EXPECTED_AAS_CATEGORY = "CONSTANT";
	private static final String EXPECTED_AAS_IDENTIFICATION_ID = "smart.festo.com/demo/aas/1/1/454576463545648365874";
	private static final IdentifierType EXPECTED_AAS_IDENTIFICATION_ID_TYPE = IdentifierType.IRI;

	private static final int EXPECTED_SUBMODEL_REFERENCE_SIZE = 5;
	private static final String EXPECTED_SUBMODEL_REFERENCE_ID_TYPE = "IRI";
	private static final String EXPECTED_SUBMODEL_REFERENCE_MODEL_TYPE = "SUBMODEL";
	private static final boolean EXPECTED_SUBMODEL_REFERENCE_LOCAL = true;
	private static final String[] EXPECTED_SUBMODEL_REFERENCE_IDS = { "www.company.com/ids/sm/6053_5072_7091_5102", "smart.festo.com/demo/sm/instance/1/1/13B7CCD9BF7A3F24", "www.company.com/ids/sm/4343_5072_7091_3242",
			"www.company.com/ids/sm/2543_5072_7091_2660", "www.company.com/ids/sm/6563_5072_7091_4267" };

	private static final int EXPECTED_SUBMODEL_SIZE = 5;
	private static final String EXPECTED_SUBMODEL_IDSHORT = "Nameplate";
	private static final String EXPECTED_SUBMODEL_IDENTIFICATION_ID = "www.company.com/ids/sm/4343_5072_7091_3242";
	private static final IdentifierType EXPECTED_SUBMODEL_IDENTIFICATION_ID_TYPE = IdentifierType.IRI;
	private static final String EXPECTED_SUBMODEL_MODELING_KIND = "Instance";

	private static final String EXPECTED_SUBMODELELEMENT_CATEGORY = "PARAMETER";
	private static final String EXPECTED_SUBMODELELEMENT_MODELING_KIND = "Instance";
	private static final ValueType EXPECTED_SUBMODELELEMENT_PROPERTY_VALUE_TYPE = ValueType.String;
	private static final String EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_TYPE = "ConceptDescription";
	private static final boolean EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_LOCAL = true;

	private static final String EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_IDSHORT = "ManufacturerName";
	private static final String EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_PROPERTY_VALUE = "Festo AG & Co. KG";
	private static final String EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE = "IRDI";
	private static final String EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_SEMANTIC_KEY_VALUE = "0173-1#02-AAO677#002";

	private static final String EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_IDSHORT = "ManufacturerProductDesignation";
	private static final String EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_PROPERTY_VALUE = "OVEL Vacuum generator";
	private static final String EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE = "IRDI";
	private static final String EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_SEMANTIC_KEY_VALUE = "0173-1#02-AAW338#001";

	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_IDSHORT = "PhysicalAddress";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE = "IRI";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_SEMANTIC_KEY_VALUE = "https://www.hsu-hh.de/aut/aas/physicaladdress";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_MODEL_TYPE = "SubmodelElementCollection";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_1_IDSHORT = "CountryCode";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_1_VALUE = "DE";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_2_IDSHORT = "Street";
	private static final String EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_2_VALUE = "Ruiter Straße 82";

	private static AASXToMetamodelConverter packageConverter;
	private static Set<AASBundle> aasBundlesFromConverter;
	private static Optional<AASBundle> specificAASBundleOptional;
	private static IAssetAdministrationShell assFromConverter;

	private static Set<ISubmodel> submodelsFromConverter;
	private static Optional<ISubmodel> specificSubmodelOptional;
	private static ISubmodel specificSubmodel;
	private static Map<String, ISubmodelElement> specificSubmodelElements;

	@BeforeClass
	public static void setup() throws InvalidFormatException, IOException, ParserConfigurationException, SAXException {
		// Initialize the aasx package converter with the path to the aasx package
		packageConverter = new AASXToMetamodelConverter(GIVEN_AASX_FILE_PATH);

		// retrieve all AASBundles of the given file
		aasBundlesFromConverter = packageConverter.retrieveAASBundles();

		// get the Optional<AASBundle> of the selected AAS and the AAS itself
		specificAASBundleOptional = getSpecificAASBundleAsOptional(aasBundlesFromConverter, EXPECTED_AAS_IDSHORT);
		assFromConverter = getAASFromBundleOptional(specificAASBundleOptional);

		// get the Optional<Submodel> of the selected submodel and the submodel itself
		// as well as its submodelElements
		submodelsFromConverter = getFirstSubmodel(aasBundlesFromConverter, EXPECTED_AAS_IDSHORT);
		specificSubmodelOptional = getSpecificSubmodelAsOptional(submodelsFromConverter, EXPECTED_SUBMODEL_IDSHORT);
		specificSubmodel = specificSubmodelOptional.get();
		specificSubmodelElements = specificSubmodel.getSubmodelElements();
	}
	
	@AfterClass
	public static void tearDown() {
		packageConverter.close();
	}

	/**
	 * Test the converted AAS with expected information.
	 */
	@Test
	public void testAASsOfConvertedAASX() {
		// check bundle size
		assertEquals(EXPECTED_AASBUNDLE_SIZE, aasBundlesFromConverter.size());

		// check if Optional is present
		assertTrue(specificAASBundleOptional.isPresent());

		// check AAS specific attributes
		assertEquals(EXPECTED_AAS_IDSHORT, assFromConverter.getIdShort());
		assertEquals(EXPECTED_AAS_CATEGORY, assFromConverter.getCategory());
		assertEquals(EXPECTED_AAS_IDENTIFICATION_ID, assFromConverter.getIdentification().getId());
		assertEquals(EXPECTED_AAS_IDENTIFICATION_ID_TYPE, assFromConverter.getIdentification().getIdType());
	}

	/**
	 * Test the converted submodel references with expected information.
	 */
	@Test
	public void testSubmodelReferencesOfConvertedAASX() {
		Collection<IReference> submodelReferences = assFromConverter.getSubmodelReferences();
		assertEquals(EXPECTED_SUBMODEL_REFERENCE_SIZE, submodelReferences.size());

		List<IReference> referencelist = new ArrayList<>();
		referencelist.addAll(submodelReferences);
		sortReferencelist(referencelist);
		for (int i = 0; i < referencelist.size(); i++) {
			IReference ref = referencelist.get(i);
			List<IKey> refKeys = ref.getKeys();

			assertEquals(EXPECTED_SUBMODEL_REFERENCE_IDS[i], refKeys.get(0).getValue());
			assertEquals(EXPECTED_SUBMODEL_REFERENCE_ID_TYPE, refKeys.get(0).getIdType().name());
			assertEquals(EXPECTED_SUBMODEL_REFERENCE_MODEL_TYPE, refKeys.get(0).getType().name());
			assertEquals(EXPECTED_SUBMODEL_REFERENCE_LOCAL, refKeys.get(0).isLocal());
		}
	}

	/**
	 * Test parsed submodels with expected information about them.
	 */
	@Test
	public void testSubmodelsOfConvertedAASX() {
		// check submodel size
		assertEquals(EXPECTED_SUBMODEL_SIZE, submodelsFromConverter.size());

		// check if Optional is present
		assertTrue(specificSubmodelOptional.isPresent());

		// check specific submodel
		assertEquals(EXPECTED_SUBMODEL_IDSHORT, specificSubmodel.getIdShort());
		assertEquals(EXPECTED_SUBMODEL_IDENTIFICATION_ID, specificSubmodel.getIdentification().getId());
		assertEquals(EXPECTED_SUBMODEL_IDENTIFICATION_ID_TYPE, specificSubmodel.getIdentification().getIdType());
		assertEquals(EXPECTED_SUBMODEL_MODELING_KIND, specificSubmodel.getKind().toString());
	}

	/**
	 * Test the manufacturer name submodel element.
	 */
	@Test
	public void testManufacturerNameSubmodelElementOfConvertedAASX() {
		// get submodelElement
		ISubmodelElement submodelElement = specificSubmodelElements.get(EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_IDSHORT);

		// check if the idShort matches
		assertEquals(EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_IDSHORT, submodelElement.getIdShort());

		// check if the category and modelingKind matches
		assertEquals(EXPECTED_SUBMODELELEMENT_CATEGORY, submodelElement.getCategory());
		assertTrue(submodelElement.getKind().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_MODELING_KIND));

		// check if the property matches
		Property prop = (Property) submodelElement;
		assertEquals(EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_PROPERTY_VALUE, prop.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_PROPERTY_VALUE_TYPE, prop.getValueType());

		// check if the semantic matches
		IReference semantic = submodelElement.getSemanticId();
		IKey semanticKey = semantic.getKeys().get(0);
		assertTrue(semanticKey.getType().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_TYPE));
		assertEquals(EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE, semanticKey.getIdType().name());
		assertEquals(EXPECTED_MANUFACTURER_NAME_SUBMODELELEMENT_SEMANTIC_KEY_VALUE, semanticKey.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_LOCAL, semanticKey.isLocal());
	}

	/**
	 * test the manufacturer product designation submodel element.
	 */
	@Test
	public void testManufacturerProductDesignationSubmodelElementOfConvertedAASX() {
		// get submodelElement
		ISubmodelElement submodelElement = specificSubmodelElements.get(EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_IDSHORT);

		// check if the idShort matches
		assertEquals(EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_IDSHORT, submodelElement.getIdShort());

		// check if the category and modelingKind matches
		assertEquals(EXPECTED_SUBMODELELEMENT_CATEGORY, submodelElement.getCategory());
		assertTrue(submodelElement.getKind().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_MODELING_KIND));

		// check if the property matches
		Property prop = (Property) submodelElement;
		assertEquals(EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_PROPERTY_VALUE, prop.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_PROPERTY_VALUE_TYPE, prop.getValueType());

		// check if the semantic matches
		IReference semantic = submodelElement.getSemanticId();
		IKey semanticKey = semantic.getKeys().get(0);
		assertTrue(semanticKey.getType().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_TYPE));
		assertEquals(EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE, semanticKey.getIdType().name());
		assertEquals(EXPECTED_MANUFACTURER_PRODUCT_DESIGNATION_SUBMODELELEMENT_SEMANTIC_KEY_VALUE, semanticKey.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_LOCAL, semanticKey.isLocal());
	}

	/**
	 * test the physical address submodel element collection.
	 */
	@Test
	public void testPhysicalAddressSubmodelElementOfConvertedAASX() {
		// get submodelElement
		ISubmodelElement submodelElement = specificSubmodelElements.get(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_IDSHORT);

		// check if the idShort matches
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_IDSHORT, submodelElement.getIdShort());

		// check if the category and modelingKind matches
		assertEquals(EXPECTED_SUBMODELELEMENT_CATEGORY, submodelElement.getCategory());
		assertTrue(submodelElement.getKind().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_MODELING_KIND));

		// check if the semantic matches
		IReference semantic = submodelElement.getSemanticId();
		IKey semanticKey = semantic.getKeys().get(0);
		assertTrue(semanticKey.getType().name().equalsIgnoreCase(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_TYPE));
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_SEMANTIC_KEY_ID_TYPE, semanticKey.getIdType().name());
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_SEMANTIC_KEY_VALUE, semanticKey.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_SEMANTIC_KEY_LOCAL, semanticKey.isLocal());

		// check submodelElementColleciton
		assertTrue(submodelElement.getModelType().equalsIgnoreCase(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_MODEL_TYPE));
		SubmodelElementCollection collection = (SubmodelElementCollection) submodelElement;
		Map<String, ISubmodelElement> submodelElementCollectionMap = collection.getSubmodelElements();

		// check if the properties match
		assertEquals(5, submodelElementCollectionMap.size());
		Property prop1 = (Property) submodelElementCollectionMap.get(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_1_IDSHORT);
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_1_IDSHORT, prop1.getIdShort());
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_1_VALUE, prop1.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_PROPERTY_VALUE_TYPE, prop1.getValueType());

		Property prop2 = (Property) submodelElementCollectionMap.get(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_2_IDSHORT);
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_2_IDSHORT, prop2.getIdShort());
		assertEquals(EXPECTED_PHYSICAL_ADDRESS_SUBMODELELEMENT_PROP_2_VALUE, prop2.getValue());
		assertEquals(EXPECTED_SUBMODELELEMENT_PROPERTY_VALUE_TYPE, prop2.getValueType());
	}

	/**
	 * Sort a given referenceList by the last two numbers of their ids.
	 * 
	 * @param referencelist
	 */
	private void sortReferencelist(List<IReference> referencelist) {
		referencelist.sort((x, y) -> {
			String idx = x.getKeys().get(0).getValue();
			String idy = y.getKeys().get(0).getValue();

			String idx_end = idx.substring(idx.length() - 2);
			int idxint = Integer.parseInt(idx_end);
			String idy_end = idy.substring(idy.length() - 2);
			int idyint = Integer.parseInt(idy_end);

			return idxint - idyint;
		});
	}

	/**
	 * Get the first submodel set from given set of AASBundles.
	 * 
	 * @param givenBundles
	 * @param specificSubmodelIdShort
	 * @return Set<ISubmodel>
	 */
	private static Set<ISubmodel> getFirstSubmodel(Set<AASBundle> givenBundles, String specificSubmodelIdShort) {
		return givenBundles.stream().filter(b -> b.getAAS().getIdShort().equals(specificSubmodelIdShort)).findFirst().get().getSubmodels();
	}

	/**
	 * Get the Optional<AASBundle> with a given specificAASIdShort from a set of
	 * AASBundles.
	 * 
	 * @param aasBundles
	 * @param specificAASIdShort
	 * @return Optional<AASBundle>
	 */
	private static Optional<AASBundle> getSpecificAASBundleAsOptional(Set<AASBundle> aasBundles, String specificAASIdShort) {
		return aasBundles.stream().filter(b -> b.getAAS().getIdShort().equals(specificAASIdShort)).findFirst();
	}

	/**
	 * Get the Optional<ISubmodel> with a given specificSubmodelIdShort from a set
	 * of submodels.
	 * 
	 * @param submodels
	 * @param specificSubmodelIdShort
	 * @return Optional<ISubmodel>
	 */
	private static Optional<ISubmodel> getSpecificSubmodelAsOptional(Set<ISubmodel> submodels, String specificSubmodelIdShort) {
		return submodels.stream().filter(s -> s.getIdShort().equals(specificSubmodelIdShort)).findFirst();
	}

	/**
	 * Get the AAS of an optional AASBundle.
	 * 
	 * @param optionalBundle
	 * @return IAssetAdministrationShell
	 */
	private static IAssetAdministrationShell getAASFromBundleOptional(Optional<AASBundle> optionalBundle) {
		AASBundle testAASBundle = optionalBundle.get();
		IAssetAdministrationShell aas = testAASBundle.getAAS();

		return aas;
	}
}
