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
package org.eclipse.basyx.submodel.metamodel.api;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

/**
 * Base interface for elements containing submodel elements
 * 
 * @author schnicke
 *
 */
public interface IElementContainer {
	/**
	 * Adds a submodel element
	 * 
	 * @param element
	 */
	public void addSubmodelElement(ISubmodelElement element);

	/**
	 * Gets all contained submodel elements
	 * 
	 * @return
	 */
	public Map<String, ISubmodelElement> getSubmodelElements();

	/**
	 * Gets only submodel elements that are properties
	 * 
	 * @return
	 */
	public Map<String, IProperty> getProperties();

	/**
	 * Gets only submodel elements that are operations
	 * 
	 * @return
	 */
	public Map<String, IOperation> getOperations();

	/**
	 * Gets a submodel element by name
	 * 
	 * @param id
	 * @return submodel element
	 */
	ISubmodelElement getSubmodelElement(String id);

	/**
	 * Deletes a submodel element by name
	 * 
	 * @param id
	 */
	void deleteSubmodelElement(String id);
}
