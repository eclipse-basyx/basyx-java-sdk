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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link AdministrativeInformation} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestAdministrativeInformation {
	private static final String VERSION = "1.0";
	private static final String REVISION = "5";
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, VALUE);
	private static final Reference REFERENCE = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	
	private AdministrativeInformation information;
	
	@Before
	public void buildAdministrativeInformation() {
		information = new AdministrativeInformation(VERSION, REVISION);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(VERSION, information.getVersion());
		assertEquals(REVISION, information.getRevision());
		assertEquals(new HashSet<IReference>(), information.getDataSpecificationReferences());
	}
	
	@Test
	public void testSetDataSpecificationReferences() {
		Collection<IReference> references = Collections.singleton(REFERENCE);
		information.setDataSpecificationReferences(references);
		assertEquals(references, information.getDataSpecificationReferences());
	}
	
	@Test
	public void testSetVersionInformation() {
		String newVersionString = "2.0";
		String newRevisionString = "2.0.1";
		information.setVersionInformation(newVersionString, newRevisionString);
		assertEquals(newVersionString, information.getVersion());
		assertEquals(newRevisionString, information.getRevision());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetVersionInformationExceptionEmptyString() {
		String newVersionString = "";
		String newRevisionString = "2.0.1";
		information.setVersionInformation(newVersionString, newRevisionString);
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetVersionInformationExceptionNullString() {
		String newVersionString = null;
		String newRevisionString = "2.0.1";
		information.setVersionInformation(newVersionString, newRevisionString);
	}
	
	@Test
	public void testSetVersionInformationNoRevision() {
		String newVersionString = "";
		String newRevisionString = "";
		information.setVersionInformation(newVersionString, newRevisionString);
		assertEquals(newVersionString, information.getVersion());
		assertEquals(newRevisionString, information.getRevision());
	}
	
	@Test
	public void testSetEmbeddedDataSpecifications() {
		EmbeddedDataSpecification embeddedDataSpecification = new EmbeddedDataSpecification();
		Collection<IEmbeddedDataSpecification> specifications = Collections.singleton(embeddedDataSpecification);
		information.setEmbeddedDataSpecifications(specifications);
		assertEquals(specifications, information.getEmbeddedDataSpecifications());
	}
}
