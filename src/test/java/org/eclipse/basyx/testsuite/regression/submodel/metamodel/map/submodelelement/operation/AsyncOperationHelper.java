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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;

/**
 * Helperclass for testing async invocations of Operations
 * 
 * @author conradi
 *
 */
public class AsyncOperationHelper {
	
	public static final String ASYNC_OP_ID = "asyncOperation";
	public static Collection<OperationVariable> IN;
	protected static Collection<OperationVariable> OUT;
	public static final String ASYNC_EXCEPTION_OP_ID = "asyncExceptionOperation";

	private Object waitObject = new Object();
	private boolean shouldWait = true;
	
	public AsyncOperationHelper() {
		IN = new ArrayList<OperationVariable>();
		OUT = new ArrayList<OperationVariable>();
		Property asyncIn1 = new Property("asyncIn1", "");
		asyncIn1.setModelingKind(ModelingKind.TEMPLATE);
		Property asyncIn2 = new Property("asyncIn2", "");
		asyncIn2.setModelingKind(ModelingKind.TEMPLATE);
		IN.add(new OperationVariable(asyncIn1));
		IN.add(new OperationVariable(asyncIn2));
		Property asyncOut = new Property("asyncOut", "");
		asyncOut.setModelingKind(ModelingKind.TEMPLATE);
		OUT.add(new OperationVariable(asyncOut));
	}
	
	private final Function<Object[], Object> ASYNC_FUNC = (Function<Object[], Object>) v -> {
		int result = (int)v[0] + (int)v[1];
		synchronized (waitObject) {
			while (shouldWait) {
				try {
					waitObject.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return result;
	};
	
	private final Function<Object[], Object> ASYNC_EXCEPTION_FUNC = (Function<Object[], Object>) v -> {
		NullPointerException ex = new NullPointerException();
		synchronized (waitObject) {
			while (shouldWait) {
				try {
					waitObject.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		throw ex;
	};
	
	public Operation getAsyncOperation() {
		shouldWait = true;
		Operation op = new Operation(ASYNC_FUNC);
		op.setIdShort(ASYNC_OP_ID);
		op.setInputVariables(IN);
		op.setOutputVariables(OUT);
		return op;
	}
	
	public Operation getAsyncExceptionOperation() {
		shouldWait = true;
		Operation op = new Operation(ASYNC_EXCEPTION_FUNC);
		op.setIdShort(ASYNC_EXCEPTION_OP_ID);
		return op;
	}
	
	public void releaseWaitingOperation() {
		shouldWait = false;
		synchronized (waitObject) {
			waitObject.notifyAll();
		}
		
		// Give the Operation a bit of time to finish
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}
	
}
