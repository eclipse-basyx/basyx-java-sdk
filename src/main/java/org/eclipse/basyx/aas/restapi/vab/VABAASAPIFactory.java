/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.restapi.vab;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * AAS API provider that provides the default VAB AAS API
 * 
 * @author espen
 */
public class VABAASAPIFactory implements IAASAPIFactory {
	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		return new VABAASAPI(new VABLambdaProvider(aas));
	}
}
