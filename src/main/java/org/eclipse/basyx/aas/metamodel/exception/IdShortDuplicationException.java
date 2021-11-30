/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.exception;

import java.util.Map;

/**
 * This class represents the exception when there are two
 * Submodel elements that has same IdShort.
 * 
 * @author danish
 *
 */
public class IdShortDuplicationException extends MetamodelConstructionException {
	private static final long serialVersionUID = 1L;

	public IdShortDuplicationException(Class<?> clazz, Map<String, Object> map) {
		super(clazz, map);
	}
	
	public IdShortDuplicationException(Map<String, Object> map) {
		super("Could not construct meta model element due to IdShort duplication. Passed argument was " + map.toString());
	}

}
