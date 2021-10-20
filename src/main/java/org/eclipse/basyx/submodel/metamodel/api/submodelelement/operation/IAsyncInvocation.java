/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation;

/**
 * An AsyncInvocation is used for asynchronously invoking operation
 * 
 * @author conradi
 *
 */
public interface IAsyncInvocation {
	
	/**
	 * Gets the result of the async Invocation<br>
	 * Will block if execution is not finished yet
	 * 
	 * @return the result of the Invocation
	 */
	public Object getResult();
	
	/**
	 * Gets the status of the async Invocation
	 * 
	 * @return true if execution is completed; false otherwise
	 */
	public boolean isFinished();
	
}
