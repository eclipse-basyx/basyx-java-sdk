/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.event;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.event.ConnectedBasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.support.TypeDestroyingProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedBasicEvent can be created and used correctly
 * 
 * @author conradi
 *
 */
public class TestConnectedBasicEvent {

	ConnectedBasicEvent connectedEvent;
	BasicEvent event;
	
	@Before
	public void build() {
		
		Reference ref = new Reference(new Key(KeyElements.BLOB, true, "", IdentifierType.CUSTOM));
		
		event = new BasicEvent(ref);
		
		VABConnectionManagerStub manager = new VABConnectionManagerStub(
				new SubmodelElementProvider(new TypeDestroyingProvider(new VABLambdaProvider(event))));

		connectedEvent = new ConnectedBasicEvent(manager.connectToVABElement(""));
	}
	
	/**
	 * Tests if getObserved() returns the correct value
	 */
	@Test
	public void testGetObserved() {
		assertEquals(event.getObserved(), connectedEvent.getObserved());
	}

}
