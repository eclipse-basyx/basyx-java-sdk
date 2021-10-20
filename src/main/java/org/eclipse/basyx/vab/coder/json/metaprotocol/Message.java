/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.coder.json.metaprotocol;

import java.util.HashMap;

/**
 * 
 * @author pschorn
 *
 */
@SuppressWarnings("serial")
public class Message extends HashMap<String, Object> {
	
	public static final String MESSAGETYPE = "messageType";
	public static final String CODE = "code";
	public static final String TEXT = "text";

	public Message(MessageType messageType, String text) {
		this(messageType, null, text);
	}
	
	public Message(MessageType messageType, String code, String text) {
		put(MESSAGETYPE, messageType.getId());
		put(CODE, code);
		put(TEXT, text);
	}

	public String getText() {
		return (String) get(TEXT);
	}
	public String getCode() {
		return (String) get(CODE);
	}

	public MessageType getMessageType() {
		return MessageType.getById((int) get(MESSAGETYPE));
	}
	
	@Override
	public String toString() {
		String code = getCode();
		if (code == null || code.isEmpty()) {
			return getMessageType().getId() + " | " + getText();
		} else {
			return getMessageType().getId() + " | " + getCode() + " - " + getText();
		}
	}

}
