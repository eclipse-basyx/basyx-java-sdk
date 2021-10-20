/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.restapi.vab;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Submodel API provider that provides the default VAB Submodel API
 * 
 * @author espen
 *
 */
public class VABSubmodelAPIFactory implements ISubmodelAPIFactory {
	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new VABSubmodelAPI(new VABLambdaProvider(submodel));
	}
}
