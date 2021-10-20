/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.productclassifications;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassificationItem;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassifications;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link ProductClassifications} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestProductClassifications {
	public static ProductClassificationItem productClassificationItem = new ProductClassificationItem(ProductClassifications.PRODUCTCLASSIFICATIONITEMPREFIX + "01", TestProductClassificationItem.classificationSystem, TestProductClassificationItem.productClass);
	
	private Map<String, Object> classificationMap = new HashMap<String, Object>();
	
	@Before
	public void init() {
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(productClassificationItem);
		
		classificationMap.put(Referable.IDSHORT, ProductClassifications.IDSHORT);
		classificationMap.put(HasSemantics.SEMANTICID, ProductClassifications.SEMANTICID);
		classificationMap.put(Property.VALUE, elements);
	}
	
	@Test
	public void testCreateAsFacade() {
		ProductClassifications classificationFromMap = ProductClassifications.createAsFacade(classificationMap);
		assertEquals(ProductClassifications.SEMANTICID, classificationFromMap.getSemanticId());
		assertEquals(ProductClassifications.IDSHORT, classificationFromMap.getIdShort());
		assertEquals(Collections.singletonList(productClassificationItem), classificationFromMap.getProductClassificationItems());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		classificationMap.remove(Referable.IDSHORT);
		ProductClassifications.createAsFacade(classificationMap);
	}
}
