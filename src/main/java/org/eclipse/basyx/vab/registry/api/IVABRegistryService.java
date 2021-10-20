/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.registry.api;

/**
 * Directory service SDK interface.
 * 
 * @author kuhn
 *
 */
public interface IVABRegistryService {

	
	/**
	 * Add a mapping to directory
	 */
	public IVABRegistryService addMapping(String key, String value);

	
	/**
	 * Remove a mapping from directory
	 */
	public void removeMapping(String key);
	

	/**
	 * Lookup method maps key "id" to value
	 */
	public String lookup(String id);
}
