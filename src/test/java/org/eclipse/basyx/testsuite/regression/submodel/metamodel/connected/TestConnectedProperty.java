/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if a ConnectedSingleProperty can be created and used correctly
 * 
 * @author schnicke
 *
 */
public class TestConnectedProperty {

	IProperty prop;
	private static final int VALUE = 10;
	private static final Reference VALUEID = new Reference(new Key(KeyElements.ACCESSPERMISSIONRULE, true, "testValue", IdentifierType.CUSTOM));

	@Before
	public void build() {
		// Create PropertySingleValued containing the simple value
		Property propertyMeta = new Property("testProp", VALUE);
		propertyMeta.setValueId(VALUEID);
		Map<String, Object> destroyType = TypeDestroyer.destroyType(propertyMeta);
		prop = new ConnectedProperty(new VABConnectionManagerStub(new SubmodelElementProvider(new VABMapProvider(destroyType))).connectToVABElement(""));
	}

	@Test
	public void testEmptyProperty() throws Exception {
		Property propertyMeta = new Property("testIdShort", ValueType.String);
		propertyMeta.setValue(null);
		Map<String, Object> destroyType = TypeDestroyer.destroyType(propertyMeta);
		prop = new ConnectedProperty(
				new VABConnectionManagerStub(new SubmodelElementProvider(new VABMapProvider(destroyType)))
						.connectToVABElement(""));
		prop.setValue("content");
		assertEquals("content", prop.getValue());
	}

	/**
	 * Tests getting the value
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGet() throws Exception {
		int val = (int) prop.getValue();
		assertEquals(VALUE, val);
	}

	/**
	 * Tests if the value type can be correctly retrieved
	 * 
	 * @throws Exception
	 */
	@Test
	public void testValueTypeRetrieval() {
		ValueType valueType = prop.getValueType();
		assertEquals(ValueType.Integer, valueType);
	}

	/**
	 * Tests setting the value
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSet() throws Exception {
		prop.setValue(123);
		int val = (int) prop.getValue();
		assertEquals(123, val);
	}

	@Test
	public void testGetValueId() {
		assertEquals(VALUEID, prop.getValueId());
	}

}
