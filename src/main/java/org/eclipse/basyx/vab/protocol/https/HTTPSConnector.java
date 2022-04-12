/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
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
