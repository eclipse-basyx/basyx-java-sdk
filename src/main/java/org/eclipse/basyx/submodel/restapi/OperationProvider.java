/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.operation.AsyncOperationHandler;
import org.eclipse.basyx.submodel.restapi.operation.CallbackResponse;
import org.eclipse.basyx.submodel.restapi.operation.ExecutionState;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Handles operations according to AAS meta model.
 *
 * @author schnicke
 *
 */
public class OperationProvider implements IModelProvider {
	public static final String ASYNC = "?async=true";
	public static final String INVOCATION_LIST = "invocationList";
	public String operationId;

	private IModelProvider modelProvider;

	public OperationProvider(IModelProvider modelProvider) {
		this.modelProvider = modelProvider;
		operationId = getIdShort(modelProvider.getValue(""));
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		String[] splitted = VABPathTools.splitPath(path);
		if (path.isEmpty()) {
			return modelProvider.getValue("");
		} else if (splitted[0].equals(INVOCATION_LIST) && splitted.length == 2) {
			String requestId = splitted[1];
			return AsyncOperationHandler.retrieveResult(requestId, operationId);
			
		} else {
			throw new MalformedRequestException("Get of an Operation supports only empty or /invocationList/{requestId} paths");
		}
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

	@Override
	public Object invokeOperation(String path, Object... parameters) throws ProviderException {
		// Fix path
		boolean async = path.endsWith(ASYNC);
		// remove the "invoke" from the end of the path
		path = VABPathTools.stripInvokeFromPath(path);

		// TODO: Only allow wrapped parameters with InvokationRequests
		Object[] unwrappedParameters;
		InvocationRequest request = getInvocationRequest(parameters);
		String requestId;
		if (request != null) {
			unwrappedParameters = request.unwrapInputParameters();
			requestId = request.getRequestId();
		} else {
			// => not necessary, if it is only allowed to use InvocationRequests
			unwrappedParameters = unwrapDirectParameters(parameters);
			// Generate random request id
			requestId = UUID.randomUUID().toString();
		}

		// Invoke /invokable instead of an Operation property if existent
		Object childElement = modelProvider.getValue(path);
		if (Operation.isOperation(childElement)) {
			path = VABPathTools.concatenatePaths(path, Operation.INVOKABLE);
		}
		
		// Handle async operations
		if (async) {
			// Async call? No return value, yet
			Collection<IOperationVariable> outputVars = copyOutputVariables();
			IModelProvider provider = new VABElementProxy(path, modelProvider);

			// Only necessary as long as invocations without InvokationRequest is allowed
			if (request != null) {
				AsyncOperationHandler.invokeAsync(provider, operationId, request, outputVars);
			} else {
				AsyncOperationHandler.invokeAsync(provider, operationId, requestId, unwrappedParameters, outputVars,
						10000);
			}

			// Request id has to be returned for caller to be able to retrieve result
			// => Use callback response and leave url empty
			return new CallbackResponse(requestId, "");
		}

		// Handle synchronous operations
		// Forward direct operation call to modelprovider
		Object directResult = modelProvider.invokeOperation(path, unwrappedParameters);
		if (request == null) {
			// Parameters have been passed directly? Directly return the result
			return directResult;
		}
		return createInvocationResponseFromDirectResult(request, directResult);
	}

	/**
	 * Directly creates an InvocationResponse from an operation result
	 */
	private Object createInvocationResponseFromDirectResult(InvocationRequest request, Object directResult) {
		// Get SubmodelElement output template
		Collection<IOperationVariable> outputs = copyOutputVariables();
		if(outputs.size() > 0)
		{
			SubmodelElement outputElem = (SubmodelElement) outputs.iterator().next().getValue();
			// Set result object
			outputElem.setValue(directResult);
		};
		
		// Create and return InvokationResponse
		return new InvocationResponse(request.getRequestId(), new ArrayList<>(), outputs, ExecutionState.COMPLETED);
	}

	/**
	 * Extracts an invokation request from a generic parameter array
	 * 
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private InvocationRequest getInvocationRequest(Object[] parameters) {
		if (parameters.length == 1 && parameters[0] instanceof Map<?, ?>) {
			Map<String, Object> requestMap = (Map<String, Object>) parameters[0];
			return InvocationRequest.createAsFacade(requestMap);
		}
		return null;
	}
	
	/**
	 * Gets the (first) output parameter from the underlying object
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Collection<IOperationVariable> copyOutputVariables() {
		Map<String, Object> operationMap = (Map<String, Object>) getValue("");
		Operation op = Operation.createAsFacade(operationMap);
		Collection<IOperationVariable> outputs = op.getOutputVariables();
		Collection<IOperationVariable> outCopy = new ArrayList<>();
		outputs.stream().forEach(o -> outCopy.add(new OperationVariable(o.getValue().getLocalCopy())));
		return outCopy;
	}

	@SuppressWarnings("unchecked")
	private String getIdShort(Object operation) {
		if(Operation.isOperation(operation)) {
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
