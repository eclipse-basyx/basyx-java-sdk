/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.https;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.basyx.vab.protocol.http.connector.IAuthorizationSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An HTTPS Connector class which can be used
 * for creating an HTTPS Client with self signed SSL certificate
 * and communicating in REST 
 * 
 * @author haque
 *
 */
public class HTTPSConnector extends HTTPConnector {
	private static Logger logger = LoggerFactory.getLogger(HTTPSConnector.class);
	
	/**
	 * Initiates an HTTPSConnector with given address
	 * @param address
	 */
	public HTTPSConnector(String address) {
		super(address);
		setHttpsClientWithValidation();
	}
	
	/**
	 * Initiates an HTTPSConnector with given address and media type
	 * @param address
	 * @param mediaType
	 */
	public HTTPSConnector(String address, String mediaType) { 
		super(address, mediaType);
		setHttpsClientWithValidation();
	}
	
	public HTTPSConnector(String address, IAuthorizationSupplier authorizationSupplier) {
		super(address, authorizationSupplier);
		setHttpsClientWithValidation();
	}

	public HTTPSConnector(String address, IAuthorizationSupplier authorizationSupplier, boolean validateFlag) {
		super(address, authorizationSupplier);
		if (validateFlag) {
			setHttpsClientWithValidation();
		} else {
			setHttpsClientWithoutValidation();
		}
	}

	/**
	 * Configures the client so that it can run with HTTPS protocol
	 */
	private void setHttpsClientWithoutValidation() {
		try {
			this.client = JerseyHttpsClientFactory.getJerseyHTTPSClientWithoutValidation();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("Cannot create a https client");
		}
	}

	private void setHttpsClientWithValidation() {
		try {
			this.client = JerseyHttpsClientFactory.getJerseyHTTPSClientWithValidation();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("Cannot create a https client");
		}
	}
}
