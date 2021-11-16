/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.dataelement;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedReferenceElement can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedReferenceElement {
	
	ConnectedReferenceElement connectedRefElem;
	ReferenceElement refElem;
	
	@Before
	public void build() {
		
		Reference ref = new Reference(new Key(KeyElements.BLOB, true, "", IdentifierType.CUSTOM));
		
		refElem = new ReferenceElement(ref);
		
		VABElementProxy elementProxy = SubmodelElementTestHelper.provideElementProxy(refElem);

		connectedRefElem = new ConnectedReferenceElement(elementProxy);
	}
	
	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		assertEquals(refElem.getValue(), connectedRefElem.getValue());
	}

	@Test
	public void setValueUpdatesValueCorrectly() {
		triggerCachingOfSubmodelElement();

		IReference expected = new Reference(new Key(KeyElements.ASSET, true, "asset", IdentifierType.CUSTOM));

		connectedRefElem.setValue(expected);
		
		assertEquals(expected, connectedRefElem.getValue());
	}

	private void triggerCachingOfSubmodelElement() {
		connectedRefElem.getElem();
	}
}
