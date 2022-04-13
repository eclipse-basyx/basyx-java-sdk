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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.junit.Test;

public class TestAasEnv {

	@Test
	public void testAssetsGetSet() {
		AasEnv env = new AasEnv();
		Asset asset = new Asset();
		asset.setIdShort("TestAasEnv");
		env.setAssets(Arrays.asList(asset));
		assertEquals(asset, env.getAssets().toArray()[0]);
	}

	@Test
	public void testAssetAdministrationShellGetSet() {
		AasEnv env = new AasEnv();
		AssetAdministrationShell aas = new AssetAdministrationShell();
		aas.setIdShort("TestAasEnv");
		env.setAssetAdministrationShells(Arrays.asList(aas));
		assertEquals(aas, env.getAssetAdministrationShells().toArray()[0]);
	}

	@Test
	public void testConceptDescriptionsGetSet() {
		AasEnv env = new AasEnv();
		ConceptDescription conceptDescriptions = new ConceptDescription();
		conceptDescriptions.setIdShort("TestAasEnv");
		env.setConceptDescriptions(Arrays.asList(conceptDescriptions));
		assertEquals(conceptDescriptions, env.getConceptDescriptions().toArray()[0]);
	}

	@Test
	public void testSubmodelsGetSet() {
		AasEnv env = new AasEnv();
		Submodel submodels = new Submodel();
		submodels.setIdShort("TestAasEnv");
		env.setSubmodels(Arrays.asList(submodels));
		assertEquals(submodels, env.getSubmodels().toArray()[0]);
	}

	@Test
	public void testCreateAsFacade() {
		Map<String, Object> asset = new LinkedHashMap<>();
		asset.put(ModelType.MODELTYPE, Asset.MODELTYPE);
		asset.put(Referable.IDSHORT, "TestAsset");
		asset.put(Asset.KIND, AssetKind.INSTANCE);
		asset.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRI, "testAssetIdShort"));

		Map<String, Object> assetAdministrationShell = new LinkedHashMap<>();
		assetAdministrationShell.put(ModelType.MODELTYPE, AssetAdministrationShell.MODELTYPE);
		assetAdministrationShell.put(Referable.IDSHORT, "TestAssetAdministrationShell");
		assetAdministrationShell.put(AssetAdministrationShell.ASSET, asset);
		assetAdministrationShell.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRI, "testAASIdShort"));

		Map<String, Object> submodel = new LinkedHashMap<>();
		submodel.put(ModelType.MODELTYPE, Submodel.MODELTYPE);
		submodel.put(Referable.IDSHORT, "TestSubmodel");
		submodel.put(Submodel.SUBMODELELEMENT, new ArrayList<Object>());
		submodel.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRI, "testSubmodelIdShort"));

		Map<String, Object> conceptDescription = new LinkedHashMap<>();
		conceptDescription.put(ModelType.MODELTYPE, ConceptDescription.MODELTYPE);
		conceptDescription.put(Referable.IDSHORT, "TestConceptDescription");
		conceptDescription.put(Identifiable.IDENTIFICATION, new Identifier(IdentifierType.IRI, "testConceptDesIdShort"));

		Map<String, Object> aasEnvAsMap = new LinkedHashMap<>();
		aasEnvAsMap.put(AasEnv.ASSETS, Arrays.asList(asset));
		aasEnvAsMap.put(AasEnv.ASSETADMINISTRATIONSHELLS, Arrays.asList(assetAdministrationShell));
		aasEnvAsMap.put(AasEnv.SUBMODELS, Arrays.asList(submodel));
		aasEnvAsMap.put(AasEnv.CONCEPTDESCRIPTIONS, Arrays.asList(conceptDescription));

		AasEnv aasEnv = AasEnv.createAsFacade(aasEnvAsMap);

		Asset assetObj = (Asset) aasEnv.getAssets().toArray()[0];
		assertEquals(assetObj.getIdShort(), asset.get(Referable.IDSHORT));
		AssetAdministrationShell assetAdministrationShellObj = (AssetAdministrationShell) aasEnv.getAssetAdministrationShells().toArray()[0];
		assertEquals(assetAdministrationShellObj.getIdShort(), assetAdministrationShell.get(Referable.IDSHORT));
		Submodel submodelObj = (Submodel) aasEnv.getSubmodels().toArray()[0];
		assertEquals(submodelObj.getIdShort(), submodel.get(Referable.IDSHORT));
		ConceptDescription conceptDescriptionObj = (ConceptDescription) aasEnv.getConceptDescriptions().toArray()[0];
		assertEquals(conceptDescriptionObj.getIdShort(), conceptDescription.get(Referable.IDSHORT));
	}
}
