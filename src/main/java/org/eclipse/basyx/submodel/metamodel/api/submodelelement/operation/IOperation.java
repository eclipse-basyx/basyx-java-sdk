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

import java.util.Collection;

import org.eclipse.basyx.submodel.metamodel.api.IElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;

/**
 * An operation is a submodel element with input and output variables.
 * 
 * @author kuhn, schnicke
 *
 */
public interface IOperation extends IElement, ISubmodelElement {

	/**
	 * Return operation parameter types (operation signature)
	 * 
	 * @return Parameter types
	 */
	public Collection<IOperationVariable> getInputVariables();

	/**
	 * Get operation return type
	 * 
	 * @return Operation return type
	 */
	public Collection<IOperationVariable> getOutputVariables();

	/**
	 * Get the parameters that are input and output of the operation.
	 * 
	 * @return
	 */
	public Collection<IOperationVariable> getInOutputVariables();

	/**
	 * Invoke operation with given parameter
	 * 
	 * @deprecated Please use either {@link #invoke(SubmodelElement...)} for passing
	 *             SubmodelElements or {@link #invokeSimple(Object...)} for directly
	 *             passing values.
	 * @param params
	 *            Operation parameter
	 * @return If multiple values are returned, Object is here a list of Objects
	 */
	@Deprecated
	public Object invoke(Object... params);
	
	/**
	 * Invoke operation with parameters wrapped as SubmodelElements
	 * 
	 * 
	 * @param elems
	 *               Operation parameters
	 * @return List of results
	 */
	public SubmodelElement[] invoke(SubmodelElement... elems);

	/**
	 * Invoke operation with raw parameters, i.e. not wrapped as SubmodelElements
	 * 
	 * @param params
	 *            Raw operation parameters
	 * @return Raw result
	 */
	public Object invokeSimple(Object... params);

	/**
	 * Invoke operation with given parameter asynchronously
	 * 
	 * @param params
	 *               Operation parameter
	 * @return An IAsyncInvocation
	 */
	public IAsyncInvocation invokeAsync(Object... params);

	/**
	 * Invoke operation with given parameter asynchronously and use a user-defined timeout
	 * 
	 * @param timeout
	 *                The timeout in ms
	 * @param params
	 *                Operation parameter
	 * @return An IAsyncInvocation
	 */
	public IAsyncInvocation invokeAsyncWithTimeout(int timeout, Object... params);
}
