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

package org.eclipse.basyx.testsuite.regression.submodel.aggregator.observing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.observing.ISubmodelAggregatorObserver;
import org.eclipse.basyx.submodel.aggregator.observing.ObservableSubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests events emitting with the SubmodelAggregator
 * 
 * @author fischer, jungjan, fried
 *
 */
public class ObservableSubmodelAggregatorTest {
	protected Submodel submodel;

	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String PARENT_AAS_ID = "parent_aas";
	private static final Identifier PARENT_AAS_IDENTIFIER = new Identifier(IdentifierType.IRI, PARENT_AAS_ID);

	private ObservableSubmodelAggregator observerdSubmodelAggregator;
	private MockObserver observer;

	@Before
	public void setup() {
		ISubmodelAggregator aggregator = new SubmodelAggregator();
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);

		IReference parentReference = new Reference(PARENT_AAS_IDENTIFIER, KeyElements.ASSETADMINISTRATIONSHELL, false);

		submodel.setParent(parentReference);
		aggregator.createSubmodel(submodel);

		observerdSubmodelAggregator = new ObservableSubmodelAggregator(aggregator);

		// Create an Observer
		observer = new MockObserver();

		// Register the observer at the API
		observerdSubmodelAggregator.addObserver(observer);
	}

	@Test
	public void testCreateSubmodel() {
		String newSubmodelIdShort = "newSubmodelIdShort";
		Identifier newSubmodelIdentifier = new Identifier(IdentifierType.IRDI, newSubmodelIdShort);
		Submodel newSubmodel = new Submodel(newSubmodelIdShort, newSubmodelIdentifier);
		observerdSubmodelAggregator.createSubmodel(newSubmodel);

		assertEquals(newSubmodelIdShort, observer.submodelId);
		assertTrue(observer.createdNotified);
	}

	@Test
	public void testUpdateSubmodel() {
		submodel.setCategory("newCategory");
		observerdSubmodelAggregator.updateSubmodel(submodel);

		assertTrue(observer.updatedNotified);
		assertEquals(SUBMODEL_ID, observer.submodelId);
		assertEquals(PARENT_AAS_ID, observer.shellId);
	}

	@Test
	public void testDeleteSubmodelByIdentifier() {
		observerdSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		assertTrue(observer.deletedNotified);
		assertEquals(SUBMODEL_ID, observer.submodelId);
		assertEquals(PARENT_AAS_ID, observer.shellId);
	}

	@Test
	public void testDeleteSubmodelByIdShort() {
		observerdSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
		assertTrue(observer.deletedNotified);
		assertEquals(SUBMODEL_ID, observer.submodelId);
		assertEquals(PARENT_AAS_ID, observer.shellId);
	}

	@Test
	public void testRemoveObserver() {
		assertTrue(observerdSubmodelAggregator.removeObserver(observer));
		observerdSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		assertFalse(observer.deletedNotified);
	}

	private class MockObserver implements ISubmodelAggregatorObserver {

		public boolean createdNotified = false;
		public boolean deletedNotified = false;
		public boolean updatedNotified = false;

		public String shellId = "";
		public String submodelId = "";

		@Override
		public void submodelCreated(String shellId, String submodelId) {
			createdNotified = true;
			deletedNotified = false;
			updatedNotified = false;
			this.shellId = shellId;
			this.submodelId = submodelId;
		}

		@Override
		public void submodelUpdated(String shellId, String submodelId) {
			createdNotified = false;
			deletedNotified = false;
			updatedNotified = true;
			this.shellId = shellId;
			this.submodelId = submodelId;
		}

		@Override
		public void submodelDeleted(String shellId, String submodelId) {
			createdNotified = false;
			deletedNotified = true;
			updatedNotified = false;
			this.shellId = shellId;
			this.submodelId = submodelId;
		}
	}
}
