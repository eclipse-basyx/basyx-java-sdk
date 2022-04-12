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
