/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.authorization;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;

/**
 * Api provider for constructing a new AAS API that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingAASAPIFactory implements IAASAPIFactory {
	protected final IAASAPIFactory apiFactory;
	protected final IAASAPIPep aasAPIPep;

	public AuthorizedDecoratingAASAPIFactory(IAASAPIFactory factoryToBeDecorated, IAASAPIPep aasAPIPep) {
		this.apiFactory = factoryToBeDecorated;
		this.aasAPIPep = aasAPIPep;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		return new AuthorizedAASAPI(apiFactory.create(aas), aasAPIPep);
	}
}