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
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Api provider for constructing a new AAS API that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingAASAPIFactory implements IAASAPIFactory {
	private IAASAPIFactory apiFactory;

	public AuthorizedDecoratingAASAPIFactory(IAASAPIFactory factoryToBeDecorated) {
		this.apiFactory = factoryToBeDecorated;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		return new AuthorizedAASAPI(apiFactory.create(aas));
	}

	@Override
	public IAASAPI create(IIdentifier aasId) {
		return new AuthorizedAASAPI(apiFactory.create(aasId));
	}
}