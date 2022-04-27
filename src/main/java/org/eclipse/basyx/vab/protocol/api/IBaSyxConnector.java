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
package org.eclipse.basyx.vab.protocol.api;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Connector interface for technology specific communication. Returns the
 * response including meta information
 * 
 * @author pschorn
 *
 */
public interface IBaSyxConnector {

	/**
	 * Get a sub model property value
	 * 
	 * @param path
	 *            Path to the requested value
	 * @return Property value. Object type is assumed to be [Integer | ... |
	 *         Collection]
	 */
	public String getValue(String path) throws ProviderException;

	/**
	 * Sets or overrides existing property, operation or event.
	 * 
	 * @param path
	 *            Path to the requested value
	 * @param newValue
	 *            Updated value
	 */
	public String setValue(String path, String newValue) throws ProviderException;

	/**
	 * Create a new property, operation, event submodel or aas under the given path
	 * 
	 * @param path
	 *            Path to the entity where the element should be created
	 * @param newEntity
	 *            new Element to be created on the server
	 */
	public String createValue(String path, String newEntity) throws ProviderException;

	/**
	 * Delete a property, operation, event, submodel or aas under the given path
	 * 
	 * @param path
	 *            Path to the entity that should be deleted
	 */
	public String deleteValue(String path) throws ProviderException;

	/**
	 * Deletes an entry from a map or collection by the given key
	 * 
	 * @param path
	 *            Path to the entity that should be deleted
	 */
	public String deleteValue(String path, String obj) throws ProviderException;

	/**
	 * Invoke an operation
	 *
	 * @param path
	 *            Path to operation
	 * @param jsonObject
	 *            Operation parameter
	 * @return Return value
	 */
	public String invokeOperation(String path, String jsonObject) throws ProviderException;

	/**
	 * Get string representation of endpoint for given path for debugging.
	 * 
	 * @param path
	 *            Requested path
	 * @return String representing requested endpoint
	 */
	public String getEndpointRepresentation(String path);
}
