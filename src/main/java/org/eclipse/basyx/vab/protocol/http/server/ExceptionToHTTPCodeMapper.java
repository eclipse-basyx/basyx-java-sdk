/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.http.server;

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
	 * @param e The thrown ProviderException
	 * @return HTTP-Code
	 */
	public static int mapFromException(ProviderException e) {

		if (e instanceof MalformedRequestException) {
			return 400;
		} else if(e instanceof ResourceAlreadyExistsException) {
			return 422;
		} else if(e instanceof ResourceNotFoundException) {
			return 404;
		}
		return 500;
		
	}
	
	/**
	 * Maps HTTP-Codes to ProviderExceptions
	 * 
	 * @param statusCode The received HTTP-code
	 * @return the corresponding ProviderException
	 */
	public static ProviderException mapToException(int statusCode, String text) {
		
		switch(statusCode) {
		case 400:
			return new MalformedRequestException(text);
		case 422:
			return new ResourceAlreadyExistsException(text);
		case 404:
			return new ResourceNotFoundException(text);
		default:
			return new ProviderException(text);
		}
		
	}
	
}
