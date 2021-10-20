/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
