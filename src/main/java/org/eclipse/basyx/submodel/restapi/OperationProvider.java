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
package org.eclipse.basyx.submodel.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.operation.AsyncOperationHandler;
import org.eclipse.basyx.submodel.restapi.operation.CallbackResponse;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;
import org.eclipse.basyx.submodel.restapi.operation.ExecutionState;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

/**
 * Handles operations according to AAS meta model.
 *
 * @author schnicke, espen, fischer
 *
 */
public class OperationProvider implements IModelProvider {
	public static final String ASYNC = "?async=true";
	public static final String INVOCATION_LIST = "invocationList";
	public String operationId;

	private IModelProvider modelProvider;
	private DelegatedInvocationManager invocationHelper;
	
	public OperationProvider(IModelProvider modelProvider) {
		this(modelProvider, new DelegatedInvocationManager(new HTTPConnectorFactory()));
	}

	public OperationProvider(IModelProvider modelProvider, DelegatedInvocationManager invocationHelper) {
		this.modelProvider = modelProvider;
		this.invocationHelper = invocationHelper;
		operationId = getIdShort(modelProvider.getValue(""));
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		String[] splitted = VABPathTools.splitPath(path);
		if (path.isEmpty()) {
			return modelProvider.getValue("");
		} else if (isInvocationListQuery(splitted)) {
			String requestId = splitted[1];
			return AsyncOperationHandler.retrieveResult(requestId, operationId);

		} else {
			throw new MalformedRequestException("Get of an Operation supports only empty or /invocationList/{requestId} paths");
		}
	}

