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

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IAsyncInvocation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationExecutionErrorException;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationExecutionTimeoutException;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.submodel.restapi.operation.CallbackResponse;
import org.eclipse.basyx.submodel.restapi.operation.ExecutionState;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Connected variant of IAsyncInvocation
 * 
 * @author conradi
 *
 */
public class ConnectedAsyncInvocation implements IAsyncInvocation {

	private String operationId;
	private String requestId;
	
	private VABElementProxy proxy;
	
	private Object result = null;
	private boolean resultRetrieved = false;
	
	@SuppressWarnings("unchecked")
	public ConnectedAsyncInvocation(VABElementProxy proxy, String operationId, InvocationRequest request) {
		this.proxy = proxy;
		this.operationId = operationId;
		CallbackResponse response = CallbackResponse.createAsFacade((Map<String, Object>) proxy.invokeOperation(Operation.INVOKE + OperationProvider.ASYNC, request));
		requestId = response.getRequestId();
	}
	
	@Override
	public Object getResult() {
		// Wait for Operation to finish
		while (!isFinished()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		
		// Side-effect of isFinished is querying the result.
		// Thus, it can be assumed, the the result will be available here

		if (result instanceof Exception) {
			throw new OperationExecutionErrorException("Exception while executing Invocation '"
					+ requestId + "' of Operation '" + operationId + "'");
		} else if (ExecutionState.FAILED == result) {
			throw new OperationExecutionErrorException("Exception while executing Invocation '" + requestId
					+ "' of Operation '" + operationId + "'; Operation failed");
		} else if (ExecutionState.TIMEOUT == result) {
			throw new OperationExecutionTimeoutException(
					"Invocation '" + requestId + "' of Operation '" + operationId + "' timed out");
		} else {
			return result;
		}
	}
	
	/**
	 * Queries the operation with the connected proxy to see, if the result is already finished
	 */
	@Override
	public boolean isFinished() {
		if (resultRetrieved) {
			// If the result was already retrieved the Operation is done
			return true;
		}

		retrieveResultDirectly();
		return resultRetrieved;
	}

	@SuppressWarnings("unchecked")
	private void retrieveResultDirectly() {
		// 1. Retrieve InvocationResponse from proxy
		Object responseObj = null;
		try {
			 responseObj = proxy.getValue(getListPath());
		} catch (ProviderException e) {
			// As the Submodel-API does not specify a request to ask whether
			// the operation is finished, it has to be done via the retrieval of the value.
			// If the execution resulted in an Exception this Exception would be thrown here
			// -> if a ProviderException with a RuntimeException as cause is thrown,
			// the Operation is finished.
			if (e.getCause() instanceof RuntimeException) {
				resultRetrieved = true;
				result = e;
			} else {
				// If it is something else -> rethrow it
				throw e;
			}
		}

		// 2. Cast response to InvocationResponse
		InvocationResponse response = null;
		if ( responseObj instanceof InvocationResponse ) {
			response = (InvocationResponse) responseObj;
		} else if ( result instanceof Map<?, ?> ) {
			response = InvocationResponse.createAsFacade((Map<String, Object>) result);
		} else {
			// got no valid InvocationResponse
			throw new ProviderException("Response for requestId " + requestId + " invalid!");
		}

		// 3. Transform to direct result
		switch (response.getExecutionState()) {
			case COMPLETED:
				// Finished => set internal state
				resultRetrieved = true;
				result = response.getFirstOutput();
				break;
			case FAILED:
			case TIMEOUT:
				// Finished, but no result => set internal state
				resultRetrieved = true;
				result = response.getExecutionState();
				break;
			default:
				// Not finished, yet, result hasn't been retrieved
		}
	}

	public String getRequestId() {
		return requestId;
	}

	public String getOperationId() {
		return operationId;
	}
	
	private String getListPath() {
		return VABPathTools.concatenatePaths(OperationProvider.INVOCATION_LIST, requestId);
	}

}
