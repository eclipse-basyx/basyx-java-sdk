/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.api;

import java.util.Collection;

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
	 * @param  elem
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
	 * 			the idShort path to the operation
	 * @param params
	 * 			to be passed to the operation
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

}
