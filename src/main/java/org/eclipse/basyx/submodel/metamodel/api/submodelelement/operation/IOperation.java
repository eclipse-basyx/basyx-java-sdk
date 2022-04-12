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
