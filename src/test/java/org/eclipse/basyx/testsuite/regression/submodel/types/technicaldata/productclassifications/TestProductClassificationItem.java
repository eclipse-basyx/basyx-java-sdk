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

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.productclassifications;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassificationItem;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassifications;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link ProductClassificationItem} for
 * their correctness
 * 
 * @author haque
 *
 */
public class TestProductClassificationItem {
	public static Property classificationSystem = new Property(ProductClassificationItem.PRODUCTCLASSIFICATIONSYSTEMID, ValueType.String);
	public static Property version = new Property(ProductClassificationItem.CLASSIFICATIONSYSTEMVERSIONID, ValueType.String);
	public static Property productClass = new Property(ProductClassificationItem.PRODUCTCLASSID, ValueType.String);

	private Map<String, Object> classificationMap = new LinkedHashMap<String, Object>();

	@Before
	public void init() {
		classificationSystem.setValue("ECLASS");
		version.setValue("9.0 (BASIC)");
		productClass.setValue("27-01-88-77");

		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(classificationSystem);
		elements.add(version);
		elements.add(productClass);

		classificationMap.put(Referable.IDSHORT, ProductClassifications.PRODUCTCLASSIFICATIONITEMPREFIX + "01");
		classificationMap.put(HasSemantics.SEMANTICID, ProductClassificationItem.SEMANTICID);
		classificationMap.put(Property.VALUE, elements);
	}

	@Test
	public void testCreateAsFacade() {
		ProductClassificationItem classificationFromMap = ProductClassificationItem.createAsFacade(classificationMap);
		assertEquals(ProductClassificationItem.SEMANTICID, classificationFromMap.getSemanticId());
		assertEquals(classificationSystem, classificationFromMap.getProductClassificationSystem());
		assertEquals(version, classificationFromMap.getClassificationSystemVersion());
		assertEquals(productClass, classificationFromMap.getProductClassId());
		assertEquals(ProductClassifications.PRODUCTCLASSIFICATIONITEMPREFIX + "01", classificationFromMap.getIdShort());
	}

	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		classificationMap.remove(Referable.IDSHORT);
		ProductClassificationItem.createAsFacade(classificationMap);
	}

	@Test(expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionProductClassificationSystem() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>) classificationMap.get(Property.VALUE);
		elements.remove(classificationSystem);
		ProductClassificationItem.createAsFacade(classificationMap);
	}

	@Test(expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionProductClassId() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>) classificationMap.get(Property.VALUE);
		elements.remove(productClass);
		ProductClassificationItem.createAsFacade(classificationMap);
	}
}
