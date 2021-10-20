/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.helper;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Contains helper methods of element container
 * @author haque
 *
 */
public class ElementContainerHelper {
	
	public static ISubmodelElement getElementById(Map<String, ISubmodelElement> elements, String id) {
		if (elements != null && elements.containsKey(id)) {
			return elements.get(id);
		}
		throw new ResourceNotFoundException("Submodel Element with id: " + id + " does not exist");
	}
	
	public static void removeElementById(Map<String, ISubmodelElement> elements, String id) {
		if (elements != null && elements.containsKey(id)) {
			elements.remove(id);
			return;
		}
		throw new ResourceNotFoundException("Submodel Element with id: " + id + " does not exist");
	}
}
