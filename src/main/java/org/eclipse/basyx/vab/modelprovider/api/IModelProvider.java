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
