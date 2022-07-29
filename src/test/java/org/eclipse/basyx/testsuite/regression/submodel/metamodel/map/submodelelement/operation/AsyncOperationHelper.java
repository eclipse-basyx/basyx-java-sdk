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

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
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
		Property asyncIn1 = new Property("asyncIn1", ValueType.Int32);
		asyncIn1.setKind(ModelingKind.TEMPLATE);
		Property asyncIn2 = new Property("asyncIn2", ValueType.Int32);
		asyncIn2.setKind(ModelingKind.TEMPLATE);
		IN.add(new OperationVariable(asyncIn1));
		IN.add(new OperationVariable(asyncIn2));
		Property asyncOut = new Property("asyncOut", ValueType.Int32);
		asyncOut.setKind(ModelingKind.TEMPLATE);
		OUT.add(new OperationVariable(asyncOut));
	}

	private final Function<Object[], Object> ASYNC_FUNC = (Function<Object[], Object>) v -> {
		int result = (int) v[0] + (int) v[1];
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
