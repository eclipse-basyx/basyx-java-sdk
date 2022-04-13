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
package org.eclipse.basyx.vab.modelprovider;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Proxy class for a VAB element
 * 
 * @author kuhn
 *
 */
public class VABElementProxy implements IModelProvider {

	private static Logger logger = LoggerFactory.getLogger(VABElementProxy.class);

	/**
	 * Connector specific target address
	 */
	private String addr = "";

	/**
	 * IModelProvider that connects to the target address
	 */
	protected IModelProvider provider = null;

	/**
	 * Creates the proxy based on a specific model provider. E.g, if the element
	 * resides on <i>basyx://127.0.0.1</i> in the path <i>a/b/c</i>, <i>provider</i>
	 * would realize the connection to <i>basyx://127.0.0.1</i> and <i>addr</i>
	 * would be <i>a/b/c</i>. The VABElementProxy then directly points to the
	 * element.
	 * 
	 * @param addr
	 *            Address "within" the provider
	 * @param provider
	 *            The provider this proxy is based on
	 * 
	 */
	public VABElementProxy(String addr, IModelProvider provider) {
		// Store references
		this.addr = VABPathTools.stripSlashes(addr);
		this.provider = provider;
	}

	/**
	 * Read VAB element value
	 */
	@Override
	public Object getValue(String elementPath) throws ProviderException {
		// Get element from server
		try {
			return provider.getValue(constructPath(elementPath));
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in getModelPropertyValue", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Update VAB element value <br>
	 * <br>
	 * If the element does not exist it will be created<br>
	 */
	@Override
	public void setValue(String elementPath, Object newValue) throws ProviderException {
		// Set property value
		try {
			// Change element on server
			provider.setValue(constructPath(elementPath), newValue);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in setModelPropertyValue", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Add element on server
	 */
	@Override
	public void createValue(String elementPath, Object newValue) throws ProviderException {
		// Set property value
		try {
			// Create new element on server
			provider.createValue(constructPath(elementPath), newValue);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in createValue", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Delete element from server
	 */
	@Override
	public void deleteValue(String elementPath) throws ProviderException {
		// Delete property from server
		try {
			// Delete element from server
			provider.deleteValue(constructPath(elementPath));
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in deleteValue", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Delete element from server
	 */
	@Override
	public void deleteValue(String elementPath, Object value) throws ProviderException {
		// Delete property from server
		try {
			// Delete element from server
			provider.deleteValue(constructPath(elementPath), value);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in deleteValue", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Invoke element as an operation
	 */
	@Override
	public Object invokeOperation(String elementPath, Object... parameter) throws ProviderException {
		// Invoke operation on server
		try {
			// Invoke server operation
			return provider.invokeOperation(constructPath(elementPath), parameter);
		} catch (ProviderException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Exception in invokeOperation", e);
			throw new ProviderException(e);
		}
	}

	/**
	 * Add path to VAB element address. Make sure that resulting path contains the
	 * proper number of slashes ("/")
	 * 
	 * @param path
	 *            Input path
	 * @return processed path
	 */
	private String constructPath(String path) {
		if (path == null) {
			return null;
		}

		// Trim input path
		path = VABPathTools.stripSlashes(path);

		// Now combine both paths
		if (path.isEmpty()) {
			return addr;
		} else if (addr != null && !addr.isEmpty()) {
			if (path.startsWith("?")) {
				return addr + path;
			} else {
				return addr + "/" + path;
			}
		} else {
			return path;
		}
	}

	/**
	 * Creates a proxy object pointing to an object deeper within the element the
	 * current proxy is pointing to
	 * 
	 * @param path
	 * @return
	 */
	public VABElementProxy getDeepProxy(String path) {
		return new VABElementProxy(constructPath(path), provider);
	}
}
