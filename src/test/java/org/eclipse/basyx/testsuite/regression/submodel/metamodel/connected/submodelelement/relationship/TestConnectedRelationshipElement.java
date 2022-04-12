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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.relationship;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship.ConnectedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedRelationshipElement can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedRelationshipElement {
	
	ConnectedRelationshipElement connectedRelElem;
	RelationshipElement relElem;
	
	@Before
	public void build() {
		
		Reference ref1 = new Reference(new Key(KeyElements.BLOB, true, "1", IdentifierType.CUSTOM));
		Reference ref2 = new Reference(new Key(KeyElements.FILE, false, "2", IdentifierType.IRDI));
		
		relElem = new RelationshipElement("testId", ref1, ref2);
		
		
		VABConnectionManagerStub manager = new VABConnectionManagerStub(
				new SubmodelElementProvider(new TypeDestroyingProvider(new VABLambdaProvider(relElem))));

		connectedRelElem = new ConnectedRelationshipElement(manager.connectToVABElement(""));
	}

	/**
	 * Tests if getValue() returns the correct value
	 */
	@Test
	public void testGetValue() {
		RelationshipElementValue value = connectedRelElem.getValue();
		assertEquals(relElem.getFirst(), value.getFirst());
		assertEquals(relElem.getSecond(), value.getSecond());
	}

}
