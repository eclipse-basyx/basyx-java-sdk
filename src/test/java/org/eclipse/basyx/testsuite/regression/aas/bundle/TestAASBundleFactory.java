/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.testsuite.regression.aas.bundle;

import java.util.Collections;

import org.eclipse.basyx.aas.bundle.AASBundleFactory;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.junit.Test;

/**
 * Tests AASBundleFactory
 * 
 * @author schnicke
 *
 */
public class TestAASBundleFactory {

	@Test
	public void testBundleCreationAssetAlreadySetInAAS() {
		Asset asset = new Asset("assetIdShort", new CustomId("assetId"), AssetKind.INSTANCE);
		
		AssetAdministrationShell shell = new AssetAdministrationShell("aasIdShort", new CustomId("aasId"), asset);
		new AASBundleFactory().create(Collections.singleton(shell), Collections.emptySet(),
				Collections.singleton(asset));
	}
}
