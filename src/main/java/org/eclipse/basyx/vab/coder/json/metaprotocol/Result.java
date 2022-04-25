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
package org.eclipse.basyx.vab.coder.json.metaprotocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.protocol.http.server.ExceptionToHTTPCodeMapper;

/**
 * Wrapper class that handles meta-data
 * 
 * @author pschorn
 *
 */
@SuppressWarnings("serial")
public class Result extends LinkedHashMap<String, Object> {

	public static final String SUCCESS = "success";
	public static final String ISEXCEPTION = "isException";
	public static final String MESSAGES = "messages";
	public static final String ENTITY = "entity";
	public static final String ENTITYTYPE = "entityType";

	public Result(boolean success) {
		this(success, null, null);
	}

	public Result(boolean success, Message message) {
		this(success, null, new LinkedList<Message>(Arrays.asList(message)));
	}

	public Result(boolean success, List<Message> messages) {
		this(success, null, messages);
	}

	public Result(boolean success, Object entity, List<Message> messages) {
		put(SUCCESS, success);

		if (messages != null) {

			List<Map<String, Object>> messageslist = new LinkedList<Map<String, Object>>();
			for (Message msg : messages) {

				MessageType type = msg.getMessageType();

				if (type.equals(MessageType.Exception)) {
					put(ISEXCEPTION, true); // make sure isException is set!
				}

				messageslist.add(msg);
			}

			put(MESSAGES, messageslist);
		}

		if (entity != null) {
			put(ENTITY, entity);
			put(ENTITYTYPE, entity.getClass().getName());
		}
	}

	public Result(Result result) {
		this(result.success(), result.getEntity(), result.getMessages());
	}

	public Result(Exception e) {
		this(false, getMessageListFromException(e));
	}

	@SuppressWarnings("unchecked")
	public static Result createAsFacade(Map<String, Object> map) {
		boolean success = (Boolean) map.get(SUCCESS);
		Object entity = map.get(ENTITY);
		List<Message> messages = new ArrayList<>();

		for (Map<String, Object> messageMap : (List<Map<String, Object>>) map.get(MESSAGES)) {
			messages.add(Message.createAsFacade(messageMap));
		}

		return new Result(success, entity, messages);
	}

	private static List<Message> getMessageListFromException(Exception e) {

		List<Message> messageList = new LinkedList<Message>();

		// Translate the exception to code
		if (e instanceof ProviderException) {
			String code = new Integer(ExceptionToHTTPCodeMapper.mapFromException((ProviderException) e)).toString();
			Message message = new Message(MessageType.Exception, code, e.getClass().getSimpleName() + ": " + e.getMessage());

			// replace with desired debugging output
			messageList.add(message);
		}

		if (e.getCause() != null) {
			messageList.addAll(getMessageListFromException((Exception) e.getCause()));
		}

		return messageList;
	}

	public Class<?> getEntityType() {
		Object entityType = get(ENTITYTYPE);
		if (entityType instanceof String) {
			String typeString = (String) entityType;
			try {
				return Class.forName(typeString);
			} catch (ClassNotFoundException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	public Object getEntity() {
		return get(ENTITY);
	}

	public boolean success() {
		return (boolean) get(SUCCESS);
	}

	public boolean isException() {
		return (boolean) get(ISEXCEPTION);
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages() {
		return (List<Message>) get(MESSAGES);
	}

}
