/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.testsuite.regression.registry.observing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.registry.api.IRegistry;
import org.eclipse.basyx.registry.descriptor.AASDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.registry.memory.InMemoryRegistry;
import org.eclipse.basyx.registry.observing.IRegistryServiceObserver;
import org.eclipse.basyx.registry.observing.ObservableRegistryService;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.Before;
import org.junit.Test;

public class ObservableRegistryServiceTest {

	private static final String AASID = "aasid1";
	private static final String SUBMODELID = "submodelid1";
	private static final String AASENDPOINT = "http://localhost:8080/aasList/" + AASID + "/aas";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	private static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);

	private ObservableRegistryService observedRegistry;
	private MockObserver observer;

	@Before
	public void setup() {
		// Create underlying registry service
		IRegistry registryService = new InMemoryRegistry();

		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		AASDescriptor aasDescriptor = new AASDescriptor(shell, new Endpoint(AASENDPOINT));
		registryService.register(aasDescriptor);

		Submodel submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, new Endpoint(submodelEndpoint));
		registryService.registerSubmodelForShell(AASIDENTIFIER, submodelDescriptor);

		observedRegistry = new ObservableRegistryService(registryService);

		// Create an Observer
		observer = new MockObserver();

		// Register the observer at the API
		observedRegistry.addObserver(observer);
	}

	@Test
	public void testRegisterAAS() {
		String newAASId = "aasid2";
		Identifier newIdentifier = new Identifier(IdentifierType.IRI, newAASId);
		AssetAdministrationShell shell = new AssetAdministrationShell(newAASId, newIdentifier, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		String aasEndpoint = "http://localhost:8080/aasList/" + newAASId + "/aas";

		AASDescriptor aasDescriptor = new AASDescriptor(shell, new Endpoint(aasEndpoint));
		observedRegistry.register(aasDescriptor);

		assertEquals(newAASId, observer.shellId);
		assertTrue(observer.registerAASNotified);
	}

	@Test
	public void testUpdateAAS() {
		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		String aasEndpoint = "http://localhost:8080/aasList/" + AASID + "/aas";

		AASDescriptor aasDescriptor = new AASDescriptor(shell, new Endpoint(aasEndpoint));
		observedRegistry.updateShell(aasDescriptor.getIdentifier(), aasDescriptor);

		assertEquals(AASID, observer.shellId);
		assertTrue(observer.updateAASNotified);
	}

	@Test
	public void testRegisterSubmodel() {
		String submodelid = "submodelid2";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, submodelid);
		Submodel submodel = new Submodel(submodelid, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + submodelid + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, new Endpoint(submodelEndpoint));

		observedRegistry.registerSubmodelForShell(AASIDENTIFIER, submodelDescriptor);

		assertTrue(observer.registerSubmodelNotified);
		assertEquals(AASID, observer.shellId);
		assertEquals(submodelid, observer.submodelId);
	}

	@Test
	public void testUpdateSubmodel() {
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, SUBMODELID);
		Submodel submodel = new Submodel(SUBMODELID, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel/new/Endpoint";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, new Endpoint(submodelEndpoint));

		observedRegistry.updateSubmodelForShell(AASIDENTIFIER, submodelDescriptor);

		assertTrue(observer.updateSubmodelNotified);
		assertEquals(AASID, observer.shellId);
		assertEquals(SUBMODELID, observer.submodelId);
	}

	@Test
	public void testDeleteAAS() {
		observedRegistry.deleteShell(AASIDENTIFIER);
		assertTrue(observer.deleteAASNotified);
		assertEquals(AASID, observer.shellId);
	}

	@Test
	public void testDeleteSubmodel() {
		observedRegistry.deleteSubmodelFromShell(AASIDENTIFIER, SUBMODELIDENTIFIER);
		assertTrue(observer.deleteSubmodelNotified);
		assertEquals(AASID, observer.shellId);
		assertEquals(SUBMODELID, observer.submodelId);
	}

	@Test
	public void testRemoveObserver() {
		assertTrue(observedRegistry.removeObserver(observer));
		observedRegistry.deleteShell(AASIDENTIFIER);
		assertFalse(observer.deleteAASNotified);
	}

	private class MockObserver implements IRegistryServiceObserver {

		public boolean registerAASNotified = false;
		public boolean updateAASNotified = false;
		public boolean registerSubmodelNotified = false;
		public boolean updateSubmodelNotified = false;
		public boolean deleteAASNotified = false;
		public boolean deleteSubmodelNotified = false;

		public String shellId = "";
		public String submodelId = "";

		@Override
		public void shellRegistered(String shellId) {
			this.registerAASNotified = true;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.shellId = shellId;
		}

		@Override
		public void shellUpdated(String shellId) {
			this.registerAASNotified = false;
			this.updateAASNotified = true;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.shellId = shellId;
		}

		@Override
		public void submodelUpdated(String submodelId) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = true;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.submodelId = submodelId;
		}

		@Override
		public void submodelRegistered(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = true;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.shellId = shellIdentifier.getId();
			this.submodelId = submodelIdentifier.getId();
		}

		@Override
		public void submodelUpdated(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = true;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.shellId = shellIdentifier.getId();
			this.submodelId = submodelIdentifier.getId();
		}

		@Override
		public void shellDeleted(String shellId) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = true;
			this.deleteSubmodelNotified = false;
			this.shellId = shellId;
		}

		@Override
		public void shellSubmodelDeleted(IIdentifier shellIdentifier, IIdentifier submodelIdentifier) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = true;
			this.shellId = shellIdentifier.getId();
			this.submodelId = submodelIdentifier.getId();
		}

		@Override
		public void submodelDeleted(IIdentifier submodelIdentifier) {
			this.registerAASNotified = false;
			this.updateAASNotified = false;
			this.registerSubmodelNotified = false;
			this.updateSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = true;
			this.submodelId = submodelIdentifier.getId();
		}
	}
}
