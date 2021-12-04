/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.registration.memory;

import java.util.LinkedHashMap;

/**
 * An implementation of the IAASRegistryService interface.
 * This registry can not store its entries permanently, because it is completely based on HashMaps.
 * 
 * @author espen
 *
 */
public class InMemoryRegistry extends AASRegistry {

	/**
	 * Default constructor based on HashMaps
	 */
	public InMemoryRegistry() {
		super(new MapRegistryHandler(new LinkedHashMap<>()));
	}
}
