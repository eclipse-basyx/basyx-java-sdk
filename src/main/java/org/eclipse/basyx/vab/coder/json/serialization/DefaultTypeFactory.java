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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Default type factory
 *
 * @author kuhn
 *
 */
public class DefaultTypeFactory implements GSONToolsFactory {

	/**
	 * Create a map
	 */
	@Override
	public Map<String, Object> createMap() {
		return new LinkedHashMap<>();
	}


	/**
	 * Create a collection
	 */
	@Override
	public Collection<Object> createCollection() {
		return new ArrayList<>();
	}
}
