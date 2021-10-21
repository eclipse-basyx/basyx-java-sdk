/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IAsyncInvocation;

/**
 * Local implementation of IAsyncInvocation.
 * 
 * @author conradi, espen
 *
 */
public class AsyncInvocation implements IAsyncInvocation {
	// Delayer for timeouts
	private static ScheduledThreadPoolExecutor delayer = new ScheduledThreadPoolExecutor(0);

	private String operationId;
	private CompletableFuture<Void> future;
	
	// This variable is used to write the result of the function into
	private Object result;
	
	private RuntimeException exception;
	
	@SuppressWarnings("unchecked")
	public AsyncInvocation(Operation operation, int timeout, Object... parameters) {
		operationId = operation.getIdShort();
		
		Function<Object[], Object> invokable = (Function<Object[], Object>) operation.get(Operation.INVOKABLE);
		
		// This future executes the given function and a timeout future.
		// It finishes when either the timeout is over or the function finishes.
		
		// The result of the function is written into the "result" variable
		// as it can not be returned through the Future due to the timeout check
		future = CompletableFuture.supplyAsync(
				// Run Operation asynchronously
				() -> invokable.apply(parameters))
				// Accept either result or throw exception on timeout
				.acceptEither(setTimeout(timeout),
					// result accepted => write result (or timeout exception)
						futureResult -> this.result = futureResult
				).exceptionally(throwable -> {
					// result not accepted? set operation state
					if (throwable.getCause() instanceof OperationExecutionTimeoutException) {
						exception = (RuntimeException) throwable.getCause();
					} else {
						// result not accepted? set operation state
						exception = new OperationExecutionErrorException(
								"Exception while executing Operation Operation '" + operationId + "'", throwable);
					}
					return null;
				});
	}
	
	/**
	 * Function for scheduling a timeout function with completable futures
	 */
	private CompletableFuture<Void> setTimeout(int timeout) {
		CompletableFuture<Void> timeoutFuture = new CompletableFuture<>();
		delayer.schedule(
				() -> timeoutFuture.completeExceptionally(
						new OperationExecutionTimeoutException("Operation " + operationId + " timed out")),
				timeout, TimeUnit.MILLISECONDS);
		return timeoutFuture;
	}

	@Override
	public Object getResult() {
		try {
			// The future itself always returns null (<Void>)
			// Actual return value is written into result variable inside future
			future.get();
		} catch (Exception e) {
			// Some RuntimeException occured when finishing the future
			throw new OperationExecutionErrorException(
					"Exception while executing Operation '" + operationId + "'", e.getCause());
		}
		if (exception instanceof OperationExecutionTimeoutException
				|| exception instanceof OperationExecutionErrorException) {
			// Future finished with an exception
			throw exception;
		}
		return result;
	}
	
	@Override
	public boolean isFinished() {
		return future.isDone();
	}

	public String getOperationId() {
		return operationId;
	}
}
