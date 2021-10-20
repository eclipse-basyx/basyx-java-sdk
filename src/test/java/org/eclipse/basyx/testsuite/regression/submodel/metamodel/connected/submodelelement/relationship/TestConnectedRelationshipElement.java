/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
