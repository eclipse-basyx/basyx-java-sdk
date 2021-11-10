/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.registry.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.registry.descriptor.ModelUrn;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link ModelUrn} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestModelUrn {
	private static final String rawURN = "testRawUrn";
	
	@Test
	public void testConstructor1() {
		ModelUrn modelUrn = new ModelUrn(rawURN);
		assertEquals(rawURN, modelUrn.getURN());
	}
	
	@Test
	public void testConstructor2() {
		String legalEntity = "testLegalEntity";
		String subUnit = "testSubUnit";
		String subModel = "testSubmodel";
		String version = "1.0";
		String revision = "5";
		String elementId = "testId";
		String elementInstance = "testInstance";
		
		ModelUrn modelUrn = new ModelUrn(legalEntity, subUnit, subModel, version, revision, elementId, elementInstance);
		String appendedString = "urn:" + legalEntity + ":" + subUnit + ":" + subModel + ":" + version + ":" + revision + ":" + elementId + "#"+ elementInstance;
		assertEquals(appendedString, modelUrn.getURN());
		assertEquals(IdentifierType.IRI, modelUrn.getIdType());
	}
	
	@Test
	public void testAppend() {
		String suffix = "testSuffix";
		ModelUrn modelUrn = new ModelUrn(rawURN);
		ModelUrn newModelUrn = modelUrn.append(suffix);
		assertEquals(rawURN + suffix, newModelUrn.getURN());
	}

	@Test
	public void testValidIdentifier() {
		ModelUrn modelUrn = new ModelUrn(rawURN);
		assertTrue(Identifier.isValid(modelUrn));
	}
}
