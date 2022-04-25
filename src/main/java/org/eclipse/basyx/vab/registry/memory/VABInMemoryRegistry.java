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
package org.eclipse.basyx.vab.registry.memory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.registry.api.IVABRegistryService;

/**
 * Implement a in memory directory
 * 
 * @author kuhn
 *
 */
public class VABInMemoryRegistry implements IVABRegistryService {

	/**
	 * Map that stores key/value mappings
	 */
	protected Map<String, Object> keyToValue = new LinkedHashMap<>();

	/**
	 * Default constructor
	 */
	public VABInMemoryRegistry() {
		// Do nothing
	}

	/**
	 * Constructor that accepts initial entries
	 */
	public VABInMemoryRegistry(Map<String, String> addedValues) {
		keyToValue.putAll(addedValues);
	}

	/**
	 * Add a mapping to directory
	 */
	@Override
	public IVABRegistryService addMapping(String key, String value) {
		keyToValue.put(key, value);

		// Return 'this' instance
		return this;
	}

	/**
	 * Add several mappings to directory
	 */
	public void addMappings(Map<String, String> mappings) {
		keyToValue.putAll(mappings);
	}

	/**
	 * Remove a mapping from directory
	 */
	@Override
	public void removeMapping(String key) {
		keyToValue.remove(key);
	}

	/**
	 * Lookup method
	 */
	@Override
	public String lookup(String id) {
		if (keyToValue.containsKey(id)) {
			return (String) keyToValue.get(id);
		} else {
			throw new ResourceNotFoundException("No entry exists for key " + id);
		}
	}
}
