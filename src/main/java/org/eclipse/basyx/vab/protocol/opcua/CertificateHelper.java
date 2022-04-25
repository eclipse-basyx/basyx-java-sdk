/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
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
package org.eclipse.basyx.vab.protocol.opcua;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateBuilder;
import org.eclipse.milo.opcua.stack.core.util.SelfSignedCertificateGenerator;

/**
 * Builds self-signed X.509 certificates.
 */
public class CertificateHelper {
	private boolean buildDefault = true;
	private KeyPair keyPair;
	private X509Certificate certificate;
	private String commonName;
	private String organization;
	private String organizationalUnit;
	private String locality;
	private String state;
	private String countryCode;
	private String applicationUri;
	private Set<InetAddress> ipAddresses = new HashSet<>();
	private Set<String> dnsNames = new CopyOnWriteArraySet<>();

	/**
	 * Creates a new {@link CertificateHelper}.
	 *
	 * <p>
	 * This constructor generates a new RSA-2048 key pair used for signing the
	 * certificate. That's why it might take an appreciable amount of time to
	 * return.
	 */
	public CertificateHelper() {
		try {
			// We use RSA 2048 because it is well-supported and can't fail.
			keyPair = SelfSignedCertificateGenerator.generateRsaKeyPair(2048);
		} catch (NoSuchAlgorithmException impossible) {
			throw new AssertionError("Every Java implementation is required to implement 2048 bit RSA", impossible);
		}
	}

	/**
	 * Gets the generated key pair.
	 *
	 * @return The generated key pair.
	 *
	 * @throws IllegalStateException
	 *             if called before {@link #build()}.
	 */
	public KeyPair getKeyPair() {
		if (certificate == null) {
			throw new IllegalStateException("Must build certificate first.");
		}

		return keyPair;
	}

	/**
	 * Gets the generated self-signed certificate.
	 *
	 * @return The generated certificate.
	 *
	 * @throws IllegalStateException
	 *             if called before {@link #build()}.
	 */
	public X509Certificate getCertificate() {
		if (certificate == null) {
			throw new IllegalStateException("Must build certificate first.");
		}

		return certificate;
	}

	/**
	 * Sets the certificate's common name (CN) field.
	 *
	 * @param commonName
	 *            The certificate's common name.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setCommonName(String commonName) {
		buildDefault = false;
		this.commonName = commonName;
		return this;
	}

	/**
	 * Sets the certificate's organization (O) field.
	 *
	 * @param organization
	 *            The certificate's organization.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setOrganization(String organization) {
		buildDefault = false;
		this.organization = organization;
		return this;
	}

	/**
	 * Sets the certificate's organizational unit (OU) field.
	 *
	 * @param organizationalUnit
	 *            The certificate's organizational unit.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setOrganizationalUnit(String organizationalUnit) {
		buildDefault = false;
		this.organizationalUnit = organizationalUnit;
		return this;
	}

	/**
	 * Sets the certificate's locality (L) field.
	 *
	 * @param locality
	 *            The certificate's locality.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setLocality(String locality) {
		buildDefault = false;
		this.locality = locality;
		return this;
	}

	/**
	 * Sets the certificate's state (ST) field.
	 *
	 * @param state
	 *            The certificate's state or region.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setState(String state) {
		buildDefault = false;
		this.state = state;
		return this;
	}

	/**
	 * Sets the certificate's country code (C) field.
	 *
	 * @param countryCode
	 *            The certificate's country code.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setCountryCode(String countryCode) {
		buildDefault = false;
		this.countryCode = countryCode;
		return this;
	}

	/**
	 * Sets the certificate's application URI which will be added as a <i>subject
	 * alternative name</i>.
	 *
	 * @param applicationUri
	 *            The certificate's application URI.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper setApplicationUri(String applicationUri) {
		buildDefault = false;
		this.applicationUri = applicationUri;
		return this;
	}

	/**
	 * Adds a DNS name as a <i>subject alternative name</i> to the certificate.
	 *
	 * <p>
	 * If you're adding an IP address to the certificate as well, you can use
	 * {@link #addIpAddress(InetAddress, boolean)} to add the associated host name
	 * automatically, without manually calling this method. However, see that
	 * method's documentation for important limitations.
	 *
	 * @param dnsName
	 *            The name to add. Can be any kind of host name or FQDN.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper addDnsName(String dnsName) {
		buildDefault = false;
		dnsNames.add(dnsName);
		return this;
	}

	/**
	 * Adds an IP address as a <i>subject alternative name</i> to the certificate.
	 *
	 * <p>
	 * Optionally, this method can also attempt to lookup the matching host name in
	 * the background. If successful, the name will be added to the certificate.
	 * <br>
	 * If the lookup fails or doesn't finish until {@link #build()} is called, the
	 * name won't be added. <br>
	 * To guarantee the host name is added use {@link #addDnsName(String)}, instead.
	 *
	 * @param ipAddress
	 *            The address to add.
	 * @param lookupHostName
	 *            If true, the host name will be looked up in the background and
	 *            added to the certificate.
	 *
	 * @return This {@link CertificateHelper}.
	 */
	public CertificateHelper addIpAddress(InetAddress ipAddress, boolean lookupHostName) {
		buildDefault = false;
		ipAddresses.add(ipAddress);

		if (lookupHostName) {
			CompletableFuture.supplyAsync(ipAddress::getHostName).thenAccept(hostName -> dnsNames.add(hostName));
		}

		return this;
	}

	/**
	 * Builds a self-signed certificate from the information previously provided to
	 * this helper.
	 *
	 * <p>
	 * If no information has been provided, a default certificate is generated
	 * automatically. The default certificate carries only the common name
	 * <code>CN=Unknown</code>.
	 *
	 * <p>
	 * After this method returns, the certificate can be acquired through
	 * {@link #getCertificate()}.
	 *
	 * @throws CertificateException
	 *             if certificate generation fails.
	 */
	public void build() throws CertificateException {
		try {
			SelfSignedCertificateBuilder builder = buildDefault ? configureDefaultBuilder() : configureBuilderWithInfo();
			certificate = builder.build();
		} catch (Exception e) {
			throw new CertificateException("Failed to create self-signed certificate.", e);
		}
	}

	/** Returns a builder for a default certificate (CN=Unknown). */
	private SelfSignedCertificateBuilder configureDefaultBuilder() {
		return new SelfSignedCertificateBuilder(keyPair).setCommonName("Unknown");
	}

	/** Returns a builder for a certificate with the user-provided information. */
	private SelfSignedCertificateBuilder configureBuilderWithInfo() {
		SelfSignedCertificateBuilder builder = new SelfSignedCertificateBuilder(keyPair).setCommonName(commonName).setOrganization(organization).setOrganizationalUnit(organizationalUnit).setLocalityName(locality).setStateName(state)
				.setCountryCode(countryCode).setApplicationUri(applicationUri);

		ipAddresses.forEach(ip -> builder.addIpAddress(ip.getHostName()));
		dnsNames.forEach(builder::addDnsName);

		return builder;
	}
}
