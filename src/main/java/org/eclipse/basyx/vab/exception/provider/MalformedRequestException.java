/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.exception.provider;

/**
 * Used to indicate by a ModelProvider,
 * that a given request was malformed. <br/>
 * e.g. an invalid path or a invalid JSON.
 * 
 * @author conradi
 *
 */
public class MalformedRequestException extends ProviderException {

	
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor
	 */
	public MalformedRequestException(String msg) {
		super(msg);
	}
	
	public MalformedRequestException(Exception e) {
		super(e);
	}
}
