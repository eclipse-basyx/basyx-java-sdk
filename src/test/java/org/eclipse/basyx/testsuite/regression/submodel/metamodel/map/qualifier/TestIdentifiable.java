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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Identifiable} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestIdentifiable {
	private static final String VERSION = "1.0";
	private static final String REVISION = "5";
	private static final String CATE_STRING = "testCategory";
	private static final String ID_SHORT_STRING = "testIdShort";
	private static final LangStrings DESCRIPTION = new LangStrings("Eng", "test");
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	
	private Identifiable identifiable;
	
	@Before
	public void buildIdentifiable() {
		identifiable = new Identifiable(VERSION, REVISION, ID_SHORT_STRING, CATE_STRING, DESCRIPTION, ID_TYPE, ID_SHORT_STRING);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(ID_SHORT_STRING, identifiable.getIdShort());
		assertEquals(CATE_STRING, identifiable.getCategory());
		assertEquals(new AdministrativeInformation(VERSION, REVISION), identifiable.getAdministration());
		assertEquals(new Identifier(ID_TYPE, ID_SHORT_STRING), identifiable.getIdentification());
	}
	
	@Test
	public void testSetAdministration() {
		String newVersion = "2.0";
		String newRevision = "2.0.1";
		AdministrativeInformation information = new AdministrativeInformation(newVersion, newRevision);
		identifiable.setAdministration(information);
		assertEquals(information, identifiable.getAdministration());
	}
	
	@Test
	public void testSetIdentification() {
		IdentifierType idType = IdentifierType.IRI;
		String newIdString = "newId";
		identifiable.setIdentification(idType, newIdString);
		assertEquals(new Identifier(idType, newIdString), identifiable.getIdentification());
	}
}
