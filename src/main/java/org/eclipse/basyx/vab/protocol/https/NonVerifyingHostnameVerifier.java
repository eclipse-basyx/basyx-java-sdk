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
import javax.net.ssl.SSLSession;

/**
 * A default host name verifier which verifies every host name
 * Used for testing with self signed certificate
 * @author haque
 *
 */
public class NonVerifyingHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
