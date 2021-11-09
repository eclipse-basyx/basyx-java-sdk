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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.vab.coder.json.metaprotocol.Message;
import org.eclipse.basyx.vab.coder.json.metaprotocol.MessageType;

/**
 * Used to indicate a general exception in a ModelProvider
 * 
 * @author conradi
 *
 */
public class ProviderException extends RuntimeException {

	private List<Message> messages = new ArrayList<>();
	
	/**
	 * Version information for serialized instances
	 */
	private static final long serialVersionUID = 1L;

	public ProviderException(String msg) {
		super(msg);
		messages.add(new Message(MessageType.Exception, msg));
	}
	
	public ProviderException(List<Message> messages) {
		super();
		this.messages = messages;
	}

	public ProviderException(Throwable cause) {
		super(cause);
	}

	public ProviderException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
}
