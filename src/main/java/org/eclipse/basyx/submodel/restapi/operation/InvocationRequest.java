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
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * Request for invoking operation submodel elements
 * 
 * @author schnicke
 *
 */
public class InvocationRequest extends VABModelMap<Object> {
	public static final String REQUESTID = "requestId";
	public static final String INOUTARGUMENTS = "inoutputArguments";
	public static final String INPUTARGUMENTS = "inputArguments";
	public static final String TIMEOUT = "timeout";

	private InvocationRequest() {
	}

	public InvocationRequest(String requestId, Collection<IOperationVariable> inoutArguments, Collection<IOperationVariable> inputArguments, int timeout) {
		put(REQUESTID, requestId);
		put(INOUTARGUMENTS, inoutArguments);
		put(INPUTARGUMENTS, inputArguments);
		put(TIMEOUT, timeout);
	}

	public static InvocationRequest createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}
		
		InvocationRequest ret = new InvocationRequest();
		ret.setRequestId((String) map.get(REQUESTID));
		Collection<IOperationVariable> inoutArguments = createInoutArguments(map);
		ret.setInOutArguments(inoutArguments);

		Collection<IOperationVariable> inputArguments = createInputArguments(map);
		ret.setInputArguments(inputArguments);

		ret.setTimeout((int) map.get(TIMEOUT));

		return ret;
	}
	
	/**
	 * Returns true if the given map is recognized as an InvocationRequest
	 */
	@SuppressWarnings("unchecked")
	public static boolean isInvocationRequest(Object value) {
		if(!(value instanceof Map<?, ?>)) {
			return false;
		}
		
		Map<String, Object> map = (Map<String, Object>) value;
		
		return isValid(map);
	}
	
	/**
	 * Check whether all mandatory elements for the metamodel
	 * exist in a map
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> map) {
		return map.containsKey(REQUESTID) && map.containsKey(TIMEOUT);
	}

	/**
	 * Unwraps the values of the inputVars in the order of occurance in the collection of input arguments
	 * 
	 * @return
	 */
	public Object[] unwrapInputParameters() {
		Collection<IOperationVariable> inputArguments = getInputArguments();
		Object[] unwrappedParameters = new Object[inputArguments.size()];
		Iterator<IOperationVariable> iterator = inputArguments.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			IOperationVariable next = iterator.next();
			unwrappedParameters[i] = next.getValue().getValue();
			i++;
		}
		return unwrappedParameters;
	}

	@SuppressWarnings("unchecked")
	private static Collection<IOperationVariable> createInputArguments(Map<String, Object> map) {
		Collection<Map<String, Object>> inputMap = (Collection<Map<String, Object>>) map.get(INPUTARGUMENTS);
		return createOperationVariables(inputMap);
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

	private void setRequestId(String request) {
		put(REQUESTID, request);
	}

	private void setInOutArguments(Collection<IOperationVariable> inoutArguments) {
		put(INOUTARGUMENTS, inoutArguments);
	}

	private void setInputArguments(Collection<IOperationVariable> inputArguments) {
		put(INPUTARGUMENTS, inputArguments);
	}

	private void setTimeout(int timeout) {
		put(TIMEOUT, timeout);
	}

	public String getRequestId() {
		return (String) get(REQUESTID);
	}

	@SuppressWarnings("unchecked")
	public Collection<IOperationVariable> getInOutArguments() {
		return (Collection<IOperationVariable>) get(INOUTARGUMENTS);
	}

	@SuppressWarnings("unchecked")
	public Collection<IOperationVariable> getInputArguments() {
		return (Collection<IOperationVariable>) get(INPUTARGUMENTS);
	}

	public int getTimeout() {
		return (int) get(TIMEOUT);
	}
}
