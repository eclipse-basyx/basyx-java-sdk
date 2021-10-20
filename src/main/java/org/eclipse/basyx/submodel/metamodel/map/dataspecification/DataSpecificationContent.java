/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.vab.model.VABModelMap;

public class DataSpecificationContent extends VABModelMap<Object> implements IDataSpecificationContent {
	/**
	 * Creates a DataSpecificationContent object from a map
	 * 
	 * @param obj
	 *            a DataSpecificationContent object as raw map
	 * @return a DataSpecificationContent object, that behaves like a facade for the given map
	 */
	public static DataSpecificationContent createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		DataSpecificationContent ret = new DataSpecificationContent();
		ret.setMap(map);
		return ret;
	}
}
