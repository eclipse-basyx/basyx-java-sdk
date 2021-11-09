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

import java.util.List;

import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;

/**
 * Used to indicate by a ModelProvider,
 * that a resource to be created already exists
 * 
 * @author conradi
 *
 */
public class ResourceAlreadyExistsException extends ProviderException {

	
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor
	 */
	public ResourceAlreadyExistsException(String msg) {
		super(msg);
	}
	
	public ResourceAlreadyExistsException(Exception e) {
		super(e);
	}
	
	public ResourceAlreadyExistsException(List<Message> msgs) {
		super(msgs);
	}
}
