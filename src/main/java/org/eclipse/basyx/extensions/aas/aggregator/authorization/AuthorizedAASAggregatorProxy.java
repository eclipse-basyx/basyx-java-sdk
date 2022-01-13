/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.vab.coder.json.connector.JSONConnector;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;

/**
 * Local proxy class that hides HTTP calls to BaSys aggregator with enabled
 * authorization.
 * 
 * @author jungjan, fischer, fried
 */
public class AuthorizedAASAggregatorProxy extends AASAggregatorProxy {

	/**
	 * Constructor for an AAS aggregator proxy based on a HTTP connection
	 *
	 * @param aasAggregatorUrl
	 *            The endpoint of the aggregator
	 * @param authorizationSupplier
	 *            Supplier for values to be placed in the HTTP Authorization request
	 *            header
	 */
	public AuthorizedAASAggregatorProxy(String aasAggregatorURL, IAuthorizationSupplier authorizationSupplier) {
		super(new JSONConnector(new HTTPConnector(harmonizeURL(aasAggregatorURL), authorizationSupplier)));
	}

}
