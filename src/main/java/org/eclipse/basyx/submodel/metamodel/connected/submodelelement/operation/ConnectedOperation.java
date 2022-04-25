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
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationCheckHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of IOperation
 * 
 * @author rajashek
 *
 */
public class ConnectedOperation extends ConnectedSubmodelElement implements IOperation {
	// Default timeout for asynchronous operation calls
	public static final int DEFAULT_ASYNC_TIMEOUT = Operation.DEFAULT_ASYNC_TIMEOUT;

	public ConnectedOperation(VABElementProxy proxy) {
		super(proxy);
	}

	@Override
	public Collection<IOperationVariable> getInputVariables() {
		return Operation.createAsFacade(getElem()).getInputVariables();
	}

	@Override
	public Collection<IOperationVariable> getOutputVariables() {
		return Operation.createAsFacade(getElem()).getOutputVariables();
	}

	@Override
	public Collection<IOperationVariable> getInOutputVariables() {
		return Operation.createAsFacade(getElem()).getInOutputVariables();
	}

	/**
	 * Invoke a remote operation
	 */
	@Override
	public Object invoke(Object... params) {
		return invokeSimple(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SubmodelElement[] invoke(SubmodelElement... elems) {
		// Create request
		InvocationRequest request = createInvocationRequest(DEFAULT_ASYNC_TIMEOUT, elems);

		// Invoke the operation
		Object responseObj = getProxy().invokeOperation(Operation.INVOKE, request);
		InvocationResponse response = InvocationResponse.createAsFacade((Map<String, Object>) responseObj);

		// Extract the output elements
		Collection<IOperationVariable> outputArguments = response.getOutputArguments();
		List<ISubmodelElement> elements = outputArguments.stream().map(IOperationVariable::getValue).collect(Collectors.toList());

		// Cast them to an array
		SubmodelElement[] result = new SubmodelElement[elements.size()];
		elements.toArray(result);
		return result;
	}

	private InvocationRequest createInvocationRequest(int timeout, SubmodelElement... elems) {
		// Wrap parameters in operation variables
		Collection<IOperationVariable> inputArguments = Arrays.asList(elems).stream().map(OperationVariable::new).collect(Collectors.toList());
		// Generate random request id
		String requestId = UUID.randomUUID().toString();

		// Create invokation request
		return new InvocationRequest(requestId, new ArrayList<>(), inputArguments, timeout);
	}

	@Override
	public ConnectedAsyncInvocation invokeAsync(Object... params) {
		return invokeAsyncWithTimeout(DEFAULT_ASYNC_TIMEOUT, params);
	}

	@Override
	public ConnectedAsyncInvocation invokeAsyncWithTimeout(int timeout, Object... params) {
		OperationCheckHelper.checkValidParameterLength(params.length, getIdShort(), getInputVariables());
		SubmodelElement[] smElements = OperationHelper.wrapParameters(getInputVariables(), params);
		InvocationRequest request = createInvocationRequest(timeout, smElements);
		return new ConnectedAsyncInvocation(getProxy(), getIdShort(), request);
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.OPERATION;
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("An Operation has no value");
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("An Operation has no value");
	}

	@Override
	public Operation getLocalCopy() {
		return Operation.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public Object invokeSimple(Object... params) {
		OperationCheckHelper.checkValidParameterLength(params.length, getIdShort(), getInputVariables());
		OperationCheckHelper.checkSubmodelElementExpectedTypes(params, getInputVariables());
		SubmodelElement[] wrapper = OperationHelper.wrapParameters(getInputVariables(), params);
		SubmodelElement[] result = invoke(wrapper);
		return OperationHelper.unwrapResult(result);
	}
}
