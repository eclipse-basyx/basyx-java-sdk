/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.registry.descriptor.parts;

import java.util.Map;

import org.eclipse.basyx.vab.model.VABModelMap;

/*
 * TODO: will be implemented according to "Details of the Asset Administration Shell Part 1 V3".
 * 
 * GlobalAssetId class stub
 * 
 */
public class GlobalAssetId extends VABModelMap<Object> {
	public GlobalAssetId() {

	}

	public static GlobalAssetId createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		GlobalAssetId facade = new GlobalAssetId();
		facade.setMap(obj);
		return facade;
	}
}