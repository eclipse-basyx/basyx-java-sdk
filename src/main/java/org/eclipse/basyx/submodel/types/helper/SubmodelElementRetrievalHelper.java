/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.types.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

public class SubmodelElementRetrievalHelper {
	
	public static List<ISubmodelElement> getSubmodelElementsByIdPrefix(String prefix, Map<String, ISubmodelElement> elemMap) {
		if (elemMap != null && elemMap.size() > 0) {
			return elemMap.values().stream().filter(s -> s.getIdShort().startsWith(prefix)).collect(Collectors.toList());
		}
		return new ArrayList<ISubmodelElement>();
	}
}
