/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.dataspecification;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.ValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor and getter of {@link ValueReferencePair} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestValueReferencePair {
	private static final String VALUE = "testValue";
	private static final IReference VALUE_ID = new Reference(new Key(KeyElements.ASSET, true, "testValue", IdentifierType.IRDI));
	
	private ValueReferencePair valueReferencePair;
	
	@Before
	public void buildValueReferencePair() {
		valueReferencePair = new ValueReferencePair(VALUE, VALUE_ID);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(VALUE, valueReferencePair.getValue());
		assertEquals(VALUE_ID, valueReferencePair.getValueId());
	} 
	
	@Test
	public void testSetValue() {
		String newValue = "testValue1";
		valueReferencePair.setValue(newValue);
		assertEquals(newValue, valueReferencePair.getValue());
	} 
	
	@Test
	public void testSetValueId() {
		IReference newValueId = new Reference(new Key(KeyElements.BLOB, false, "testValueNew", IdentifierType.IRI));
		valueReferencePair.setValueId(newValueId);
		assertEquals(newValueId, valueReferencePair.getValueId());
	} 
}
