/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier.qualifiable;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Formula} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestFormula {
	private static final KeyElements KEY_ELEMENTS = KeyElements.ASSET;
	private static final boolean IS_LOCAL = false;
	private static final String VALUE = "testValue";
	private static final IdentifierType ID_TYPE = IdentifierType.CUSTOM;
	private static final Identifier IDENTIFIER = new Identifier(ID_TYPE, VALUE);
	private static final Reference REFERENCE = new Reference(IDENTIFIER, KEY_ELEMENTS, IS_LOCAL);
	
	private Formula formula;
	
	@Before
	public void buildFormula() {
		formula = new Formula(Collections.singleton(REFERENCE));
	}
	
	@Test
	public void testConstructor() {
		Collection<IReference> references = Collections.singleton(REFERENCE);
		assertEquals(references, formula.getDependsOn());
	}
	
	@Test
	public void testSetDependsOn() {
		Reference reference = new Reference(new Identifier(IdentifierType.IRDI, "newId"), KeyElements.BLOB, true);
		Collection<IReference> references = Collections.singleton(reference);
		formula.setDependsOn(references);
		assertEquals(references, formula.getDependsOn());
	}
}
