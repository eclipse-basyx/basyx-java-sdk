/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.map;

import java.util.Map;

import org.eclipse.basyx.vab.modelprovider.generic.IVABElementHandler;
import org.eclipse.basyx.vab.modelprovider.generic.VABModelProvider;



/**
 * A simple VAB model provider based on a LinkedHashMap.
 * 
 * This provider demonstrates the inclusion of new data sources to the VAB
 * 
 * @author kuhn, schnicke, espen
 *
 */
public class VABMapProvider extends VABModelProvider {
	public VABMapProvider(Map<String, Object> elements) {
		super(elements, new VABMapHandler());
	}

	protected VABMapProvider(Map<String, Object> elements, IVABElementHandler handler) {
		super(elements, handler);
	}

	@SuppressWarnings("unchecked")
	protected Map<String, Object> getElements() {
		return (Map<String, Object>) elements;
	}
}
