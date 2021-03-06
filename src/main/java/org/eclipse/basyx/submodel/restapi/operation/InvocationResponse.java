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
package org.eclipse.basyx.submodel.restapi.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Response when invoking operation submodel elements
 * 
 * @author espen
 *
 */
public class InvocationResponse extends VABModelMap<Object> {
	public static final String REQUESTID = "requestId";
	public static final String INOUTARGUMENTS = "inoutputArguments";
	public static final String OUTPUTARGUMENTS = "outputArguments";
	public static final String EXECUTIONSTATE = "executionState";

	private InvocationResponse() {
	}

	public InvocationResponse(String requestId, Collection<IOperationVariable> inoutArguments, Collection<IOperationVariable> outputArguments, ExecutionState executionState) {
		put(REQUESTID, requestId);
		put(INOUTARGUMENTS, inoutArguments);
		put(OUTPUTARGUMENTS, outputArguments);
		put(EXECUTIONSTATE, executionState.toString());
	}

	public static InvocationResponse createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		InvocationResponse resp = new InvocationResponse();
		resp.setRequestId((String) map.get(REQUESTID));
		Collection<IOperationVariable> inoutArguments = createInoutArguments(map);
		resp.setInOutArguments(inoutArguments);
		Collection<IOperationVariable> outputArguments = createOutputArguments(map);
		resp.setOutputArguments(outputArguments);

		Object execStateObj = map.get(EXECUTIONSTATE);
		if (execStateObj instanceof String) {
			resp.setExecutionState(ExecutionState.fromString((String) execStateObj));
		} else if (execStateObj instanceof ExecutionState) {
			resp.setExecutionState((ExecutionState) execStateObj);
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	private static Collection<IOperationVariable> createOutputArguments(Map<String, Object> map) {
		Collection<Map<String, Object>> outputMap = (Collection<Map<String, Object>>) map.get(OUTPUTARGUMENTS);
		return createOperationVariables(outputMap);
	}

	/**
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	private static Collection<IOperationVariable> createInoutArguments(Map<String, Object> map) {
		Collection<Map<String, Object>> inoutMap = (Collection<Map<String, Object>>) map.get(INOUTARGUMENTS);
		return createOperationVariables(inoutMap);
	}

	private static Collection<IOperationVariable> createOperationVariables(Collection<Map<String, Object>> variableMap) {
		if (variableMap != null) {
			return variableMap.stream().map(OperationVariable::createAsFacade).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * Gets the first output value of this response
	 * 
	 * @return The output value and null, if there is no output
	 */
	public Object getFirstOutput() {
		try {
			IOperationVariable next = getOutputArguments().iterator().next();
			return next.getValue().getValue();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	private void setRequestId(String request) {
		put(REQUESTID, request);
	}

	private void setInOutArguments(Collection<IOperationVariable> inoutArguments) {
		put(INOUTARGUMENTS, inoutArguments);
	}

	private void setOutputArguments(Collection<IOperationVariable> inputArguments) {
		put(OUTPUTARGUMENTS, inputArguments);
	}

	public void setExecutionState(ExecutionState state) {
		put(EXECUTIONSTATE, state.toString());
	}

	public String getRequestId() {
		return (String) get(REQUESTID);
	}

	@SuppressWarnings("unchecked")
	public Collection<IOperationVariable> getInOutArguments() {
		return (Collection<IOperationVariable>) get(INOUTARGUMENTS);
	}

	@SuppressWarnings("unchecked")
	public Collection<IOperationVariable> getOutputArguments() {
		return (Collection<IOperationVariable>) get(OUTPUTARGUMENTS);
	}

	public ExecutionState getExecutionState() {
		return ExecutionState.fromString((String) get(EXECUTIONSTATE));
	}
}
