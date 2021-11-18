/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.metamodel.connected;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
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

		Submodel submodel = retrieveBaselineSM();
		submodel.setParent(shell.getReference());
		provider.addSubmodel(new SubmodelProvider(Submodel.createAsFacade(TypeDestroyer.destroyType(submodel))));

		// Create AAS registry
		IRegistry registry = new InMemoryRegistry();
		// Create AAS Descriptor
		AASDescriptor shellDescriptor = new AASDescriptor(SHELLIDSHORT, SHELLIDENTIFIER, Arrays.asList(new Endpoint("/aas")));
		// Create Submodel Descriptor
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(SUBMODELIDSHORT, SUBMODELIDENTIFIER, Arrays.asList(new Endpoint("/aas/submodels/" + SUBMODELIDSHORT + "/submodel")));
		// Add Submodel descriptor to aas descriptor
		shellDescriptor.addSubmodelDescriptor(submodelDescriptor);

		registry.register(shellDescriptor);

		// Create connector provider stub, map address to provider
		ConnectorProviderStub connectorProvider = new ConnectorProviderStub();
		connectorProvider.addMapping("", provider);

		// Create connection manager using the dummy
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry,
				connectorProvider);

		// Create ConnectedAssetAdministrationShell
		connectedAAS = manager.retrieveAAS(SHELLIDENTIFIER);
	}

	@Override
	protected ConnectedAssetAdministrationShell retrieveShell() {
		return connectedAAS;
	}

	@Test
	public void testGetSpecificSubmodel() {
		ISubmodel submodel = retrieveShell().getSubmodel(SUBMODELIDENTIFIER);
		assertEquals(SUBMODELIDSHORT, submodel.getIdShort());
	}

	@Test
	public void testDeleteSubmodel() {
		retrieveShell().removeSubmodel(SUBMODELIDENTIFIER);
		assertFalse(retrieveShell().getSubmodels().containsKey(SUBMODELIDSHORT));
	}

	@Test
	public void testGetLocalCopy() {
		AASModelProvider aasProvider = new AASModelProvider(retrieveBaselineShell());
		ConnectedAssetAdministrationShell localCAAS = new ConnectedAssetAdministrationShell(new VABElementProxy("", aasProvider));

		assertEquals(retrieveBaselineShell(), localCAAS.getLocalCopy());
	}
}
