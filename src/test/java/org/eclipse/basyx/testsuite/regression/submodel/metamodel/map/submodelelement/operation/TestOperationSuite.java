/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.submodelelement.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IAsyncInvocation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationExecutionErrorException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationExecutionTimeoutException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.vab.exception.provider.WrongNumberOfParametersException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for IOperation
 * 
 * @author conradi
 *
 */
public abstract class TestOperationSuite {
	
	protected static final String IN_VALUE = "inValue";
	protected static final String OUT_VALUE = "outValue";
	protected static final String INOUT_VALUE = "inOutValue";
	protected static Collection<OperationVariable> IN;
	protected static Collection<OperationVariable> OUT;
	protected static Collection<OperationVariable> INOUT;
	
	protected static final Function<Object[], Object> FUNC = (Function<Object[], Object>) v -> {
		return (int)v[0] + (int)v[1];
	};
	
	protected static final Function<Object[], Object> EXCEPTION_FUNC = (Function<Object[], Object>) v -> {
		throw new NullPointerException();
	};
	
	protected IOperation operation;
	protected IOperation operationException;

	/**
	 * Converts an Operation into the IOperation to be tested
	 */
	protected abstract IOperation prepareOperation(Operation operation);
	
	@Before
	public void setup() {
		IN = new ArrayList<OperationVariable>();
		OUT = new ArrayList<OperationVariable>();
		INOUT = new ArrayList<OperationVariable>();
		Property inProp1 = new Property("testIn1", IN_VALUE);
		inProp1.setModelingKind(ModelingKind.TEMPLATE);
		Property inProp2 = new Property("testIn2", IN_VALUE);
		inProp2.setModelingKind(ModelingKind.TEMPLATE);
		Property outProp = new Property("testId2", OUT_VALUE);
		outProp.setModelingKind(ModelingKind.TEMPLATE);
		Property inOutProp = new Property("testId3", INOUT_VALUE);
		inOutProp.setModelingKind(ModelingKind.TEMPLATE);
		IN.add(new OperationVariable(inProp1));
		IN.add(new OperationVariable(inProp2));
		OUT.add(new OperationVariable(outProp));
		INOUT.add(new OperationVariable(inOutProp));
		
		Operation op1 = new Operation(IN, OUT, INOUT, FUNC);
		op1.setIdShort("op1");
		operation = prepareOperation(op1);

		Operation op2 = new Operation(IN, OUT, INOUT, EXCEPTION_FUNC);
		op2.setIdShort("op2");
		operationException = prepareOperation(op2);
	}
	
	@Test
	public void testInvoke() throws Exception {
		assertEquals(5, operation.invoke(2, 3));
	}
	
	@Test
	public void testInvokeException() throws Exception {
		try {
			// Ensure the operation is invoked directly
			operationException.invoke(1, 2);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof NullPointerException
					|| e.getCause() instanceof NullPointerException);
		}
	}
	
	@Test
	public void testInvokeWithSubmodelElements() {
		Property param1 = new Property("testIn1", 2);
		param1.setModelingKind(ModelingKind.TEMPLATE);
		Property param2 = new Property("testIn2", 4);
		param2.setModelingKind(ModelingKind.TEMPLATE);
		SubmodelElement[] result = operation.invoke(param1, param2);
		assertEquals(1, result.length);
		assertEquals(6, result[0].getValue());
	}

	@Test
	public void testInvokeParametersException() throws Exception {
		try {
			operation.invoke(1);
			fail();
		} catch (Exception e) {
			// Exceptions from ConnectedOperation are wrapped in ProviderException
			assertTrue(e instanceof WrongNumberOfParametersException
					|| e.getCause() instanceof WrongNumberOfParametersException);
		}
	}

	@Test
	public void testInvokeAsync() throws Exception {
		AsyncOperationHelper helper = new AsyncOperationHelper();
		IOperation operation = prepareOperation(helper.getAsyncOperation());

		IAsyncInvocation invocation = operation.invokeAsync(3, 2);
		
		assertFalse(invocation.isFinished());
		
		helper.releaseWaitingOperation();

		assertTrue(invocation.isFinished());
		assertEquals(5, invocation.getResult());
	}
	
	@Test
	public void testInvokeMultipleAsync() throws Exception {
		AsyncOperationHelper helper = new AsyncOperationHelper();
		IOperation operation = prepareOperation(helper.getAsyncOperation());

		IAsyncInvocation invocation1 = operation.invokeAsync(1, 2);
		IAsyncInvocation invocation2 = operation.invokeAsync(6, 2);

		assertFalse(invocation1.isFinished());
		assertFalse(invocation2.isFinished());

		helper.releaseWaitingOperation();

		assertTrue(invocation1.isFinished());
		assertTrue(invocation2.isFinished());
		assertEquals(3, invocation1.getResult());
		assertEquals(8, invocation2.getResult());
	}

	@Test
	public void testInvokeAsyncTimeout() throws Exception {
		AsyncOperationHelper helper = new AsyncOperationHelper();
		IOperation operation = prepareOperation(helper.getAsyncOperation());

		// timeout of 1ms
		IAsyncInvocation invocation = operation.invokeAsyncWithTimeout(1, 3, 2);

		// Should be more than enough to trigger the timeout exception
		Thread.sleep(100);
		helper.releaseWaitingOperation();

		assertTrue(invocation.isFinished());
		try {
			invocation.getResult();
			fail();
		} catch (OperationExecutionTimeoutException e) {
		}
	}

	@Test
	public void testInvokeExceptionAsync() throws Exception {
		AsyncOperationHelper helper = new AsyncOperationHelper();
		IOperation operationException = prepareOperation(helper.getAsyncExceptionOperation());
		IAsyncInvocation invocation = operationException.invokeAsync();
		assertFalse(invocation.isFinished());
		
		helper.releaseWaitingOperation();
		
		try {
			invocation.getResult();
			fail();
		} catch (OperationExecutionErrorException e) {
		}

	}
	
	@Test
	public void testInputVariables() {
		Collection<IOperationVariable> inputVariables = operation.getInputVariables();
		assertEquals(2, inputVariables.size());
		Object value = getValueFromOpVariable(inputVariables);
		assertEquals(IN_VALUE, value);
	}

	@Test
	public void testOutputVariables() {
		Collection<IOperationVariable> outputVariables = operation.getOutputVariables();
		assertEquals(1, outputVariables.size());
		Object value = getValueFromOpVariable(outputVariables);
		assertEquals(OUT_VALUE, value);
	}

	@Test
	public void testInOutputVariables() {
		Collection<IOperationVariable> inoutVariables = operation.getInOutputVariables();
		assertEquals(1, inoutVariables.size());
		Object value = getValueFromOpVariable(inoutVariables);
		assertEquals(INOUT_VALUE, value);
	}
	
	/**
	 * Gets the Value from the OperationVariable in a collection
	 */
	private Object getValueFromOpVariable(Collection<IOperationVariable> vars) {
		IOperationVariable var = new ArrayList<>(vars).get(0);
		return var.getValue().getValue();
	}
	
}
