/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.markings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings.Marking;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.markings.Markings;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;

/**
 * Tests createAsFacade and isValid of {@link Markings} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestMarkings {
	public static final String IDSHORT = "Markings";
	public static List<Marking> markings;
	public static Marking marking;

	private Map<String, Object> markingsMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void initMarkings() {
		TestMarking.markingFile.setIdShort(Marking.MARKINGFILEID);
		TestMarking.markingName.setValue("0173-1#07-DAA603#004");
		marking = new Marking(TestMarking.IDSHORT, TestMarking.markingName, TestMarking.markingFile);
		marking.setParent(new Reference(new Key(KeyElements.SUBMODELELEMENTCOLLECTION, true, IDSHORT, IdentifierType.IRDI)));
		markings = new ArrayList<Marking>();
		markings.add(marking);
		
		markingsMap.put(Referable.IDSHORT, IDSHORT);
		markingsMap.put(Property.VALUE, markings);
		markingsMap.put(HasSemantics.SEMANTICID, Markings.SEMANTICID);
	}
	
	@Test
	public void testCreateAsFacade() {
		Markings markingsFromMap = Markings.createAsFacade(markingsMap);
		assertEquals(Markings.SEMANTICID, markingsFromMap.getSemanticId());
		assertEquals(markings, markingsFromMap.getMarking());
		assertEquals(IDSHORT, markingsFromMap.getIdShort());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		markingsMap.remove(Referable.IDSHORT);
		Markings.createAsFacade(markingsMap);
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionNullMarkings() {
		markingsMap.remove(Property.VALUE);
		Markings.createAsFacade(markingsMap);
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionEmptyMarkings() {
		markingsMap.put(Property.VALUE, new ArrayList<Marking>());
		Markings.createAsFacade(markingsMap);
	}
}
