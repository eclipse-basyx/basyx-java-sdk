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

package org.eclipse.basyx.testsuite.regression.aas.registration.observing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.registration.observing.IAASRegistryServiceObserver;
import org.eclipse.basyx.aas.registration.observing.ObservableAASRegistryService;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.Before;
import org.junit.Test;

public class ObservableAASRegistryServiceTest {

	private static final String AASID = "aasid1";
	private static final String SUBMODELID = "submodelid1";
	private static final String AASENDPOINT = "http://localhost:8080/aasList/" + AASID + "/aas";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	private static final Identifier SUBMODELIDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODELID);

	private ObservableAASRegistryService observedRegistry;
	private MockObserver observer;

	@Before
	public void setup() {
		// Create underlying registry service
		IAASRegistry registryService = new InMemoryRegistry();

		AssetAdministrationShell shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset("assetid1", new Identifier(IdentifierType.IRI, "assetid1"), AssetKind.INSTANCE));
		AASDescriptor aasDescriptor = new AASDescriptor(shell, AASENDPOINT);
		registryService.register(aasDescriptor);

		Submodel submodel = new Submodel(SUBMODELID, SUBMODELIDENTIFIER);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + SUBMODELID + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);
		registryService.register(AASIDENTIFIER, submodelDescriptor);

		observedRegistry = new ObservableAASRegistryService(registryService);

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

		AASDescriptor aasDescriptor = new AASDescriptor(shell, aasEndpoint);
		observedRegistry.register(aasDescriptor);

		assertEquals(newAASId, observer.aasId);
		assertTrue(observer.registerAASNotified);
	}

	@Test
	public void testRegisterSubmodel() {
		String submodelid = "submodelid2";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRI, submodelid);
		Submodel submodel = new Submodel(submodelid, newSubmodelIdentifier);
		String submodelEndpoint = AASENDPOINT + "/submodels/" + submodelid + "/submodel";
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(submodel, submodelEndpoint);

		observedRegistry.register(AASIDENTIFIER, submodelDescriptor);

		assertTrue(observer.registerSubmodelNotified);
		assertEquals(AASID, observer.aasId);
		assertEquals(submodelid, observer.smId);
	}

	@Test
	public void testDeleteAAS() {
		observedRegistry.delete(AASIDENTIFIER);
		assertTrue(observer.deleteAASNotified);
		assertEquals(AASID, observer.aasId);
	}

	@Test
	public void testDeleteSubmodel() {
		observedRegistry.delete(AASIDENTIFIER, SUBMODELIDENTIFIER);
		assertTrue(observer.deleteSubmodelNotified);
		assertEquals(AASID, observer.aasId);
		assertEquals(SUBMODELID, observer.smId);
	}

	@Test
	public void testRemoveObserver() {
		assertTrue(observedRegistry.removeObserver(observer));
		observedRegistry.delete(AASIDENTIFIER);
		assertFalse(observer.deleteAASNotified);
	}

	private class MockObserver implements IAASRegistryServiceObserver {

		public boolean registerAASNotified = false;
		public boolean registerSubmodelNotified = false;
		public boolean deleteAASNotified = false;
		public boolean deleteSubmodelNotified = false;

		public String aasId = "";
		public String smId = "";

		@Override
		public void aasRegistered(String aasId) {
			this.registerAASNotified = true;
			this.registerSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.aasId = aasId;
		}

		@Override
		public void submodelRegistered(IIdentifier aasId, IIdentifier smId) {
			this.registerAASNotified = false;
			this.registerSubmodelNotified = true;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = false;
			this.aasId = aasId.getId();
			this.smId = smId.getId();
		}

		@Override
		public void aasDeleted(String aasId) {
			this.registerAASNotified = false;
			this.registerSubmodelNotified = false;
			this.deleteAASNotified = true;
			this.deleteSubmodelNotified = false;
			this.aasId = aasId;
		}

		@Override
		public void submodelDeleted(IIdentifier aasId, IIdentifier smId) {
			this.registerAASNotified = false;
			this.registerSubmodelNotified = false;
			this.deleteAASNotified = false;
			this.deleteSubmodelNotified = true;
			this.aasId = aasId.getId();
			this.smId = smId.getId();
		}
	}
}
