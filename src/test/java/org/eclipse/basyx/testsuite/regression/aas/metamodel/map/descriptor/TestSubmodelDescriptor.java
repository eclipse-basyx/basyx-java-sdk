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

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link SubmodelDescriptor} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestSubmodelDescriptor extends ModelDescriptorTestSuite {
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final String HTTP_ENDPOINT = "testEnd/submodel";
	private static final String ID_SHORT_STRING = "testIdShort";
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, ID_SHORT_STRING);

	@Test
	public void testConstructor1() {
		ModelingKind modelingKind = ModelingKind.INSTANCE;
		HasKind hasKind = new HasKind(modelingKind);
		KeyElements keyElements = KeyElements.ASSET;
		boolean isLocal = false;
		Reference reference = new Reference(IDENTIFIER, keyElements, isLocal);
		HasSemantics hasSemantics = new HasSemantics(reference);
		String version = "1.0";
		String revision = "5";
		String category = "testCategory";
		LangStrings description = new LangStrings("Eng", "test");
		Identifiable identifiable = new Identifiable(version, revision, ID_SHORT_STRING, category, description, ID_TYPE, ID_SHORT_STRING);
		Formula formula = new Formula(Collections.singleton(new Reference(new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI))));
		Qualifiable qualifiable = new Qualifiable(formula);
		HasDataSpecification hasDataSpecification = new HasDataSpecification(new ArrayList<>(), Collections.singleton(reference));
		Submodel subModel = new Submodel(hasSemantics, identifiable, qualifiable, hasDataSpecification, hasKind);

		SubmodelDescriptor descriptor = new SubmodelDescriptor(subModel, HTTP_ENDPOINT);
		assertEquals(HTTP_ENDPOINT, descriptor.getFirstEndpoint());
		assertEquals(ID_SHORT_STRING, descriptor.getIdShort());
		assertEquals(IDENTIFIER, descriptor.getIdentifier());
	}

	@Test
	public void testConstructor2() {
		SubmodelDescriptor descriptor = new SubmodelDescriptor(ID_SHORT_STRING, IDENTIFIER, HTTP_ENDPOINT);
		assertEquals(HTTP_ENDPOINT, descriptor.getFirstEndpoint());
		assertEquals(ID_SHORT_STRING, descriptor.getIdShort());
		assertEquals(IDENTIFIER, descriptor.getIdentifier());
	}

	@Override
	public ModelDescriptor retrieveModelDescriptor() {
		return new SubmodelDescriptor(ID_SHORT_STRING, IDENTIFIER, HTTP_ENDPOINT);
	}
}
