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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier.qualifiable;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Constraint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Formula;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Qualifiable} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestQualifiable {
	private static final Formula FORMULA1 = new Formula(Collections.singleton(new Reference(new Key(KeyElements.BLOB, true, "TestValue", IdentifierType.IRI))));
	private static final Formula FORMULA2 = new Formula(Collections.singleton(new Reference(new Key(KeyElements.ANNOTATEDRELATIONSHIPELEMENT, true, "TestValue2", IdentifierType.IRDI))));
	
	@Test
	public void testConstructor1() {
		Qualifiable qualifiable = new Qualifiable(FORMULA1);
		assertEquals(Collections.singleton(FORMULA1), qualifiable.getQualifiers());
	}
	
	@Test
	public void testConstructor2() {
		Collection<Constraint> constraints = new HashSet<Constraint>();
		constraints.add(FORMULA1);
		constraints.add(FORMULA2);
		
		Qualifiable qualifiable = new Qualifiable(constraints);
		assertEquals(constraints, qualifiable.getQualifiers());
	}
	
	@Test
	public void testSetQualifier() {
		Qualifiable qualifiable = new Qualifiable(FORMULA1);
		
		qualifiable.setQualifiers(Collections.singleton(FORMULA2));
		assertEquals(Collections.singleton(FORMULA2), qualifiable.getQualifiers());
	}
}
