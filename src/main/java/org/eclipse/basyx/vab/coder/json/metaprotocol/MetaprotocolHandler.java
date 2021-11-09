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

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.coder.json.serialization.GSONToolsFactory;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.protocol.http.server.ExceptionToHTTPCodeMapper;

public class MetaprotocolHandler implements IMetaProtocolHandler {
	/**
	 * Reference to serializer / deserializer
	 */
	protected GSONTools serializer = null;
	
	/**
	 * Constructor that create the serializer
	 * 
	 */
	public MetaprotocolHandler() {
		// Create GSON serializer
		serializer = new GSONTools(new DefaultTypeFactory());
	}
	
	/**
	 * Constructor that accepts specific factory for serializer
	 * @param factory
	 */
	public MetaprotocolHandler(GSONToolsFactory factory) {
		// Create GSON serializer
		serializer = new GSONTools(factory);
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public Object deserialize(String message) throws ProviderException {

		// First get the GSON object from the JSON string
		Object gsonObj = serializer.deserialize(message);
		
		// Then interpret and verify the result object
		Object result = null;

		// If it is a map, see if it does contain an exception
		if (gsonObj instanceof Map) {
			Map<String, Object> responseMap = (Map<String, Object>) gsonObj;
				
			// Handle meta information and exceptions
			result = handleResult(responseMap);
		} else {
			// Otherwise, return directly.
			result = gsonObj;
		}
        return result;
	}
	
	
	/**
	 * Verify the Result and try to extract the entity if available. Process
	 * information of "success", "entityType" and "messages"
	 * 
	 * @param responseMap
	 *            - provide deserialized message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object handleResult(Map<String, Object> responseMap) throws ProviderException {

		// If there's no success: "false", there was no exception
		if (!responseMap.containsKey(Result.SUCCESS)) {
			return responseMap;
		}

		// Retrieve messages if any
		Collection<Map<String, Object>> messages = (Collection<Map<String, Object>>) responseMap.get(Result.MESSAGES);
		if (messages == null) {
			throw new ProviderException("Unknown error occured: Success entry is indicating an error but no message was attached");
		}
		
		Map<String, Object> first = messages.iterator().next(); // assumes an Exception always comes with a message

		// Get the code of the exception message
		String code = (String) first.get(Message.CODE);

		// Get the text from the exception
		String text = (String) first.get(Message.TEXT);

		throw getExceptionFromCode(code, text);
	}
	
	/**
	 * Creates a ProviderException from a String received form the Server<br>
	 * The String has to be formated e.g. "ResourceNotFoundException: Requested Item
	 * was not found"
	 * 
	 * @param code - code of the exception message
	 * @return the matching ProviderException
	 */
	public static ProviderException getExceptionFromCode(String code, String text) {

		int exceptionCode = Integer.parseInt(code);
		
		// return exception based on code
		return ExceptionToHTTPCodeMapper.mapToException(exceptionCode, text);
	}
}
