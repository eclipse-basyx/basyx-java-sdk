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
package org.eclipse.basyx.testsuite.regression.aas.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.testsuite.regression.vab.gateway.ConnectorProviderStub;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ConnectedAssetAdministrationShellManager class
 * 
 * @author schnicke
 * 
 */
public class TestConnectedAssetAdministrationShellManager {
	ConnectedAssetAdministrationShellManager manager;
	ConnectorProviderStub connectorProvider;
	IAASRegistry registry;

	/**
	 * Create infrastructure
	 */
	@Before
	public void build() {
		registry = new InMemoryRegistry();
		connectorProvider = new ConnectorProviderStub();

		// Create connection manager using the dummy
		manager = new ConnectedAssetAdministrationShellManager(registry, connectorProvider);
	}

	/**
	 * Tests creation of aas
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAAS() throws Exception {
		// Register AAS at directory
		IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aasId");
		String aasIdShort = "aasName";
		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		// Create an AAS containing a reference to the created Submodel
		AssetAdministrationShell aas = createTestAAS(aasId, aasIdShort);
		manager.createAAS(aas, "");

		// Check descriptor for correct endpoint
		String endpoint = registry.lookupAAS(aasId).getFirstEndpoint();
		assertEquals(AASAggregatorProvider.PREFIX + "/" + aasId.getId() + "/aas", VABPathTools.stripSlashes(endpoint));

		// Retrieve it
		ConnectedAssetAdministrationShell connectedAAS = manager.retrieveAAS(aasId);
		assertEquals(aasIdShort, connectedAAS.getIdShort());
		assertEquals(aasId.getId(), connectedAAS.getIdentification().getId());
		assertEquals(aasId.getIdType(), connectedAAS.getIdentification().getIdType());
	}

	@Test
	public void testCreateSubmodel() throws Exception {
		IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aasId");
		IIdentifier smId = new Identifier(IdentifierType.CUSTOM, "smId");
		String smIdShort = "smName";

		// Register AAS at directory
		AASDescriptor desc = new AASDescriptor(aasId, "/aas");
		registry.register(desc);
		IModelProvider provider = new MultiSubmodelProvider(new AASModelProvider(new AssetAdministrationShell()));
		connectorProvider.addMapping("", provider);

		// Create sub model
		Submodel submodel = new Submodel();
		submodel.setIdShort(smIdShort);
		submodel.setIdentification(smId.getIdType(), smId.getId());

		// - Add example properties to sub model
		Property prop1 = new Property(7);
		prop1.setIdShort("prop1");
		submodel.addSubmodelElement(prop1);

		Property prop2 = new Property("myStr");
		prop2.setIdShort("prop2");
		submodel.addSubmodelElement(prop2);

		// - Retrieve submodel using the SDK connector
		manager.createSubmodel(aasId, submodel);
		ISubmodel sm = manager.retrieveSubmodel(aasId, smId);

		// - check id and properties
		IProperty prop1Connected = sm.getProperties().get("prop1");
		IProperty prop2Connected = sm.getProperties().get("prop2");

		assertEquals(smIdShort, sm.getIdShort());
		assertEquals(smId.getId(), sm.getIdentification().getId());
		assertEquals(smId.getIdType(), sm.getIdentification().getIdType());
		assertEquals("prop1", prop1Connected.getIdShort());
		assertEquals(7, prop1Connected.getValue());
		assertEquals("prop2", prop2Connected.getIdShort());
		assertEquals("myStr", prop2Connected.getValue());
	}

	@Test
	public void testDeleteSubmodel() {
		IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aasId");
		String aasIdShort = "aasName";

		IIdentifier smId = new Identifier(IdentifierType.CUSTOM, "smId");
		String smIdShort = "smName";

		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		AssetAdministrationShell aas = createTestAAS(aasId, aasIdShort);
		manager.createAAS(aas, "");

		Submodel sm = new Submodel(smIdShort, smId);
		manager.createSubmodel(aasId, sm);

		// Assert everything was created correctly
		IAssetAdministrationShell connectedAAS = manager.retrieveAAS(aasId);
		ISubmodel connectedSm = connectedAAS.getSubmodels().get(sm.getIdShort());

		assertEquals(sm.getIdShort(), connectedSm.getIdShort());

		manager.deleteSubmodel(aasId, smId);
		assertFalse(connectedAAS.getSubmodels().containsKey(smIdShort));
	}

	@Test
	public void testDeleteAAS() {
		IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aasId");
		String aasIdShort = "aasName";

		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		AssetAdministrationShell aas = createTestAAS(aasId, aasIdShort);
		manager.createAAS(aas, "");
		manager.deleteAAS(aas.getIdentification());
		try {
			manager.retrieveAAS(aas.getIdentification());
			fail();
		} catch (ResourceNotFoundException e) {
			// Expected
		}
	}

	@Test
	public void testRetrieveAll() {
		IIdentifier aasId1 = new Identifier(IdentifierType.CUSTOM, "aasId1");
		String aasIdShort1 = "aasName1";
		IIdentifier aasId2 = new Identifier(IdentifierType.CUSTOM, "aasId2");
		String aasIdShort2 = "aasName2";

		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		// Create the AASs
		AssetAdministrationShell aas1 = createTestAAS(aasId1, aasIdShort1);
		AssetAdministrationShell aas2 = createTestAAS(aasId2, aasIdShort2);
		manager.createAAS(aas1, "");
		manager.createAAS(aas2, "");

		// Retrieve them
		Collection<IAssetAdministrationShell> connectedAASs = manager.retrieveAASAll();
		assertEquals(2, connectedAASs.size());
		connectedAASs.stream().forEach(aas -> assertTrue(aas.getIdShort().equals(aasIdShort1) || aas.getIdShort().equals(aasIdShort2)));
	}

	/**
	 * Tries to retrieve a nonexistent AAS
	 */
	@Test
	public void testRetrieveNonexistentAAS() {
		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		IIdentifier nonexistentAASId = new Identifier(IdentifierType.CUSTOM, "nonexistentAAS");

		// Try to retrieve a nonexistent AAS
		try {
			manager.retrieveAAS(nonexistentAASId);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * Tries to retrieve a nonexistent Submodel from a nonexistent AAS
	 */
	@Test
	public void testRetrieveNonexistentSMFromNonexistentSM() {
		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		IIdentifier nonexistentAASId = new Identifier(IdentifierType.CUSTOM, "nonexistentAAS");
		IIdentifier nonexistentSMId = new Identifier(IdentifierType.CUSTOM, "nonexistentSM");

		// Try to retrieve a nonexistent Submodel from a nonexistent AAS
		try {
			manager.retrieveSubmodel(nonexistentAASId, nonexistentSMId);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * Tries to retrieve a nonexistent Submodel from an existing AAS
	 */
	@Test
	public void testRetrieveNonexistentSMFromExistentAAS() {
		IModelProvider provider = new AASAggregatorProvider(new AASAggregator());
		prepareConnectorProvider(provider);

		IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, "aasId");
		IIdentifier nonexistentSMId = new Identifier(IdentifierType.CUSTOM, "nonexistentSM");

		// Try to retrieve a nonexistent Submodel from an existing AAS
		try {
			manager.retrieveSubmodel(aasId, nonexistentSMId);
			fail();
		} catch (ResourceNotFoundException e) {
		}
	}

	/**
	 * @param provider
	 */
	private void prepareConnectorProvider(IModelProvider provider) {
		connectorProvider.addMapping("", new VABElementProxy("", provider));
		connectorProvider.addMapping("shells", new VABElementProxy("shells", provider));
	}

	private AssetAdministrationShell createTestAAS(IIdentifier aasId, String aasIdShort) {
		AssetAdministrationShell aas = new AssetAdministrationShell(aasIdShort, aasId, new Asset("assetIdShort", new ModelUrn("assetId"), AssetKind.INSTANCE));
		return aas;
	}
}
