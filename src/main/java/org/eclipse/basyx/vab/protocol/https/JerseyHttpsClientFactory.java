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
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;

/**
 * A Factory class containing methods creating an HTTPS client
 * with no verification and validation for self signed SSL
 * and other helper methods
 * 
 * @author haque
 *
 */
public class JerseyHttpsClientFactory {
	private static final String PROTOCOL = "TLSv1.2";

	/**
	 * Returns an HTTPS client without validation
	 * 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
    public static Client getJerseyHTTPSClientWithoutValidation() throws KeyManagementException, NoSuchAlgorithmException {
		return getJerseyHTTPSClient(new NonVerifyingHostnameVerifier());
    }

	/**
	 * Returns an HTTPS client
	 * 
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static Client getJerseyHTTPSClientWithValidation() throws KeyManagementException, NoSuchAlgorithmException {
		return getJerseyHTTPSClient(new DefaultHostnameVerifier());
	}


	private static Client getJerseyHTTPSClient(HostnameVerifier hostnameVerifier) throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslContext = getSslContext(PROTOCOL);
		return ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(hostnameVerifier).build();
	}

    /**
     * Retrieves an SSL Context
     * with given protocol
     * @param protocol
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext getSslContext(String protocol) throws NoSuchAlgorithmException,
                                                     KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(protocol);

        KeyManager[] keyManagers = null;
        TrustManager[] trustManager = {new DefaultTrustManager()};
        SecureRandom secureRandom = new SecureRandom();

        sslContext.init(keyManagers, trustManager, secureRandom);

        return sslContext;
    }
}
