/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.types.digitalnameplate.submodelelementcollections.address;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.digitalnameplate.enums.FaxType;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Fax;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link Fax} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestFax {
	public static final String IDSHORT = "testFaxId";
	public static MultiLanguageProperty faxNumber = new MultiLanguageProperty(Fax.FAXNUMBERID);
	public static Property typeOfFax = new Property(Fax.TYPEOFFAXID, ValueType.String);

	private Map<String, Object> faxMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void buildFax() {
		faxNumber.setValue(new LangStrings(new LangString("DE", "0631123456")));
		typeOfFax.setValue(FaxType.HOME);
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(faxNumber);
		elements.add(typeOfFax);
		faxMap.put(Referable.IDSHORT, IDSHORT);
		faxMap.put(HasSemantics.SEMANTICID, Fax.SEMANTICID);
		faxMap.put(Property.VALUE, elements);
	}

	@Test
	public void testCreateAsFacade() {
		Fax faxFromMap = Fax.createAsFacade(faxMap);
		assertEquals(Fax.SEMANTICID, faxFromMap.getSemanticId());
		assertEquals(faxNumber, faxFromMap.getFaxNumber());
		assertEquals(typeOfFax, faxFromMap.getTypeOfFaxNumber());
		assertEquals(IDSHORT, faxFromMap.getIdShort());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		faxMap.remove(Referable.IDSHORT);
		Fax.createAsFacade(faxMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionFaxNumber() {
		List<ISubmodelElement> newElements = new ArrayList<ISubmodelElement>();
		newElements.add(typeOfFax);
		faxMap.put(Property.VALUE, newElements);
		Fax.createAsFacade(faxMap);
	}
}
