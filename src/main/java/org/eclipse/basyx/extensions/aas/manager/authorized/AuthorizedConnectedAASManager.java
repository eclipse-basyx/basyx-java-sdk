/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.manager.authorized;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;
import org.eclipse.basyx.vab.protocol.https.HTTPSConnectorProvider;

/**
 * A ConnectedAASManager that uses a HTTPSConnector with authorization.
 * Optionally, an authorized registry can also be created with an registry-URL
 * 
 * @author mueller-zhang, espen
 */
public class AuthorizedConnectedAASManager extends ConnectedAssetAdministrationShellManager {

	/**
	 * Constructor to create a ConnectedAASManager with an user defined registry and
	 * authorization supplier
	 * 
	 * @param registry
	 *            an user defined registry
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedConnectedAASManager(IAASRegistry registry, IAuthorizationSupplier authorizationSupplier) {
		super(registry, new HTTPSConnectorProvider(authorizationSupplier));
	}

	/**
	 * Constructor to create a ConnectedAASManager with authorized registry and
	 * authorization supplier
	 * 
	 * @param registryUrl
	 *            registry url to create a registry with authorization
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedConnectedAASManager(String registryUrl, IAuthorizationSupplier authorizationSupplier) {
		super(new AuthorizedAASRegistryProxy(registryUrl, authorizationSupplier), new HTTPSConnectorProvider(authorizationSupplier));
	}


}
