/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
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


	private Submodel getSM() {
		Submodel sm = new Submodel("submodelIdShort", new ModelUrn("submodelUrn"));
		Property property = new Property(ID_SHORT, ValueType.String);
		
		sm.addSubmodelElement(property);
		return sm;
	}
	
	
}
