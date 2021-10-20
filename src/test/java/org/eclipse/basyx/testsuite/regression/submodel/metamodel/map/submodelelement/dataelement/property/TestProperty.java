/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.dataelement.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests constructor, getter and setter of {@link Property} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestProperty {
	private static final String VALUE = "testValue";
	private static final ValueType STRING_TYPE = ValueType.String;
	private Property property;

	@Before
	public void buildFile() {
		property = new Property(VALUE);
	}
	
	@Test
	public void testConstructor1(){
		assertEquals(VALUE, property.getValue());
		assertNull(property.getValueId());
		assertEquals(STRING_TYPE, property.getValueType());
	} 
	
	@Test
	public void testConstructor2(){
		Referable referable = new Referable("testIdShort", "testCategory", new LangStrings("DE", "test"));
		Reference semanticId = new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRI));
		Qualifiable qualifiable = new Qualifiable(new Formula(Collections.singleton(new Reference(new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI)))));
		Property property = new Property(VALUE, referable, semanticId, qualifiable);
		assertEquals(VALUE, property.getValue());
		assertNull(property.getValueId());
		assertEquals(STRING_TYPE, property.getValueType());
	} 
	
	@Test
	public void testSetValueType() {
		property.setValueType(ValueType.String);
		assertEquals(STRING_TYPE, property.getValueType());
	} 
	
	@Test
	public void testSet() throws DatatypeConfigurationException{
		Property booleanProp = new Property();
		Boolean isSomething = true;
		booleanProp.setValue(isSomething);
		assertEquals(isSomething, booleanProp.getValue());
		assertEquals(ValueType.Boolean, booleanProp.getValueType());

		Byte byteNumber = new Byte("2");
		Property byteProp = new Property();
		byteProp.setValue(byteNumber);
		assertEquals(byteNumber, byteProp.getValue());
		assertEquals(ValueType.Int8, byteProp.getValueType());
		
		Duration duration = Duration.ofSeconds(10);
		Property durationProp = new Property();
		durationProp.setValue(duration);
		assertEquals(duration, durationProp.getValue());
		assertEquals(ValueType.Duration, durationProp.getValueType());

		Property periodProp = new Property();
		LocalDate today = LocalDate.now();
		LocalDate birthday = LocalDate.of(1960, Month.JANUARY, 1);
		Period p = Period.between(birthday, today);
		periodProp.setValue(p);
		assertEquals(p, periodProp.getValue());
		assertEquals(ValueType.YearMonthDuration, periodProp.getValueType());

		Property bigNumberProp = new Property();
		BigInteger bignumber = new BigInteger("9223372036854775817");
		bigNumberProp.setValue(bignumber);
		assertEquals(bignumber, bigNumberProp.getValue());
		assertEquals(ValueType.PositiveInteger, bigNumberProp.getValueType());
		
		Property dateProp = new Property();
		GregorianCalendar dateValue = GregorianCalendar.from(ZonedDateTime.of(birthday, LocalTime.MIDNIGHT, ZoneId.of("UTC")));
		XMLGregorianCalendar xmlDateValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateValue);
		dateProp.setValue(xmlDateValue);
		assertEquals("1960-01-01T00:00:00.000Z", dateProp.get(Property.VALUE));
		assertEquals(xmlDateValue, dateProp.getValue());
	}

	@Test
	public void testSetCustom(){
		property.set(null, ValueType.String);
		assertEquals(null, property.getValue());
		assertEquals(ValueType.String, property.getValueType());
	}

	@Test
	public void testSetId() {
		IReference ref = new Reference(new Key(KeyElements.PROPERTY, true, "custom", IdentifierType.CUSTOM));
		IReference ref2 = new Reference(new Key(KeyElements.PROPERTY, true, "custom", IdentifierType.CUSTOM));
		property.setValueId(ref);
		assertEquals(ref2, property.getValueId());
	}

	@Test
	public void testAddConceptDescription() {
		String id = "idShort";
		ConceptDescription description = new ConceptDescription(id, new Identifier(IdentifierType.IRDI, id));
		Property property = new Property(VALUE);
		property.addConceptDescription(description);
		assertEquals(new Reference(description, KeyElements.CONCEPTDESCRIPTION, true), property.getSemanticId());
	}
	
	@Test
	public void testInitializeWithNullValue() {
		try {
			// Should not work as valueType is a mandatory attribute
			new Property("id", null);
			fail();
		} catch (RuntimeException e) {
		}
		
		try {
			// Should not work as valueType can not be set with null as value
			Property prop = new Property();
			prop.setValue(null);
			fail();
		} catch (RuntimeException e) {
		}
		
		Property prop = new Property("id", "value");
		// This should work as the valueType is already set
		prop.setValue(null);
	}
}
