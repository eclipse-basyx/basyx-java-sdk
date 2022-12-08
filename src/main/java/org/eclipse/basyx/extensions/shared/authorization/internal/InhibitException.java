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
package org.eclipse.basyx.extensions.shared.authorization.internal;

/**
 * Exception that is thrown when an authorization decision point decides to
 * inhibit some action.
 *
 * @author wege
 */
public abstract class InhibitException extends Exception {
	protected InhibitException(final String message, final Throwable cause) {
		super(message, cause);
	}

	protected InhibitException(final String message) {
		super(message);
	}

	/**
	 * Clones the exception while replacing the submodel id in the exception by the
	 * given short id of the submodel. This is used in order to not leak information
	 * about decision criteria when the requester of a protected resource only
	 * passed the short id.
	 *
	 * @param smIdShortPath
	 *            the short id of the submodel.
	 * @return the new exception
	 */
	public abstract InhibitException reduceSmIdToSmIdShortPath(final String smIdShortPath);
}