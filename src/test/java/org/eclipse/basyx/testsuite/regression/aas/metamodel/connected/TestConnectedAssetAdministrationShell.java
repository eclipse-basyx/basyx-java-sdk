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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.connected;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedHashMap;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.aas.metamodel.AssetAdministrationShellSuite;
import org.eclipse.basyx.testsuite.regression.vab.gateway.ConnectorProviderStub;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the connected implementation of {@link IAssetAdministrationShell} based
 * on the AAS test suite <br />
 * 
 * @author schnicke
 *
 */
public class TestConnectedAssetAdministrationShell extends AssetAdministrationShellSuite {

	static ConnectedAssetAdministrationShell connectedAAS;

	@Before
	public void build() throws Exception {
		MultiSubmodelProvider provider = new MultiSubmodelProvider();
		AssetAdministrationShell shell = retrieveBaselineShell();
		provider.setAssetAdministrationShell(new AASModelProvider(AssetAdministrationShell.createAsFacade(TypeDestroyer.destroyType(shell))));

		Submodel sm = retrieveBaselineSM();
		sm.setParent(shell.getReference());
		provider.addSubmodel(new SubmodelProvider(Submodel.createAsFacade(TypeDestroyer.destroyType(sm))));

		// Create AAS registry
		IAASRegistry registry = new InMemoryRegistry();
		// Create AAS Descriptor
		AASDescriptor aasDescriptor = new AASDescriptor(AASID, "/aas");
		// Create Submodel Descriptor
		SubmodelDescriptor smDescriptor2 = new SubmodelDescriptor(SMIDSHORT, SMID, "/aas/submodels/" + SMIDSHORT + "/submodel");
		// Add Submodel descriptor to aas descriptor
		aasDescriptor.addSubmodelDescriptor(smDescriptor2);

		registry.register(aasDescriptor);

		// Create connector provider stub, map address to provider
		ConnectorProviderStub connectorProvider = new ConnectorProviderStub();
		connectorProvider.addMapping("", provider);

		// Create connection manager using the dummy
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry, connectorProvider);

		// Create ConnectedAssetAdministrationShell
		connectedAAS = manager.retrieveAAS(AASID);
	}

	@Override
	protected ConnectedAssetAdministrationShell retrieveShell() {
		return connectedAAS;
	}

	@Test
	public void testGetSpecificSubmodel() {
		ISubmodel sm = retrieveShell().getSubmodel(SMID);
		assertEquals(SMIDSHORT, sm.getIdShort());
	}

	@Test
	public void testDeleteSubmodel() {
		retrieveShell().removeSubmodel(SMID);
		assertFalse(retrieveShell().getSubmodels().containsKey(SMIDSHORT));
	}

	@Test
	public void testGetLocalCopy() {
		AASModelProvider aasProvider = new AASModelProvider(retrieveBaselineShell());
		ConnectedAssetAdministrationShell localCAAS = new ConnectedAssetAdministrationShell(new VABElementProxy("", aasProvider));

		AssetAdministrationShell originalAAS = addAssetReferenceToAAS(retrieveBaselineShell());
		assertEquals(originalAAS, localCAAS.getLocalCopy());
	}

	private AssetAdministrationShell addAssetReferenceToAAS(AssetAdministrationShell aas) {
		Asset asset = (Asset) aas.getAsset();
		LinkedHashMap<String, Object> modifiedAsset = new LinkedHashMap<>();
		modifiedAsset.put("keys", asset.getReference().getKeys());
		modifiedAsset.putAll(asset);
		aas.setAsset(Asset.createAsFacade(modifiedAsset));
		return aas;
	}
}
