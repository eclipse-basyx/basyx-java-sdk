/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
	 * @param id
	 * @return submodel element
	 */
	ISubmodelElement getSubmodelElement(String id);
	
	/**
	 * Deletes a submodel element by name
	 * @param id
	 */
	void deleteSubmodelElement(String id);
}
