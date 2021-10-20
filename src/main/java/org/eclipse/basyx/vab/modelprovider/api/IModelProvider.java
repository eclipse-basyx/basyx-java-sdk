/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.api;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Basic model provider backend interface
 * 
 * @author kuhn, pschorn, schnicke
 *
 */
public interface IModelProvider {

	/**
	 * Gets a value stored in a given path
	 * 
	 * @param path
	 *            Path to the requested value
	 * @return Object type is assumed to be [Integer | ... | Collection]
	 */
	public Object getValue(String path) throws ProviderException;

	/**
	 * Sets or overrides existing value in a given path
	 * 
	 * @param path
	 *            Path to the requested value
	 * @param newValue
	 *            Updated value
	 */
	public void setValue(String path, Object newValue) throws ProviderException;

	/**
	 * Create a new value under the given path
	 * 
	 * @param path
	 *            Path to the entity where the element should be created
	 * @param newEntity
	 *            new Element to be created on the server
	 */
	public void createValue(String path, Object newEntity) throws ProviderException;

	/**
	 * Deletes value under the given path
	 * 
	 * @param path
	 *            Path to the entity that should be deleted
	 */
	public void deleteValue(String path) throws ProviderException;

	/**
	 * Deletes an entry from a map or collection by the given key
	 * 
	 * @param path
	 *            Path to the entity that should be deleted
	 */
	public void deleteValue(String path, Object obj) throws ProviderException;

	/**
	 * Invoke an operation
	 *
	 * @param path
	 *            Path to operation
	 * @param parameter
	 *            Operation parameter
	 * @return Return value
	 */
	public Object invokeOperation(String path, Object... parameter) throws ProviderException;

}
