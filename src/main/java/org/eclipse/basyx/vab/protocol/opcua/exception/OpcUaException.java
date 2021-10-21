/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.exception;

import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * Generic wrapper type for exceptions from the underlying client library.
 *
 * <p>
 * User code can catch this exception type to handle all OPC UA related exceptions uniformly. If
 * more fine-grained handling of different failure modes is required, calling code must inspect the
 * cause through {@link #getCause()} and deal with exception types of the underlying library.
 */
public class OpcUaException extends ProviderException {

    private static final long serialVersionUID = 1L;

    public OpcUaException(String msg) {
        super(msg);
    }

    public OpcUaException(Throwable cause) {
        super(cause);
    }

    public OpcUaException(String message, Throwable cause) {
        super(message, cause);
    }

}
