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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Test;

/**
 * Tests for SubmodelElementMapCollectionConverter
 * 
 * @author conradi
 *
 */
public class TestSubmodelElementMapCollectionConverter {

	private static final String ID_SHORT = "testElement";

	@Test
	public void testMapToSM() {
		Submodel sm = getSM();

		// Replace the smElement Map with a Collection
		sm.put(Submodel.SUBMODELELEMENT, sm.getSubmodelElements().values());

		// Make a Map from the SM, as if it was transferred over the VAB
		Map<String, Object> map = TypeDestroyer.destroyType(sm);

		sm = SubmodelElementMapCollectionConverter.mapToSM(map);

		assertTrue(sm.get(Submodel.SUBMODELELEMENT) instanceof Map<?, ?>);

		assertNotNull(sm.getSubmodelElements().get(ID_SHORT));
		assertTrue(sm.getSubmodelElements().get(ID_SHORT) instanceof Property);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSMToMap() {
		Submodel sm = getSM();

		Map<String, Object> map = SubmodelElementMapCollectionConverter.smToMap(sm);

		assertTrue(map.get(Submodel.SUBMODELELEMENT) instanceof Collection<?>);
		assertEquals(1, ((Collection<ISubmodelElement>) map.get(Submodel.SUBMODELELEMENT)).size());
	}

	@Test
	public void entityStatementContainingSMCConverted() {
		Entity entity = createTestEntityWithSMCAsStatement();

		Map<String, Object> convertedEntity = SubmodelElementMapCollectionConverter.smElementToMap(entity);

		assertEntityCorrectlyConverted(convertedEntity);
	}

	@SuppressWarnings("unchecked")
	private void assertEntityCorrectlyConverted(Map<String, Object> convertedEntity) {
		List<Map<String, Object>> convertedStatements = (List<Map<String, Object>>) convertedEntity.get(Entity.STATEMENT);

		Map<String, Object> convertedSMC = convertedStatements.get(0);
		Object convertedSMCValue = convertedSMC.get(Property.VALUE);
		assertTrue(convertedSMCValue instanceof List<?>);
	}

	private Entity createTestEntityWithSMCAsStatement() {
		Entity entity = new Entity("entity", EntityType.COMANAGEDENTITY);

		SubmodelElementCollection smc = new SubmodelElementCollection("smc");

		Property prop = new Property("prop", ValueType.String);
		smc.addSubmodelElement(prop);

		Collection<ISubmodelElement> statements = new HashSet<>();
		statements.add(smc);
		entity.setStatements(statements);

		return entity;
	}

	private Submodel getSM() {
		Submodel sm = new Submodel("submodelIdShort", new ModelUrn("submodelUrn"));
		Property property = new Property(ID_SHORT, ValueType.String);

		sm.addSubmodelElement(property);
		return sm;
	}

}
