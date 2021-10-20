/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.coder.json.serialization;

import java.util.Collection;
import java.util.Map;



/**
 * Factory that controls the kind of Maps and Collections that are produced when an Object is deserialized
 * 
 * @author kuhn, espen
 *
 */
public interface GSONToolsFactory {

	
	/**
	 * Create a Map
	 */
	public Map<String, Object> createMap();
	
	
	/**
	 * Create a Collection
	 */
	public Collection<Object> createCollection();
}
