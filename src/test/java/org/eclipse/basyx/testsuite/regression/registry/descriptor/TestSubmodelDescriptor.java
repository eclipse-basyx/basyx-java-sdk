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

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.basyx.registry.descriptor.ModelDescriptor;
import org.eclipse.basyx.registry.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
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
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link SubmodelDescriptor} for their
 * correctness
 *
 * @author haque, fischer
 *
 */
public class TestSubmodelDescriptor extends ModelDescriptorTestSuite {
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final String HTTP_ENDPOINT = "testEnd/submodel";
	private static final String ID_SHORT_STRING = "testIdShort";
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, ID_SHORT_STRING);
	private static Endpoint httpEndpoint;

	@Before
	public void initialize() {
		httpEndpoint = new Endpoint(HTTP_ENDPOINT);
	}

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

		SubmodelDescriptor descriptor = new SubmodelDescriptor(subModel, httpEndpoint);
		assertEquals(HTTP_ENDPOINT, descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
		assertEquals(ID_SHORT_STRING, descriptor.getIdShort());
		assertEquals(IDENTIFIER, descriptor.getIdentifier());
	}

	@Test
	public void testConstructor2() {
		SubmodelDescriptor descriptor = new SubmodelDescriptor(ID_SHORT_STRING, IDENTIFIER, httpEndpoint);
		assertEquals(HTTP_ENDPOINT, descriptor.getFirstEndpoint().getProtocolInformation().getEndpointAddress());
		assertEquals(ID_SHORT_STRING, descriptor.getIdShort());
		assertEquals(IDENTIFIER, descriptor.getIdentifier());
	}

	@Override
	public ModelDescriptor retrieveModelDescriptor() {
		return new SubmodelDescriptor(ID_SHORT_STRING, IDENTIFIER, httpEndpoint);
	}
}
