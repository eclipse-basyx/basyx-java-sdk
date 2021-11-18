/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.aggregator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Test;

/**
 * Test for the AASAggregationProvider
 * 
 * @author conradi, schnicke
 *
 */
public class TestAASAggregatorProxy extends AASAggregatorSuite {

	@Override
	protected IAASAggregator getAggregator() {
		return new AASAggregatorProxy(new VABElementProxy("", new AASAggregatorProvider(new AASAggregator())));
	}

	/**
	 * Requests like /shells/${aasId}/aas need to be fed through correctly. This
	 * behaviour is tested here.
	 */
	@Test
	public void testFeedThrough() throws Exception {
		IAASAggregator aggregator = getAggregator();
		aggregator.createAAS(aas1);

		// Test feedthrough of GET
		IAssetAdministrationShell retrievedAAS = aggregator.getAAS(aas1.getIdentification());
		assertEquals(aas1.getIdentification(), retrievedAAS.getIdentification());

		// Test feedthrough of CREATE
		Submodel sm = new Submodel();
		sm.setIdentification(IdentifierType.CUSTOM, "smId");
		sm.setIdShort("smIdShort");
		Operation op = new Operation((o) -> {
			return true;
		});
		op.setIdShort("op");
		sm.addSubmodelElement(op);

		Property prop = new Property(5);
		prop.setIdShort("prop");
		sm.addSubmodelElement(prop);

		retrievedAAS.addSubmodel(sm);

		// Check if it was created
		ISubmodel retrievedSm = retrievedAAS.getSubmodels().get(sm.getIdShort());
		assertEquals(sm.getIdShort(), retrievedSm.getIdShort());

		// Test feedthrough of SET
		int expectedPropValue = 20;
		IProperty connectedProp = (IProperty) retrievedSm.getSubmodelElement(prop.getIdShort());

		connectedProp.setValue(expectedPropValue);
		assertEquals(expectedPropValue, connectedProp.getValue());

		// Test feedthrough of INVOKE
		// Use short form of invoke with operation variable matching and no parameters (empty object array)
		assertTrue((boolean) ((IOperation) sm.getSubmodelElement(op.getIdShort())).invokeSimple(new Object[0]));

		// Test feedthrough of DELETE
		retrievedAAS.removeSubmodel(sm.getIdentification());

		// Ensure only the submodel has been deleted
		assertEquals(aas1.getIdentification(), retrievedAAS.getIdentification());

		assertFalse(retrievedAAS.getSubmodels().containsKey(sm.getIdShort()));
	}
}
