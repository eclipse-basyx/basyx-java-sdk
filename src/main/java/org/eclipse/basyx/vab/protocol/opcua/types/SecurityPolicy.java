/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.types;

/**
 * Available security policies for OPC UA connections.
 */
public enum SecurityPolicy {
    None,
    Basic128Rsa15,
    Basic256,
    Basic256Sha256,
    Aes128_Sha256_RsaOaep,
    Aes256_Sha256_RsaPss
}
