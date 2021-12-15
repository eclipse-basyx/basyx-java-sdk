/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.formula.functions.T;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationExecutionTimeoutException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Helperclass used to keep and invoke operations asynchronously.
 * 
 * @author conradi, espen
 *
 */
public class AsyncOperationHandler {
	private static Map<String, InvocationResponse> responses = new HashMap<>();
	private static Map<String, String> responseOperationMap = new HashMap<>();
	private static ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(0);

	/**
	 * Invokes an Operation with an invocation request
	 */
	public static void invokeAsync(IModelProvider provider, String operationId, InvocationRequest request,
			Collection<IOperationVariable> outputArguments) {
		String requestId = request.getRequestId();
		Collection<IOperationVariable> inOutArguments = request.getInOutArguments();
		Object[] parameters = request.unwrapInputParameters();
		invokeAsync(provider, operationId, requestId, parameters, inOutArguments, outputArguments,
				request.getTimeout());
	}

	/**
	 * Invokes an Operation without an invocation request
	 */
	public static void invokeAsync(IModelProvider provider, String operationId, String requestId, Object[] inputs,
			Collection<IOperationVariable> outputArguments, int timeout) {
		invokeAsync(provider, operationId, requestId, inputs, new ArrayList<>(), outputArguments, timeout);
	}

	/**
	 * Invokes an Operation and returns its requestId
	 */
	private static void invokeAsync(IModelProvider provider, String operationId, String requestId, Object[] inputs,
			Collection<IOperationVariable> inOutArguments,
			Collection<IOperationVariable> outputArguments, int timeout) {
		synchronized (responses) {
			InvocationResponse response = new InvocationResponse(requestId, inOutArguments, outputArguments,
					ExecutionState.INITIATED);

			responses.put(requestId, response);
			responseOperationMap.put(requestId, operationId);

			CompletableFuture.supplyAsync(
					// Run Operation asynchronously
					() -> provider.invokeOperation("", inputs))
					// Accept either result or throw exception on timeout
					.acceptEither(setTimeout(timeout, requestId), result -> {
						// result accepted? => Write execution state if there is an output
						response.setExecutionState(ExecutionState.COMPLETED);
						if (!response.getOutputArguments().isEmpty()) {
							IOperationVariable output = response.getOutputArguments().iterator().next();
							output.getValue().setValue(result);
						}
					}).exceptionally(throwable -> {
						// result not accepted? set operation state
						ProviderException exception = null;
						if (throwable.getCause() instanceof OperationExecutionTimeoutException) {
							response.setExecutionState(ExecutionState.TIMEOUT);
							exception = new ProviderException("Request " + requestId + " timed out", throwable);
						} else {
							response.setExecutionState(ExecutionState.FAILED);
							exception = new ProviderException("Request " + requestId + " failed", throwable);
						}
						// set provider exception if there is an output
						if (!response.getOutputArguments().isEmpty()) {
							IOperationVariable output = response.getOutputArguments().iterator().next();
							output.getValue().setValue(exception);
						}
						return null;
					});
		}
	}
	
	/**
	 * Function for scheduling a timeout function with completable futures
	 */
	private static CompletableFuture<T> setTimeout(int timeout, String requestId) {
		CompletableFuture<T> result = new CompletableFuture<>();
		delayer.schedule(
				() -> result.completeExceptionally(
						new OperationExecutionTimeoutException("Request " + requestId + " timed out")),
				timeout, TimeUnit.MILLISECONDS);
		return result;
	}

	/**
	 * Gets the result of an invocation
	 * 
	 * @param operationId the id of the requested Operation
	 * @param requestId the id of the request
	 * @return the result of the Operation or a Message that it is not yet finished
	 */
	public static Object retrieveResult(String requestId, String operationId) {
		// Remove the Invocation if it is finished and its result was retrieved
		synchronized (responses) {
			if (!responses.containsKey(requestId)) {
				throw new ResourceNotFoundException(
						"RequestId '" + requestId + "' not found for operation '" + operationId + "'.");
			}
		
			String validOperationId = responseOperationMap.get(requestId);
			if (!operationId.equals(validOperationId)) {
				throw new ResourceNotFoundException(
						"RequestId '" + requestId + "' does not belong to Operation '" + operationId + "'");
			}

			InvocationResponse response = responses.get(requestId);
			if (ExecutionState.COMPLETED == response.getExecutionState()
					|| ExecutionState.TIMEOUT == response.getExecutionState()
					|| ExecutionState.FAILED == response.getExecutionState()) {
				responses.remove(requestId);
				responseOperationMap.remove(requestId);
			}
			return response;
		}
	}
	
	/**
	 * Checks if a given requestId exists
	 * 
	 * @param requestId the id to be checked
	 * @return if the id exists
	 */
	public static boolean hasRequestId(String requestId) {
		synchronized (responses) {
			return responses.containsKey(requestId);
		}
	}
}
