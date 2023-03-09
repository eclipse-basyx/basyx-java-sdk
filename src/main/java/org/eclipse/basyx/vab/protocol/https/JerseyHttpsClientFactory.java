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
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.glassfish.jersey.client.JerseyClientBuilder;

/**
 * A Factory class containing methods creating an HTTPS client with no
 * verification and validation for self signed SSL and other helper methods
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
		return new JerseyClientBuilder().sslContext(sslContext).hostnameVerifier(hostnameVerifier).build();
	}

	/**
	 * Retrieves an SSL Context with given protocol
	 * 
	 * @param protocol
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private static SSLContext getSslContext(String protocol) throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance(protocol);

		KeyManager[] keyManagers = null;
		TrustManager[] trustManager = { new DefaultTrustManager() };
		SecureRandom secureRandom = new SecureRandom();

		sslContext.init(keyManagers, trustManager, secureRandom);

		return sslContext;
	}
}
