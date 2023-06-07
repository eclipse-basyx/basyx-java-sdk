/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.entity;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.entity.ConnectedEntity;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.EntityValue;
import org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected.submodelelement.SubmodelElementTestHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedEntity can be created and used correctly
 * 
 * @author schnicke
 *
 */
public class TestConnectedEntity {
	private ConnectedEntity connectedEntity;
	private Entity entity;

	@Before
	public void build() {
		entity = new Entity("entityIdShort", EntityType.COMANAGEDENTITY);

		VABElementProxy elementProxy = SubmodelElementTestHelper.createElementProxy(entity);

		connectedEntity = new ConnectedEntity(elementProxy);
	}

	@Test
	public void testGetValue() {
		assertEquals(entity.getValue(), connectedEntity.getValue());
	}


	@Test
	public void testSetValue() {
		EntityValue value = createExpectedEntityValue();

		connectedEntity.setValue(value);

		assertEquals(value, connectedEntity.getValue());
	}

	private EntityValue createExpectedEntityValue() {
		Property property = new Property("prop", 5);
		Reference reference = new Reference(new Key(KeyElements.GLOBALREFERENCE, false, "test", IdentifierType.CUSTOM));
		
		return new EntityValue(Collections.singleton(property), reference);
	}

}
