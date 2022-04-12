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

package org.eclipse.basyx.testsuite.regression.submodel.types.technicaldata.generalinformation;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.generalinformation.GeneralInformation;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link GeneralInformation} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestGeneralInformation {
	public static Property manufacturerName = new Property(GeneralInformation.MANUFACTURERNAMEID, ValueType.String);
	public static File manufacturerLogo = new File("image/png");
	public static MultiLanguageProperty designation = new MultiLanguageProperty(GeneralInformation.MANUFACTURERPRODUCTDESIGNATIONID);
	public static Property partNumber = new Property(GeneralInformation.MANUFACTURERPARTNUMBERID, ValueType.String);
	public static Property orderCode = new Property(GeneralInformation.MANUFACTURERORDERCODEID, ValueType.String);
	public static File image = new File("image/jpg");
	
	private Map<String, Object> InfoMap = new LinkedHashMap<String, Object>();
	
	@Before
	public void init() {
		manufacturerName.setValue("Example Company");
		manufacturerLogo.setIdShort(GeneralInformation.MANUFACTURERLOGOID);
		manufacturerLogo.setValue("/aasx/TechnicalData/logo.png");
		designation.setValue(new LangStrings(new LangString("de", "Elektrischer Energie Beschleuniger")));
		partNumber.setValue("A123-456");
		orderCode.setValue("EEA-EX-200-S/47-Q3");
		image.setIdShort(GeneralInformation.PRODUCTIMAGEPREFIX + "01");
		image.setValue("/aasx/TechnicalData/ ProdFromTop.jpg");
		
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(manufacturerName);
		elements.add(manufacturerLogo);
		elements.add(designation);
		elements.add(partNumber);
		elements.add(orderCode);
		elements.add(image);
		
		InfoMap.put(Referable.IDSHORT, GeneralInformation.IDSHORT);
		InfoMap.put(HasSemantics.SEMANTICID, GeneralInformation.SEMANTICID);
		InfoMap.put(Property.VALUE, elements);
	}
	
	@Test
	public void testCreateAsFacade() {
		GeneralInformation infoFromMap = GeneralInformation.createAsFacade(InfoMap);
		assertEquals(GeneralInformation.SEMANTICID, infoFromMap.getSemanticId());
		assertEquals(Collections.singletonList(image), infoFromMap.getProductImages());
		assertEquals(manufacturerName, infoFromMap.getManufacturerName());
		assertEquals(manufacturerLogo, infoFromMap.getManufacturerLogo());
		assertEquals(designation, infoFromMap.getManufacturerProductDesignation());
		assertEquals(partNumber, infoFromMap.getManufacturerPartNumber());
		assertEquals(orderCode, infoFromMap.getManufacturerOrderCode());
		assertEquals(GeneralInformation.IDSHORT, infoFromMap.getIdShort());
	}
	
	@Test (expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		InfoMap.remove(Referable.IDSHORT);
		GeneralInformation.createAsFacade(InfoMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionName() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)InfoMap.get(Property.VALUE);
		elements.remove(manufacturerName);
		GeneralInformation.createAsFacade(InfoMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionDesignation() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)InfoMap.get(Property.VALUE);
		elements.remove(designation);
		GeneralInformation.createAsFacade(InfoMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionPartNumber() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)InfoMap.get(Property.VALUE);
		elements.remove(partNumber);
		GeneralInformation.createAsFacade(InfoMap);
	}
	
	@Test (expected = ResourceNotFoundException.class)
	@SuppressWarnings("unchecked")
	public void testCreateAsFacadeExceptionOrderCode() {
		List<ISubmodelElement> elements = (List<ISubmodelElement>)InfoMap.get(Property.VALUE);
		elements.remove(orderCode);
		GeneralInformation.createAsFacade(InfoMap);
	}
}
