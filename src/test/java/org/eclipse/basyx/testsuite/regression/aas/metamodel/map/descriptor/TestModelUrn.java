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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.descriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
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
