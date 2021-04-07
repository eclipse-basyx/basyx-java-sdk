/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.modelprovider.lambda;

import java.util.Map;

import org.eclipse.basyx.vab.modelprovider.generic.VABModelProvider;

/**
 * Provider that optionally allows properties to be modifiable by hidden
 * set/get/insert/remove property. <br>
 * Supports nested lambda-expressions. <br>
 * E.g.:<br>
 * GET $path is internally delegated to a call of $path/get which is a
 * {@link java.util.function.Consumer}. <br>
 * SET $path is delegated to $path/set which is a
 * {@link java.util.function.Supplier}.
 * 
 * @author schnicke, espen
 *
 */
public class VABLambdaProvider extends VABModelProvider {
	public VABLambdaProvider(Map<String, Object> elements) {
		super(elements, new VABLambdaHandler());
	}
}
