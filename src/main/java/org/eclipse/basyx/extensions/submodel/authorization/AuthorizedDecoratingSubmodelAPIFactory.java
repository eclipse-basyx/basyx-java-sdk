/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.authorization;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * Api provider for constructing a new SubmodelAPI that is authorized
 * 
 * @author espen
 */
public class AuthorizedDecoratingSubmodelAPIFactory implements ISubmodelAPIFactory {
	private ISubmodelAPIFactory submodelAPIFactory;

	public AuthorizedDecoratingSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		this.submodelAPIFactory = submodelAPIFactory;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		return new AuthorizedSubmodelAPI(submodelAPIFactory.create(submodel));
	}
}