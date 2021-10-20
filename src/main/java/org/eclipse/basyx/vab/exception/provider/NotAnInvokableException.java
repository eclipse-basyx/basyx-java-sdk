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
 * that invoke was called with a path to a non invokable resource.
 * 
 * @author conradi
 *
 */
public class NotAnInvokableException extends ProviderException {

	  
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor
	 */
	public NotAnInvokableException(String msg) {
		super(msg);
	}
	
	public NotAnInvokableException(Exception e) {
		super(e);
	}
}
