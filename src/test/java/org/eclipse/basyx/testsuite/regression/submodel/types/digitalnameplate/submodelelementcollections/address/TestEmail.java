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
import org.eclipse.basyx.submodel.types.digitalnameplate.enums.MailType;
import org.eclipse.basyx.submodel.types.digitalnameplate.submodelelementcollections.address.Email;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests createAsFacade and isValid of {@link Email} for their correctness
 * 
 * @author haque
 *
 */
public class TestEmail {
	public static final String IDSHORT = "testEmailId";
	public static Property emailAddress = new Property(Email.EMAILADDRESSID, ValueType.String);
	public static MultiLanguageProperty publicKey = new MultiLanguageProperty(Email.PUBLICKEYID);
	public static Property typeOfEmailAddress = new Property(Email.TYPEOFEMAILADDRESSID, ValueType.String);
	public static MultiLanguageProperty typeOfPublicKey = new MultiLanguageProperty(Email.TYPEOFPUBLICKEYID);

	private Map<String, Object> emailMap = new LinkedHashMap<String, Object>();

	@Before
	public void buildFax() {
		emailAddress.setValue("test@muster-ag.de");
		publicKey.setValue(new LangStrings(new LangString("DE", "123456")));
		typeOfEmailAddress.setValue(MailType.SECRETARY);
		typeOfPublicKey.setValue(new LangStrings(new LangString("DE", "1234")));
		List<ISubmodelElement> elements = new ArrayList<ISubmodelElement>();
		elements.add(emailAddress);
		elements.add(publicKey);
		elements.add(typeOfEmailAddress);
		elements.add(typeOfPublicKey);
		emailMap.put(Referable.IDSHORT, IDSHORT);
		emailMap.put(HasSemantics.SEMANTICID, Email.SEMANTICID);
		emailMap.put(Property.VALUE, elements);
	}

	@Test
	public void testCreateAsFacade() {
		Email emailFromMap = Email.createAsFacade(emailMap);
		assertEquals(Email.SEMANTICID, emailFromMap.getSemanticId());
		assertEquals(emailAddress, emailFromMap.getEmailAddress());
		assertEquals(publicKey, emailFromMap.getPublicKey());
		assertEquals(typeOfEmailAddress, emailFromMap.getTypeOfEmailAddress());
		assertEquals(typeOfPublicKey, emailFromMap.getTypeOfPublicKey());
		assertEquals(IDSHORT, emailFromMap.getIdShort());
	}

	@Test(expected = MetamodelConstructionException.class)
	public void testCreateAsFacadeExceptionIdShort() {
		emailMap.remove(Referable.IDSHORT);
		Email.createAsFacade(emailMap);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testCreateAsFacadeExceptionEmailAddress() {
		List<ISubmodelElement> newElements = new ArrayList<ISubmodelElement>();
		newElements.add(typeOfEmailAddress);
		emailMap.put(Property.VALUE, newElements);
		Email.createAsFacade(emailMap);
	}
}
