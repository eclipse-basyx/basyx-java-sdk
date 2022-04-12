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