	private boolean isInvocationListQuery(String[] splitted) {
		return splitted[0].equals(INVOCATION_LIST) && splitted.length == 2;
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		throw new MalformedRequestException("Set not allowed at path '" + path + "'");
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		throw new MalformedRequestException("Create not allowed at path '" + path + "'");
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		// Deletion of operation is handled by parent provider
		throw new MalformedRequestException("Delete not allowed at path '" + path + "'");
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		throw new MalformedRequestException("Delete not allowed at path '" + path + "'");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object invokeOperation(String path, Object... parameters) throws ProviderException {
		boolean isAsync = isAsyncInvokePath(path);
		path = VABPathTools.stripInvokeFromPath(path);

		// Invoke /invokable instead of an Operation property if existent
		Object childElement = modelProvider.getValue(path);
		if (!Operation.isOperation(childElement)) {
			throw new MalformedRequestException("Only operations can be invoked.");
		}
		Operation op = Operation.createAsFacade((Map<String, Object>) childElement);
		
		if (DelegatedInvocationManager.isDelegatingOperation(op)) {
			return invocationHelper.invokeDelegatedOperation(op, parameters);
		} else {
			InvocationRequest request = getInvocationRequest(parameters, op);
			
			if (request != null && isAsync) {
				return handleAsyncRequestInvokation(op, request);
			} else if (request != null) {
				return handleSyncRequestInvokation(op, request);
			} else if (isAsync) {// => not necessary, if it is only allowed to use InvocationRequests
				return handleAsyncParameterInvokation(op, parameters);
			} else {
				return handleSyncParameterInvokation(op, parameters);
			}	
		}
	}

	private CallbackResponse handleAsyncRequestInvokation(Operation operation, InvocationRequest request) {
		Collection<IOperationVariable> outputVars = copyOutputVariables(operation);

		AsyncOperationHandler.invokeAsync(operation, operationId, request, outputVars);

		// Request id has to be returned for caller to be able to retrieve result
		// => Use callback response and leave url empty
		return new CallbackResponse(request.getRequestId(), "");
	}

	private InvocationResponse handleSyncRequestInvokation(Operation operation, InvocationRequest request) {
		SubmodelElement[] inputVariables = getSumbodelElementsFromInvocationRequest(request);
		SubmodelElement[] submodelElementsResult = operation.invoke(inputVariables);

		return createInvocationResponseFromSubmodelElementsResult(request, submodelElementsResult);
	}

	private SubmodelElement[] getSumbodelElementsFromInvocationRequest(InvocationRequest request) {
		Collection<IOperationVariable> inputVariables = request.getInputArguments();

		Stream<IOperationVariable> inputVariablesStream = StreamSupport.stream(inputVariables.spliterator(), false);
		Stream<SubmodelElement> submodelElementStream = inputVariablesStream.map(inputVar -> (SubmodelElement) inputVar.getValue());

		return submodelElementStream.toArray(SubmodelElement[]::new);
	}

	private CallbackResponse handleAsyncParameterInvokation(Operation operation, Object[] parameters) {
		Object[] unwrappedParameters = unwrapDirectParameters(parameters);
		Collection<IOperationVariable> outputVars = copyOutputVariables(operation);

		String requestId = UUID.randomUUID().toString();

		AsyncOperationHandler.invokeAsync(operation, operationId, requestId, unwrappedParameters, outputVars, 10000);
		// Request id has to be returned for caller to be able to retrieve result
		// => Use callback response and leave url empty
		return new CallbackResponse(requestId, "");
	}

	private Object handleSyncParameterInvokation(Operation operation, Object[] parameters) {
		Object[] unwrappedParameters = unwrapDirectParameters(parameters);

		Object directResult = operation.invokeSimple(unwrappedParameters);

		return directResult;
	}

	private boolean isAsyncInvokePath(String path) {
		return path.endsWith(ASYNC);
	}

	private InvocationResponse createInvocationResponseFromSubmodelElementsResult(InvocationRequest request, SubmodelElement[] submodelElementsResult) {
		Collection<IOperationVariable> outputs;
		if (submodelElementsResult == null) {
			outputs = new ArrayList<>();
		} else {
			Stream<SubmodelElement> submodelElementsStream = Arrays.stream(submodelElementsResult);
			Stream<OperationVariable> operationVariableStream = submodelElementsStream.map(submodelElement -> new OperationVariable(submodelElement));

			outputs = operationVariableStream.collect(Collectors.toList());
		}
		return new InvocationResponse(request.getRequestId(), new ArrayList<>(), outputs, ExecutionState.COMPLETED);
	}

	/**
	 * Extracts an invocation request from a generic parameter array Matches
	 * parameters to order of Operation inputs by id Throws
	 * MalformedArgumentException if a required parameter is missing
	 * 
	 * @param parameters
	 *            the input parameters
	 * @param op
	 *            the Operation providing the inputVariables to be matched to the
	 *            actual input
	 * @return the build InvocationRequest
	 */
	@SuppressWarnings("unchecked")
	private InvocationRequest getInvocationRequest(Object[] parameters, Operation op) {
		if (!isInvokationRequest(parameters)) {
			return null;
		}

		Map<String, Object> requestMap = (Map<String, Object>) parameters[0];
		InvocationRequest request = InvocationRequest.createAsFacade(requestMap);

		// Sort parameters in request by InputVariables of operation
		Collection<IOperationVariable> vars = op.getInputVariables();
		Collection<IOperationVariable> ordered = createOrderedInputVariablesList(request, vars);

		return new InvocationRequest(request.getRequestId(), request.getInOutArguments(), ordered, request.getTimeout());
	}

	private Collection<IOperationVariable> createOrderedInputVariablesList(InvocationRequest request, Collection<IOperationVariable> vars) {
		Collection<IOperationVariable> ordered = new ArrayList<>();

		for (IOperationVariable var : vars) {
			String id = var.getValue().getIdShort();
			ordered.add(findOperationVariableById(id, request.getInputArguments()));
		}

		return ordered;
	}

	private IOperationVariable findOperationVariableById(String id, Collection<IOperationVariable> vars) {
		for (IOperationVariable input : vars) {
			if (input.getValue().getIdShort().equals(id)) {
				return input;
			}
		}

		throw new MalformedRequestException("Expected parameter " + id + " missing in request");
	}

	private boolean isInvokationRequest(Object[] parameters) {
		if (parameters.length != 1) {
			return false;
		}
		return InvocationRequest.isInvocationRequest(parameters[0]);
	}

	private Collection<IOperationVariable> copyOutputVariables(Operation op) {
		Collection<IOperationVariable> outputs = op.getOutputVariables();
		Collection<IOperationVariable> outCopy = new ArrayList<>();
		outputs.stream().forEach(o -> outCopy.add(new OperationVariable(o.getValue().getLocalCopy())));
		return outCopy;
	}

	@SuppressWarnings("unchecked")
	private String getIdShort(Object operation) {
		if (Operation.isOperation(operation)) {
			return Operation.createAsFacade((Map<String, Object>) operation).getIdShort();
		} else {
			// Should never happen as SubmodelElementProvider.getElementProvider
			// already checked that it is an Operation, if the Provider is used as intended
			throw new ProviderException("The Object this OperationProvider is pointing to is not an Operation");
		}
	}

	/**
	 * Unwraps a parameter by retrieving the "value" entry
	 *
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object[] unwrapDirectParameters(Object[] parameters) {
		// Unwrap parameters, if they are wrapped
		Object[] unwrappedParameters = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Object parameter = parameters[i];
			if (parameter instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>) parameter;
				// Parameters have a strictly defined order and may not be omitted at all.
				// Enforcing the structure with valueType is ok, but we should unwrap null
				// values, too.
				if (map.get("valueType") != null && map.containsKey("value")) {
					unwrappedParameters[i] = map.get("value");
					continue;
				}
			}
			unwrappedParameters[i] = parameter;
		}
		return unwrappedParameters;
	}

}
