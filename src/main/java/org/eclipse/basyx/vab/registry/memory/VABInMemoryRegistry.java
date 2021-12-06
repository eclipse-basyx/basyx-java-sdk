/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
		if(keyToValue.containsKey(id)) {
			return (String) keyToValue.get(id);
		} else {
			throw new ResourceNotFoundException("No entry exists for key " + id);
		}
	}
}
