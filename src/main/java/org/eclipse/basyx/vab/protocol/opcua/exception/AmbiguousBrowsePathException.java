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

/**
 * Indicates that a browse path was used which matches for than one node on the server.
 *
 * <p>
 * While it is perfectly valid in OPC UA to have browse paths which match more than one single node,
 * BaSyx doesn't deal well with these cases. That's why this exception is thrown whenever such a
 * browse path is encountered.
 */
public final class AmbiguousBrowsePathException extends OpcUaException {
    private static final long serialVersionUID = 1L;

    public AmbiguousBrowsePathException(String msg) {
        super(msg);
    }

    public AmbiguousBrowsePathException(Throwable cause) {
        super(cause);
    }

    public AmbiguousBrowsePathException(String message, Throwable cause) {
        super(message, cause);
    }

}
