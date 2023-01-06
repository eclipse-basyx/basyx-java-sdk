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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link OperationVariable} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestOperationVariable {
	private static final Property PROPERTY = new Property("testIdShort", "testOpVariable");

	private OperationVariable operationVariable;

	@Before
	public void buildOperationVariable() {
		PROPERTY.setKind(ModelingKind.TEMPLATE);
		operationVariable = new OperationVariable(PROPERTY);
	}

	@Test
	public void testConstructor() {
		assertEquals(PROPERTY, operationVariable.getValue());
	}

	@Test
	public void testSetValue() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setKind(ModelingKind.TEMPLATE);
		operationVariable.setValue(property);
		assertEquals(property, operationVariable.getValue());
	}

	@Test
	public void testSetValueChangedModelingKind() {
		Property property = new Property("testIdShort", ValueType.String);
		property.setKind(ModelingKind.INSTANCE);
		operationVariable.setValue(property);
		assertEquals(ModelingKind.TEMPLATE, operationVariable.getValue().getKind());
	}

	@Test
	public void internalSMCRepresentationIsCollection() {
		SubmodelElementCollection smc = createDummySMC();

		OperationVariable opVar = new OperationVariable(smc);

		assertSMCValueIsCollectionInternally(opVar);
	}

	@Test
	public void internalEntityStatementRepresentationIsCollection() {
		Entity entity = createDummyEntity();

		OperationVariable opVar = new OperationVariable(entity);

		assertSMCinEntityStatementIsCollectionInternally(opVar);
	}

	@SuppressWarnings("unchecked")
	private void assertSMCinEntityStatementIsCollectionInternally(OperationVariable opVar) {
		Map<String, Object> entityMap = (Map<String, Object>) opVar.get(Property.VALUE);
		Collection<Map<String, Object>> entityStatements = (Collection<Map<String, Object>>) entityMap.get(Entity.STATEMENT);

		Map<String, Object> smc = entityStatements.iterator().next();
		Object smcValue = smc.get(Property.VALUE);

		assertTrue(smcValue instanceof Collection);
	}

	private Entity createDummyEntity() {
		Entity entity = new Entity("entity", EntityType.COMANAGEDENTITY);
		entity.setStatements(Collections.singleton(createDummySMC()));

		return entity;
	}

	@SuppressWarnings("unchecked")
	private void assertSMCValueIsCollectionInternally(OperationVariable opVar) {
		Map<String, Object> smcMap = (Map<String, Object>) opVar.get(Property.VALUE);
		Object smcValue = smcMap.get(Property.VALUE);

		assertTrue(smcValue instanceof Collection);
	}

	private SubmodelElementCollection createDummySMC() {
		SubmodelElementCollection smc = new SubmodelElementCollection("collection");
		smc.addSubmodelElement(new Property("p1", 1));
		smc.addSubmodelElement(new Property("p2", 2));
		return smc;
	}
}
