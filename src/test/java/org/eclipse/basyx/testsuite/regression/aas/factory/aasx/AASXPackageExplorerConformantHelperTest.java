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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.basyx.aas.factory.aasx.AASXPackageExplorerConformantHelper;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * 
 * @author fried
 *
 */
public class AASXPackageExplorerConformantHelperTest {
	@Test
	public void testRemoveAASRefInSubmodelRef() {
		Key submodelKey = createDummySubmodelKey();
		AssetAdministrationShell testAAS = createAASWithSingleSubmodelReferenceContainingAASKey(submodelKey);

		AasEnv env = AASXPackageExplorerConformantHelper.adapt(Collections.singleton(testAAS), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList());

		assertAASKeyRemovedFromSubmodelReferences(submodelKey, env);
	}

	private Key createDummySubmodelKey() {
		return new Key(KeyElements.SUBMODEL, false, "testSubmodel", IdentifierType.CUSTOM);
	}

	private void assertAASKeyRemovedFromSubmodelReferences(Key submodelKey, AasEnv env) {
		Collection<IAssetAdministrationShell> convertedAASs = env.getAssetAdministrationShells();
		assertEquals(1, convertedAASs.size());

		IAssetAdministrationShell convertedAAS = convertedAASs.iterator().next();
		Collection<IReference> convertedReferences = convertedAAS.getSubmodelReferences();
		assertEquals(1, convertedReferences.size());

		Reference expected = new Reference(submodelKey);
		assertEquals(expected, convertedReferences.iterator().next());
	}

	private AssetAdministrationShell createAASWithSingleSubmodelReferenceContainingAASKey(Key submodelKey) {
		AssetAdministrationShell testAAS = createDummyAAS("testAAS");

		Reference submodelReference = createSubmodelReference(testAAS, submodelKey);

		testAAS.addSubmodelReference(submodelReference);
		return testAAS;
	}

	private Reference createSubmodelReference(IAssetAdministrationShell aas, Key submodelKey) {
		Key aasKey = createDummyAASKey(aas);

		List<IKey> keys = Arrays.asList(aasKey, submodelKey);

		return new Reference(keys);
	}

	private AssetAdministrationShell createDummyAAS(final String aasId) {
		return new AssetAdministrationShell("testAASIdShort", new CustomId(aasId),
				new Asset("testAssetIdShort", new CustomId("testAsset"), AssetKind.INSTANCE));
	}

	private Key createDummyAASKey(IAssetAdministrationShell aas) {
		IIdentifier identifier = aas.getIdentification();
		return new Key(KeyElements.ASSETADMINISTRATIONSHELL, false, identifier.getId(), identifier.getIdType());
	}
}
