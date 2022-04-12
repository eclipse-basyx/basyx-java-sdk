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
package org.eclipse.basyx.vab.modelprovider.consistency;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public class ConsistencyProvider<T extends IModelProvider> implements IModelProvider {

	/**
	 * Reference to IModelProvider backend
	 */
	protected T providerBackend = null;

	/**
	 * Constructor
	 */
	public ConsistencyProvider(T modelProviderBackend) {
		// Store reference to backend
		providerBackend = modelProviderBackend;
	}

	/**
	 * Get backend reference
	 */
	public T getBackendReference() {
		return providerBackend;
	}

	/**
	 * Server Clock that gets incremented when a property of this submodel is
	 * changed
	 */
	protected Integer clock = 0;

	/**
	 * Makes this provider block any write requests
	 */
	private boolean frozen = false;

	/**
	 * Increments the clock property for the given submodel
	 * 
	 * @param submodelPath
	 */
	private void incrementClock() {
		this.clock += 1;
	}

	private boolean isFrozen() {
		return this.frozen;
	}

	@Override
	public Object getValue(String path) throws ProviderException {

		if (path.endsWith("/frozen")) {
			return this.frozen;
		} else if (path.endsWith("/clock")) {
			return this.clock;
		} else {
			return providerBackend.getValue(path);
		}
	}

	/**
	 * Validate frozen property to make sure the submodel is not read-only, increase
	 * clock
	 * 
	 * @param path
	 * @param newValue
	 * @throws ProviderException
	 */
	@Override
	public void setValue(String path, Object newValue) throws ProviderException {

		if (path.endsWith("/frozen")) {

			// Set frozen property
			this.frozen = (boolean) newValue;

		} else if (this.frozen == false) {

			// Increment Clock
			incrementClock();

			// Set the value of the element
			providerBackend.setValue(path, newValue);

		} else {
			throw new ProviderException("Value " + path + " is read only");
		}

	}

	/**
	 * Create new Entity, check if submodel is frozen
	 * 
	 * @param path
	 * @param newEntity
	 * @throws ProviderException
	 */
	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {

		if (!isFrozen() || path.endsWith("/frozen")) {
			providerBackend.createValue(path, newEntity);
		}
	}

	/**
	 * Delete entity, check if submodel is frozen
	 * 
	 * @param path
	 * @throws ProviderException
	 */
	@Override
	public void deleteValue(String path) throws ProviderException {

		if (!isFrozen() || path.endsWith("/frozen")) {
			providerBackend.deleteValue(path);
		}

	}

	/**
	 * Delete value from collection or map, check if submodel is frozen and increase
	 * clock
	 * 
	 * @param path
	 * @param obj
	 * @throws ProviderException
	 */
	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {

		if (!isFrozen() || path.endsWith("/frozen")) {
			// Increment Clock
			incrementClock();

			// Set the value of the element
			providerBackend.deleteValue(path, obj);

		} else {
			throw new ProviderException("Value " + path + " is read only");
		}

	}

	/**
	 * Invoke and operation FIXME address security problems
	 * 
	 * @param path
	 * @param parameter
	 * @return
	 * @throws ProviderException
	 */
	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		return providerBackend.invokeOperation(path, parameter);
	}

}
