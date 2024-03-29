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

import java.util.List;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.exception.provider.ProviderException;

/**
 * The unchecked exception that gets thrown when an action must not be conducted
 * due to lacking authorization. This can be used to translate/further propagate
 * a checked {@link InhibitException}.
 *
 * @author wege
 */
public class NotAuthorizedException extends ProviderException {
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public NotAuthorizedException(final String msg) {
		super(msg);
	}

	public NotAuthorizedException(final Exception e) {
		super(e);
	}

	public NotAuthorizedException(final List<Message> msgs) {
		super(msgs);
	}

	public NotAuthorizedException() {
		this("not authorized");
	}
}
