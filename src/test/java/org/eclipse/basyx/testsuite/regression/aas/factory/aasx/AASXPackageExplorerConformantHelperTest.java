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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
	
	@Test
	public void checkForCrashWhenAdaptIsCalledMultipleTimes() throws IOException, TransformerException, ParserConfigurationException {
		AssetAdministrationShell assetAdministrationShell = createDummyAAS("testAssetAdministrationShell");
        
		Reference submodelReference1 =  createSubmodelReference(assetAdministrationShell, createDummySubmodelKey());
		Reference submodelReference2 = createSubmodelReference(assetAdministrationShell, createDummySubmodelKey());

		assetAdministrationShell.addSubmodelReference(submodelReference1);
		assetAdministrationShell.addSubmodelReference(submodelReference2);

		Collection<IAssetAdministrationShell> listOfAssetAdministrationShell = Arrays.asList(assetAdministrationShell);
        
        AASXPackageExplorerConformantHelper.adapt(listOfAssetAdministrationShell, Collections.emptyList(),
    				Collections.emptyList(), Collections.emptyList());
        AASXPackageExplorerConformantHelper.adapt(listOfAssetAdministrationShell, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList());
	}
}
