/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.aas.aggregator.observing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.observing.IAASAggregatorObserver;
import org.eclipse.basyx.aas.aggregator.observing.ObservableAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.Before;
import org.junit.Test;


public class ObservableAASAggregatorTest {
	protected AssetAdministrationShell shell;
	private static final String AASID = "aasid1";
	private static final Identifier AASIDENTIFIER = new Identifier(IdentifierType.IRI, AASID);
	
	private ObservableAASAggregator observerdAASAggregator;
	private MockObserver observer;
	
	@Before
	public void setup() {
		IAASAggregator aggregator = new AASAggregator();
		shell = new AssetAdministrationShell(AASID, AASIDENTIFIER, new Asset(AASID, AASIDENTIFIER, AssetKind.INSTANCE));
		aggregator.createShell(shell);
		
		observerdAASAggregator = new ObservableAASAggregator(aggregator);
		
		// Create an Observer
		observer = new MockObserver();
		
		// Register the observer at the API
		observerdAASAggregator.addObserver(observer);
	}
	
	@Test
	public void testCreateAAS() {
		String aasId2 = "aas2";
		Identifier identifier2 = new Identifier(IdentifierType.IRDI, aasId2);
		AssetAdministrationShell shell2 = new AssetAdministrationShell(aasId2, identifier2, new Asset("assetid2", new Identifier(IdentifierType.IRI, "assetid2"), AssetKind.INSTANCE));
		observerdAASAggregator.createShell(shell2);
		
		assertEquals(aasId2, observer.aasId);
		assertTrue(observer.createdNotified);
	}
	
	@Test
	public void testUpdateAAS() {
		shell.setCategory("newCategory");
		observerdAASAggregator.updateAAS(shell);
		
		assertTrue(observer.updatedNotified);
		assertEquals(AASID, observer.aasId);
	}
	
	@Test
	public void testDeleteAAS() {
		observerdAASAggregator.deleteAAS(AASIDENTIFIER);
		assertTrue(observer.deletedNotified);
		assertEquals(AASID, observer.aasId);
	}
	
	@Test
	public void testRemoveObserver() {
		assertTrue(observerdAASAggregator.removeObserver(observer));
		observerdAASAggregator.deleteAAS(AASIDENTIFIER);
		assertFalse(observer.deletedNotified);
	}
	
	private class MockObserver implements IAASAggregatorObserver {
		
		public boolean createdNotified = false;
		public boolean deletedNotified = false;
		public boolean updatedNotified = false;
		
		public String aasId = "";

		@Override
		public void aasCreated(String aasId) {
			createdNotified = true;
			deletedNotified = false;
			updatedNotified = false;
			this.aasId = aasId;
		}

		@Override
		public void aasUpdated(String aasId) {
			createdNotified = false;
			deletedNotified = false;
			updatedNotified = true;
			this.aasId = aasId;
		}

		@Override
		public void aasDeleted(String aasId) {
			createdNotified = false;
			deletedNotified = true;
			updatedNotified = false;
			this.aasId = aasId;
		}
	}
}
