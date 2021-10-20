/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.exception.provider.WrongNumberOfParametersException;
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
		// Wrap simple params
		SubmodelElement[] wrapper = createElementWrapper(params);

		// Invoke with submodel elements
		SubmodelElement[] result = invoke(wrapper);

		// Unwrap result wrapper
		return unwrapResult(result);
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
		List<ISubmodelElement> elements = outputArguments.stream().map(IOperationVariable::getValue)
				.collect(Collectors.toList());

		// Cast them to an array
		SubmodelElement[] result = new SubmodelElement[elements.size()];
		elements.toArray(result);
		return result;
	}

	private InvocationRequest createInvocationRequest(int timeout, SubmodelElement... elems) {
		// Wrap parameters in operation variables
		Collection<IOperationVariable> inputArguments = Arrays.asList(elems).stream().map(OperationVariable::new)
				.collect(Collectors.toList());
		// Generate random request id
		String requestId = UUID.randomUUID().toString();

		// Create invokation request
		return new InvocationRequest(requestId, new ArrayList<>(), inputArguments, timeout);
	}

	private SubmodelElement[] createElementWrapper(Object... params) {
		Collection<IOperationVariable> inputVariables = getInputVariables();
		if (inputVariables.size() != params.length) {
			throw new WrongNumberOfParametersException(getIdShort(), inputVariables, params);
		}

		// Copy parameter values into SubmodelElements according to InputVariables
		SubmodelElement[] ret = new SubmodelElement[params.length];
		Iterator<IOperationVariable> iterator = inputVariables.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			IOperationVariable matchedInput = iterator.next();
			ISubmodelElement inputElement = matchedInput.getValue();
			SubmodelElement copy = inputElement.getLocalCopy();
			copy.setValue(params[i]);
			ret[i] = copy;
			i++;
		}

		return ret;
	}

	@Override
	public ConnectedAsyncInvocation invokeAsync(Object... params) {
		SubmodelElement[] smElements = createElementWrapper(params);
		InvocationRequest request = createInvocationRequest(DEFAULT_ASYNC_TIMEOUT, smElements);
		return new ConnectedAsyncInvocation(getProxy(), getIdShort(), request);
	}
	
	@Override
	public ConnectedAsyncInvocation invokeAsyncWithTimeout(int timeout, Object... params) {
		SubmodelElement[] smElements = createElementWrapper(params);
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
	
	@SuppressWarnings("unchecked")
	private Object unwrapResult(Object result) {
		if (result instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) result;
			if (coll.isEmpty()) {
				return result;
			}
			Object resultWrapper = coll.iterator().next();
			if (resultWrapper instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>) resultWrapper;
				if (map.get(Referable.IDSHORT).equals("Response") && map.get(Property.VALUE) != null) {
					return map.get(Property.VALUE);
				}
			}
		} else if (result instanceof SubmodelElement[]) {
			SubmodelElement[] arr = (SubmodelElement[]) result;
			if (arr.length > 0 && arr[0] instanceof Map<?, ?>) {
				return arr[0].getValue();
			}
		}
		return result;
	}

	@Override
	public Operation getLocalCopy() {
		return Operation.createAsFacade(getElem()).getLocalCopy();
	}
}
