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
package org.eclipse.basyx.submodel.metamodel.api.submodelelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.IElementContainer;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

/**
 * A submodel element collection is a set or list of submodel elements.
 * 
 * @author rajashek, schnicke
 *
 */
public interface ISubmodelElementCollection extends ISubmodelElement, IElementContainer {

	/**
	 * Gets if the collection is ordered or unordered
	 * 
	 * @return
	 */
	public boolean isOrdered();

	/**
	 * Gets if the collection allows duplicates
	 * 
	 * @return
	 */
	public boolean isAllowDuplicates();

	/**
	 * Gets all the elements contained in the collection
	 * 
	 * @return
	 */
	@Override
	public Map<String, ISubmodelElement> getSubmodelElements();

	/**
	 * Gets the data elements contained in the collection
	 * 
	 * @return
	 */
	@Override
	public Map<String, IProperty> getProperties();

	/**
	 * Gets the operations contained in the collection
	 * 
	 * @return
	 */
	@Override
	public Map<String, IOperation> getOperations();

	/**
	 * Gets a {@literal Map<IdShort, smElement.getValue() >} containing the values
	 * of all submodelElements
	 * 
	 * @return a Map with the values of all submodelElements
	 */
	public Map<String, Object> getValues();
}
