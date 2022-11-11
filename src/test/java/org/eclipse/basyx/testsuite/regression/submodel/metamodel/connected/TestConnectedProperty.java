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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.connected;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.SubmodelElementProvider;
import org.eclipse.basyx.testsuite.regression.vab.coder.json.JSONConnectorFactory;
import org.eclipse.basyx.testsuite.regression.vab.manager.VABConnectionManagerStub;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
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
		prop = createConnectedProperty(propertyMeta);
	}

	@Test
	public void testEmptyProperty() throws Exception {
		Property propertyMeta = new Property("testIdShort", ValueType.String);
		propertyMeta.setValue(null);
		Map<String, Object> destroyType = TypeDestroyer.destroyType(propertyMeta);
		prop = new ConnectedProperty(new VABConnectionManagerStub(new SubmodelElementProvider(new VABMapProvider(destroyType))).connectToVABElement(""));
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
		assertEquals(ValueType.Int32, valueType);
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

	@Test
	public void testSetDate() throws DatatypeConfigurationException {
		Property dateProp = new Property("dateProp", getDummyDate());
		
		IProperty connectedDateProp = createConnectedProperty(dateProp);
		connectedDateProp.setValue(getDummyDate());

		assertEquals(getDummyDate(), connectedDateProp.getValue());
	}

	private IProperty createConnectedProperty(Property prop) {
		JSONConnector connector = new JSONConnectorFactory().create(new SubmodelElementProvider(new VABMapProvider(prop)));
		return new ConnectedProperty(new VABElementProxy("", connector));
	}

	private Object getDummyDate() throws DatatypeConfigurationException {
		XMLGregorianCalendar initDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		initDate.setYear(1900);
		initDate.setMonth(1);
		initDate.setDay(1);
		initDate.setHour(0);
		initDate.setMinute(0);
		initDate.setSecond(0);
		return initDate;
	}

}
