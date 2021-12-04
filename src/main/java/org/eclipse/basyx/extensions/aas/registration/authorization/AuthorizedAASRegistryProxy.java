/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.authorization;

import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;

/**
 * Local proxy class that hides HTTP calls to BaSys registry with enabled authorization.
 *
 * @author pneuschwander
 */
public class AuthorizedAASRegistryProxy extends AASRegistryProxy {

	/**
	 * Constructor for an AAS registry proxy based on a HTTP connection
	 *
	 * @param registryUrl           The endpoint of the registry with a HTTP-REST interface
	 * @param authorizationSupplier Supplier for values to be placed in the HTTP Authorization request header
	 */
	public AuthorizedAASRegistryProxy(final String registryUrl, final IAuthorizationSupplier authorizationSupplier) {
		super(new JSONConnector(new HTTPConnector(harmonizeURL(registryUrl), authorizationSupplier)));
	}

}
