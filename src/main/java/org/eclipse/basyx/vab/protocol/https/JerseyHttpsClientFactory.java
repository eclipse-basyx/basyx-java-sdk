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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
	 * Returns an HTTPS client
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
    public static Client getJerseyHTTPSClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = getSslContext(PROTOCOL);
        HostnameVerifier allHostsValid = new DefaultHostNameVerifier();

        return ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier(allHostsValid)
                .build();
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
