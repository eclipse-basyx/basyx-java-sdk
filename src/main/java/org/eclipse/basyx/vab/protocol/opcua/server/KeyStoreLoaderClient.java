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
package org.eclipse.basyx.vab.protocol.opcua.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

import org.eclipse.basyx.vab.protocol.opcua.CertificateHelper;
import org.eclipse.basyx.vab.protocol.opcua.connector.ClientConfiguration;
import org.eclipse.milo.opcua.sdk.server.util.HostnameUtil;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated As of version 1.1. No full replacement planned. <br>
 * 			A limited replacement is available in {@link CertificateHelper}
 *             which creates self-signed certificates in memory to be passed to
 *             {@link ClientConfiguration#setKeyPairAndCertificate(KeyPair, X509Certificate)}.
 *             But it is the user's responsibility to persist these in a
 *             {@link KeyStore}, if they wish.
 *
 */
@Deprecated
public class KeyStoreLoaderClient {

	private static final Pattern IP_ADDR_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private static final String CLIENT_ALIAS = "client-ai";
	private static final char[] PASSWORD = "password".toCharArray();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private X509Certificate clientCertificate;
	private KeyPair clientKeyPair;

	KeyStoreLoaderClient load(Path baseDir) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

		Path serverKeyStore = baseDir.resolve("example-client.pfx");

		logger.info("Loading KeyStore at {}", serverKeyStore);

		if (!Files.exists(serverKeyStore)) {
			keyStore.load(null, PASSWORD);

			KeyPair keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);

			SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair).setCommonName("Eclipse Milo Example Client").setOrganization("digitalpetri").setOrganizationalUnit("dev").setLocalityName("Folsom")
					.setStateName("CA").setCountryCode("US").setApplicationUri("urn:eclipse:milo:examples:client").addDnsName("localhost").addIpAddress("127.0.0.1");

			// Get as many hostnames and IP addresses as we can listed in the certificate.
			for (String hostname : HostnameUtil.getHostnames("0.0.0.0")) {
				if (IP_ADDR_PATTERN.matcher(hostname).matches()) {
					builder.addIpAddress(hostname);
				} else {
					builder.addDnsName(hostname);
				}
			}

			X509Certificate certificate = builder.build();

			keyStore.setKeyEntry(CLIENT_ALIAS, keyPair.getPrivate(), PASSWORD, new X509Certificate[] { certificate });
			try (OutputStream out = Files.newOutputStream(serverKeyStore)) {
				keyStore.store(out, PASSWORD);
			}
		} else {
			try (InputStream in = Files.newInputStream(serverKeyStore)) {
				keyStore.load(in, PASSWORD);
			}
		}

		Key serverPrivateKey = keyStore.getKey(CLIENT_ALIAS, PASSWORD);
		if (serverPrivateKey instanceof PrivateKey) {
			clientCertificate = (X509Certificate) keyStore.getCertificate(CLIENT_ALIAS);
			PublicKey serverPublicKey = clientCertificate.getPublicKey();
			clientKeyPair = new KeyPair(serverPublicKey, (PrivateKey) serverPrivateKey);
		}

		return this;
	}

	X509Certificate getClientCertificate() {
		return clientCertificate;
	}

	KeyPair getClientKeyPair() {
		return clientKeyPair;
	}

}
