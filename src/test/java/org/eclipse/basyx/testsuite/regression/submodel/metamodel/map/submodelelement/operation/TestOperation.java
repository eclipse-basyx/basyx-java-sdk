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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.junit.Test;

/**
 * Tests constructor, getter and setter of {@link Operation} for their
 * correctness
 * 
 * @author haque, conradi
 *
 */
public class TestOperation extends TestOperationSuite {

	private static final String KEY_VALUE = "testKeyValue";

	private int setValueForTestSetNoInNoOutFunctionAsInvokable;

	@Override
	protected IOperation prepareOperation(Operation operation) {
		return operation;
	}

	@Test
	public void testOptionalElements() throws Exception {
		simpleOperation = new Operation(null, null, null, SIMPLE_FUNC);
		assertEquals(0, simpleOperation.getInputVariables().size());
		assertEquals(0, simpleOperation.getOutputVariables().size());
		assertEquals(0, simpleOperation.getInOutputVariables().size());
	}

	@Test
	public void testSetFunctionAsInvokable() throws Exception {
		Operation operation = new Operation(TWO_IN, OUT, INOUT, SIMPLE_FUNC);
		operation.setIdShort("function");
		assertEquals(5, operation.invoke(3, 2));

		Function<Object[], Object> newFunction = (Function<Object[], Object>) v -> {
			return (int) v[0] - (int) v[1];
		};
		operation.setInvokable(newFunction);

		assertEquals(1, operation.invoke(3, 2));
	}

	@Test
	public void testSetNoInNoOutFunctionAsInvokable() throws Exception {
		Operation operation = new Operation("noInNoOutFunction");
		setValueForTestSetNoInNoOutFunctionAsInvokable = 0;
		int expected = 10;

		operation.setInvokable(() -> {
			setValueForTestSetNoInNoOutFunctionAsInvokable = expected;
		});

		operation.invokeSimple();

		assertEquals(expected, setValueForTestSetNoInNoOutFunctionAsInvokable);
	}

	@Test
	public void testSetDataSpecificationReferences() {
		Operation operation = new Operation(TWO_IN, OUT, INOUT, SIMPLE_FUNC);
		Collection<IReference> references = Collections.singleton(new Reference(new Key(KeyElements.ASSET, true, KEY_VALUE, IdentifierType.IRI)));
		operation.setDataSpecificationReferences(references);

		Collection<IReference> newReferences = operation.getDataSpecificationReferences();
		assertEquals(1, newReferences.size());

		IReference newReference = new ArrayList<>(newReferences).get(0);

		assertEquals(KEY_VALUE, newReference.getKeys().get(0).getValue());
	}
}
