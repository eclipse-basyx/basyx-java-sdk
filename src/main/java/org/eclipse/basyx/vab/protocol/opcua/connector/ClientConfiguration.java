/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.connector;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.eclipse.basyx.vab.protocol.opcua.CertificateHelper;
import org.eclipse.basyx.vab.protocol.opcua.types.MessageSecurityMode;
import org.eclipse.basyx.vab.protocol.opcua.types.SecurityPolicy;

/**
 * Holds the configuration for an {@link IOpcUaClient}.
 */
public final class ClientConfiguration implements Cloneable {
    private SecurityPolicy securityPolicy = SecurityPolicy.None;
    private MessageSecurityMode messageSecurityMode = MessageSecurityMode.None;
    private String applicationName;
    private String applicationUri;
    private KeyPair keyPair;
    private X509Certificate certificate;

    /**
     * Creates a new {@link ClientConfiguration} with default settings.
     */
    public ClientConfiguration() {
        // Nothing to be done, all settings are default.
    }

    /**
     * Creates a shallow copy of this object.
     *
     * @return A shallow copy.
     */
    @Override
    public ClientConfiguration clone() {
        try {
            return (ClientConfiguration) super.clone();
        } catch (CloneNotSupportedException impossible) {
            // Can't happen because we support cloning.
            throw new AssertionError(impossible);
        }
    }

    /**
     * Gets the key pair used to generate the client certificate.
     *
     * <p>
     * Default: <code>null</code> (i.e., no client certificate is used).
     *
     * @return The certificate.
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Gets the certificate for client identification towards the server.
     *
     * <p>
     * Default: <code>null</code> (i.e., no client certificate is used).
     *
     * @return The certificate.
     */
    public X509Certificate getCertificate() {
        return certificate;
    }

    /**
     * Sets the key pair and associated certificate for client identification towards the server.
     *
     * <p>
     * Default: No certificate is used.
     *
     * @param keyPair     The key pair. If <code>null</code> then certificate must also be
     *                    <code>null</code>.
     * @param certificate The certificate. If <code>null</code> then keyPair must also be
     *                    <code>null</code>.
     *
     * @return This {@link ClientConfiguration}.
     *
     * @see CertificateHelper
     *
     * @throws IllegalArgumentException if keyPair is <code>null</code> but certificate is not or vice
     *                                  versa.
     */
    public ClientConfiguration setKeyPairAndCertificate(KeyPair keyPair, X509Certificate certificate) {
        if ((keyPair == null) ^ (certificate == null)) {
            throw new IllegalArgumentException("Either both keyPair and certificate are null or neither.");
        }

        this.keyPair = keyPair;
        this.certificate = certificate;
        return this;
    }

    /**
     * Gets the OPC UA client's application name.
     *
     * <p>
     * The client uses this name to identify itself towards the server. It needs not follow any specific
     * format.
     *
     * @return The application name.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets the OPC UA client's application name.
     *
     * <p>
     * The client uses this name to identify itself towards the server. It needs not follow any specific
     * format.
     *
     * <p>
     * Default: <code>null</code>.
     *
     * @param applicationName The application name.
     *
     * @return This {@link ClientConfiguration}.
     */
    public ClientConfiguration setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    /**
     * Gets the OPC UA client's application URI.
     *
     * <p>
     * The client uses this URI to identify itself towards the server.
     *
     * @return The application URI.
     */
    public String getApplicationUri() {
        return applicationUri;
    }

    /**
     * Sets the OPC UA client's application URI.
     *
     * <p>
     * The client uses this URI to identify itself towards the server.
     *
     * <p>
     * Default: <code>null</code>.
     *
     * @param applicationUri The application URI.
     *
     * @return This {@link ClientConfiguration}.
     */
    public ClientConfiguration setApplicationUri(String applicationUri) {
        this.applicationUri = applicationUri;
        return this;
    }

    /**
     * Sets the message security mode.
     *
     * <p>
     * Along with {@link #getSecurityPolicy()} this parameter controls the endpoint selection when
     * connecting to a server.
     *
     * <p>
     * Default: {@link MessageSecurityMode#None}.
     *
     * @return The message security mode.
     */
    public MessageSecurityMode getMessageSecurityMode() {
        return messageSecurityMode;
    }

    /**
     * Sets the message security mode.
     *
     * <p>
     * Along with {@link #setSecurityPolicy(SecurityPolicy)} this parameter controls the endpoint
     * selection when connecting to a server.
     *
     * <p>
     * Default: {@link MessageSecurityMode#None}.
     *
     * @param messageSecurityMode The message security mode.
     *
     * @return This {@link ClientConfiguration}.
     *
     * @throws IllegalArgumentException if messageSecurityMode is <code>null</code>.
     */
    public ClientConfiguration setMessageSecurityMode(MessageSecurityMode messageSecurityMode) {
        if (messageSecurityMode == null) {
            throw new IllegalArgumentException("messageSecurityMode can not be null.");
        }

        this.messageSecurityMode = messageSecurityMode;
        return this;
    }

    /**
     * Gets the security policy.
     *
     * <p>
     * Along with {@link #getMessageSecurityMode()} this parameter controls the endpoint selection when
     * connecting to a server.
     *
     * <p>
     * <b>Default:</b> {@link SecurityPolicy#None}.
     *
     * @return The security policy.
     */
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }

    /**
     * Sets the security policy.
     *
     * <p>
     * Along with {@link #setMessageSecurityMode(MessageSecurityMode)} this parameter controls the
     * endpoint selection when connecting to a server.
     *
     * <p>
     * <b>Default:</b> {@link SecurityPolicy#None}.
     *
     * @param securityPolicy The security policy.
     *
     * @return This {@link ClientConfiguration}.
     *
     * @throws IllegalArgumentException if securityPolicy is <code>null</code>.
     */
    public ClientConfiguration setSecurityPolicy(SecurityPolicy securityPolicy) {
        if (securityPolicy == null) {
            throw new IllegalArgumentException("securityPolicy can not be null.");
        }

        this.securityPolicy = securityPolicy;
        return this;
    }
}
