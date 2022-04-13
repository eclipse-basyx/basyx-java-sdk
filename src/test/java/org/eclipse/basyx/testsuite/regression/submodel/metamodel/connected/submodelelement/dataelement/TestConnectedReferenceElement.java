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

		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(refElem);

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
