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

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * This class is used to throw exception when
 * metamodel's createAsFacade from map does not work
 * due to absence of mandatory fields
 * 
 * @author haque
 *
 */
public class MetamodelConstructionException extends ProviderException {
	private static final long serialVersionUID = 1L;
	
	public MetamodelConstructionException(Class<?> clazz , Map<String, Object> map) {
		super("Could not construct meta model element " + clazz.getName() + ". Passed argument was " + map.toString());
	}
	
	public MetamodelConstructionException(String exceptionMessage) { 
		super(exceptionMessage);
	}
}
