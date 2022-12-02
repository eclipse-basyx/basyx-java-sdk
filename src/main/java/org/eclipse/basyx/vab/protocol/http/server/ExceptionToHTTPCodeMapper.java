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
package org.eclipse.basyx.vab.protocol.http.server;

import java.util.List;

import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceAlreadyExistsException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;

/**
 * Maps Exceptions from providers to HTTP-Codes
 * 
 * @author conradi
 *
 */
public class ExceptionToHTTPCodeMapper {

	/**
	 * Maps ProviderExceptions to HTTP-Codes
	 * 
	 * @param e
	 *            The thrown ProviderException
	 * @return HTTP-Code
	 */
	public static int mapFromException(ProviderException e) {

		if (e instanceof MalformedRequestException) {
			return 400;
		} else if (e instanceof NotAuthorized) {
			return 403;
		} else if (e instanceof ResourceAlreadyExistsException) {
			return 422;
		} else if (e instanceof ResourceNotFoundException) {
			return 404;
		}
		return 500;

	}

	/**
	 * Maps HTTP-Codes to ProviderExceptions
	 * 
	 * @param statusCode
	 *            The received HTTP-code
	 * @return the corresponding ProviderException
	 */
	public static ProviderException mapToException(int statusCode, String text) {

		switch (statusCode) {
		case 400:
			return new MalformedRequestException(text);
		case 403:
			return new NotAuthorized(text);
		case 422:
			return new ResourceAlreadyExistsException(text);
		case 404:
			return new ResourceNotFoundException(text);
		default:
			return new ProviderException(text);
		}

	}

	/**
	 * Maps HTTP-Codes to ProviderExceptions
	 * 
	 * @param statusCode
	 *            The received HTTP-code
	 * @return the corresponding ProviderException
	 */
	public static ProviderException mapToException(int statusCode, List<Message> messages) {

		switch (statusCode) {
		case 400:
			return new MalformedRequestException(messages);
		case 403:
			return new NotAuthorized(messages);
		case 422:
			return new ResourceAlreadyExistsException(messages);
		case 404:
			return new ResourceNotFoundException(messages);
		default:
			return new ProviderException(messages);
		}

	}

}
