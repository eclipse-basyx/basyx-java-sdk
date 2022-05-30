/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.testsuite.regression.submodel.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Test;

/**
 * Testsuite for implementations of the {@link ISubmodelAggregator} interface
 * 
 * @author schnicke
 *
 */
public abstract class SubmodelAggregatorSuite {
	protected abstract ISubmodelAggregator getSubmodelAggregator();

	protected static Submodel sm1 = new Submodel("idShort1", new CustomId("Sm1"));
	protected static Submodel sm2 = new Submodel("idShort2", new CustomId("Sm2"));

	@Test
	public void getSubmodelByIdShort() {
		ISubmodel retrieved = getSubmodelAggregator().getSubmodelbyIdShort(sm1.getIdShort());
		
		assertEquals(sm1, retrieved);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getNonExistingSubmodelByIdShort() {
		getSubmodelAggregator().getSubmodelbyIdShort("nonExisting");
	}


	@Test
	public void getSubmodelByIdentifier() {
		ISubmodel retrieved = getSubmodelAggregator().getSubmodel(sm1.getIdentification());

		assertEquals(sm1, retrieved);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void getNonExistingSubmodelByIdentifier() {
		getSubmodelAggregator().getSubmodel(new CustomId("nonExisting"));
	}

	@Test
	public void getAllSubmodels() {
		Collection<ISubmodel> submodels = getSubmodelAggregator().getSubmodelList();

		assertTrue(submodels.contains(sm1));
		assertTrue(submodels.contains(sm2));
	}

	@Test
	public void addSubmodel() {
		Submodel newSm = new Submodel("newSmIdShort", new CustomId("newSm"));
		getSubmodelAggregator().createSubmodel(newSm);

		ISubmodel retrieved = getSubmodelAggregator().getSubmodel(newSm.getIdentification());
		
		assertEquals(newSm, retrieved);
	}

	@Test
	public void updateSubmodel() {
		sm1.addSubmodelElement(new Property("prop1", 123));
		getSubmodelAggregator().updateSubmodel(sm1);

		ISubmodel retrieved = getSubmodelAggregator().getSubmodel(sm1.getIdentification());

		assertEquals(sm1, retrieved);
	}

	@Test
	public void deleteSubmodelByIdentifier() {
		Submodel smToDelete = createSubmodelToDelete();

		getSubmodelAggregator().deleteSubmodelByIdentifier(smToDelete.getIdentification());

		assertSubmodelisDeleted(smToDelete);
	}

	@Test
	public void deleteSubmodelByIdShort() {
		Submodel smToDelete = createSubmodelToDelete();

		getSubmodelAggregator().deleteSubmodelByIdShort(smToDelete.getIdShort());

		assertSubmodelisDeleted(smToDelete);
	}

	private void assertSubmodelisDeleted(Submodel smToDelete) {
		try {
			getSubmodelAggregator().getSubmodel(smToDelete.getIdentification());
			fail();
		} catch (ResourceNotFoundException expected) {
		}
	}

	private Submodel createSubmodelToDelete() {
		Submodel smToDelete = new Submodel("toDeleteIdShort", new CustomId("toDelete"));

		getSubmodelAggregator().createSubmodel(smToDelete);
		return smToDelete;
	}

	protected static void populateWithDefaultSubmodels(ISubmodelAggregator aggregator) {
		aggregator.createSubmodel(sm1);
		aggregator.createSubmodel(sm2);
	}
}
