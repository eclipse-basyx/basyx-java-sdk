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
package org.eclipse.basyx.testsuite.regression.submodel.restapi.observing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.observing.ISubmodelAPIObserver;
import org.eclipse.basyx.submodel.restapi.observing.ObservableSubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for ObservableSubmodelAPI
 * 
 * @author conradi
 *
 */
public class ObservableSubmodelAPITest {

	private static final String AAS_ID = "testaasid";
	private static final String SUBMODEL_ID = "testsubmodelid";
	private static final String PROPERTY_ID = "testpropertyid";
	
	
	private ObservableSubmodelAPI api;
	private MockObserver observer;
	
	@Before
	public void setup() {
		// Create submodel
		Submodel sm = new Submodel(SUBMODEL_ID, new Identifier(IdentifierType.CUSTOM, SUBMODEL_ID));
		Reference parentRef = new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, true, AAS_ID, IdentifierType.IRDI));
		sm.setParent(parentRef);
		
		// Create Property
		Property prop = new Property(PROPERTY_ID, 1);
		sm.addSubmodelElement(prop);
		
		// Create an Observer
		observer = new MockObserver();
		
		// Create ObservableAPI
		VABSubmodelAPI vabAPI = new VABSubmodelAPI(new VABMapProvider(sm));
		api = new ObservableSubmodelAPI(vabAPI);
		
		// Register the observer at the API
		api.addObserver(observer);
	}
	
	@Test
	public void testAddElement() {
		Property prop1 = new Property("newProperty1", "newtest1");
		api.addSubmodelElement(prop1);
		assertTrue(observer.addedNotified);
		assertEquals(prop1.getIdShort(), observer.idShortPath);
		assertEquals(prop1.getValue(), observer.newValue);
		
		observer.addedNotified = false;
		
		Property prop2 = new Property("newProperty2", "newtest2");
		api.addSubmodelElement(prop2.getIdShort(), prop2);
		assertTrue(observer.addedNotified);
		assertEquals(prop2.getIdShort(), observer.idShortPath);
		assertEquals(prop2.getValue(), observer.newValue);
	}
	
	@Test
	public void testDeleteElement() {
		api.deleteSubmodelElement(PROPERTY_ID);
		assertTrue(observer.deletedNotified);
		assertEquals(PROPERTY_ID, observer.idShortPath);
	}
	
	@Test
	public void testUpdateElement() {
		api.updateSubmodelElement(PROPERTY_ID, 2);
		assertTrue(observer.updatedNotified);
		assertEquals(PROPERTY_ID, observer.idShortPath);
		assertEquals(2, observer.newValue);
	}
	
	@Test
	public void testRemoveObserver() {
		assertTrue(api.removeObserver(observer));
		api.deleteSubmodelElement(PROPERTY_ID);
		assertFalse(observer.deletedNotified);
	}
	
	private class MockObserver implements ISubmodelAPIObserver {
		
		public boolean addedNotified = false;
		public boolean deletedNotified = false;
		public boolean updatedNotified = false;
		
		public String idShortPath = "";
		public Object newValue = null;

		@Override
		public void elementAdded(String idShortPath, Object newValue) {
			addedNotified = true;
			this.idShortPath = idShortPath;
			this.newValue = newValue;
		}

		@Override
		public void elementDeleted(String idShortPath) {
			deletedNotified = true;
			this.idShortPath = idShortPath;
		}

		@Override
		public void elementUpdated(String idShortPath, Object newValue) {
			updatedNotified = true;
			this.idShortPath = idShortPath;
			this.newValue = newValue;
		}
		
	}
	
}
