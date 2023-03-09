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
package org.eclipse.basyx.submodel.restapi.api;

import java.util.Collection;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

/**
 * Specifies overall Submodel API
 * 
 * @author schnicke
 *
 */
public interface ISubmodelAPI {
	
	/**
	 * Retrieves the underlying submodel
	 * 
	 * @return the submodel
	 */
	public ISubmodel getSubmodel();

	/**
	 * Adds a submodelElement to the submodel
	 * 
	 * @param elem
	 *            the submodelElement to be added
	 */
	public void addSubmodelElement(ISubmodelElement elem);

	/**
	 * Adds a submodelElement to the submodel
	 * 
	 * @param idShortPath
	 *            the idShort path to the submodelElement
	 * @param elem
	 *            the submodelElement to be added
	 */
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem);

	/**
	 * Retrieves a submodelElement
	 * 
	 * @param idShortPath
	 *            the idShort Path to the submodelElement
	 * @return the submodelElement
	 */
	public ISubmodelElement getSubmodelElement(String idShortPath);

	/**
	 * Removes a submodelElement from the submodel
	 * 
	 * @param idShortPath
	 *            the idShort path to the submodelElement, which is to be removed
	 */
	public void deleteSubmodelElement(String idShortPath);

	/**
	 * Helper function for quick access of operations
	 * 
	 * @return all operations contained by the submodel
	 */
	public Collection<IOperation> getOperations();

	/**
	 * Helper function for quick access of submodelElements
	 * 
	 * @return all submodelElements contained by the submodel
	 */
	public Collection<ISubmodelElement> getSubmodelElements();

	/**
	 * Updates the value of a submodelElement
	 * 
	 * @param idShortPath
	 *            the idShort path to the submodelElement
	 * @param newValue
	 *            new value of the submodelElement
	 */
	public void updateSubmodelElement(String idShortPath, Object newValue);

	/**
	 * Retrieves the value of a submodelElement
	 * 
	 * @param idShortPath
	 *            the idShort path to the submodelElement
	 * @return submodelElement value
	 */
	public Object getSubmodelElementValue(String idShortPath);

	/**
	 * Invokes an operation
	 * 
	 * @param idShortPath
	 *            the idShort path to the operation
	 * @param params
	 *            to be passed to the operation
	 * @return the result of the operation
	 */
	public Object invokeOperation(String idShortPath, Object... params);

	/**
	 * Invoke an operation asynchronously
	 * 
	 * @param idShortPath
	 *            the idShort path to the operation
	 * @param params
	 *            to be passed to the operation
	 * @return the requestId of the invocation
	 */
	public Object invokeAsync(String idShortPath, Object... params);

	/**
	 * Gets the result of an asynchronously invoked operation
	 * 
	 * @param idShort
	 *            of the operation
	 * @param requestId
	 *            the requestId of the invocation
	 * @return the result of the Operation or a Message that it is not finished yet
	 */
	public Object getOperationResult(String idShort, String requestId);

	default public Object getSubmodelElementFile(String idShortPath) {
		throw new NotImplementedException();
	}

}
