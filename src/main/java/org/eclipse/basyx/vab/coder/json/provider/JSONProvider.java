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
package org.eclipse.basyx.vab.coder.json.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.servlet.ServletOutputStream;

import org.eclipse.basyx.vab.coder.json.metaprotocol.Result;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.coder.json.serialization.GSONToolsFactory;
import org.eclipse.basyx.vab.exception.LostHTTPRequestParameterException;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Provider class that supports JSON serialized communication <br>
 * Generic Caller is required since messages can be technology specific.
 * 
 * 
 * @author pschorn, schnicke, kuhn
 *
 */
public class JSONProvider<ModelProvider extends IModelProvider> {
	
	private static Logger logger = LoggerFactory.getLogger(JSONProvider.class);

	
	/**
	 * Reference to IModelProvider backend
	 */
	protected ModelProvider providerBackend = null;
	
	
	/**
	 * Reference to serializer / deserializer
	 */
	protected GSONTools serializer = null;
	

	
	/**
	 * Constructor
	 */
	public JSONProvider(ModelProvider modelProviderBackend) {
		// Store reference to backend
		providerBackend = modelProviderBackend;
		
		// Create GSON serializer
		serializer = new GSONTools(new DefaultTypeFactory());
	}


	/**
	 * Constructor
	 */
	public JSONProvider(ModelProvider modelProviderBackend, GSONToolsFactory factory) {
		// Store reference to backend
		providerBackend = modelProviderBackend;
		
		// Create GSON serializer
		serializer = new GSONTools(factory);
	}
	
	
	/**
	 * Get serializer reference
	 */
	public GSONTools getSerializerReference() {
		return serializer;
	}
	

	/**
	 * Get backend reference
	 */
	public ModelProvider getBackendReference() {
		return providerBackend;
	}	
	
	/**
	 * Marks success as false and delivers exception cause messages 
	 * @param e
	 * @return
	 */
	private String serialize(Exception e) {
		// Create Ack
		Result result = new Result(e);
		
		// Serialize the whole thing
		return serialize(result);
	}
	
	
	/**
	 * Serialize IResult (LinkedHashMap)
	 * @param string
	 * @return
	 */
	private String serialize(Result string) {
		// Serialize the whole thing
		return serializer.serialize(string);
	}
	

	/**
	 * Send Error
	 * @param e
	 * @param path
	 * @param resp
	 */
	private void sendException(OutputStream resp, Exception e) throws ProviderException {
		
		// Serialize Exception
		String jsonString = serialize(e);
		try {
			resp.write(jsonString.getBytes(StandardCharsets.UTF_8));
		} catch(IOException innerE) {
			throw new ProviderException("Failed to send Exception '" + e.getMessage() + "' to client", innerE);
		}
		
		//If the Exception is a ProviderException, just rethrow it
		if(e instanceof ProviderException) {
			throw (ProviderException) e;
		}
		
		//If the Exception is not a ProviderException encapsulate it in one and log it
		logger.error("Unknown Exception in JSONProvider", e);
		throw new ProviderException(e);
	}

	
	/**
	 * Extracts parameter from JSON and handles de-serialization errors
	 * 
	 * @param path
	 * @param serializedJSONValue
	 * @param outputStream
	 * @return
	 * @throws MalformedRequestException 
	 * @throws LostHTTPRequestParameterException 
	 * @throws ProviderException
	 */
	private Object extractParameter(String path, String serializedJSONValue, OutputStream outputStream) throws MalformedRequestException {
		// Return value
		Object result = null;

		try {
			// Deserialize json body
			result = serializer.deserialize(serializedJSONValue);
		} catch (Exception e) {
			//JSON could not be deserialized
			throw new MalformedRequestException(e);
		}
		
		return result;
	}
	

	/**
	 * Process a BaSys get operation, return JSON serialized result
	 * @throws ProviderException 
	 */
	public void processBaSysGet(String path, OutputStream outputStream) throws ProviderException {

		try {
			// Get requested value from provider backend
			Object value = providerBackend.getValue(path);

			// Serialize as json string - any messages?
			String jsonString = serializer.serialize(value);

			// Send response
			outputStream.write(jsonString.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}

	
	/**
	 * Process a BaSys set operation
	 * 
	 * @param path
	 * @param serializedJSONValue
	 * @param outputStream
	 * @throws ProviderException 
	 */
	public void processBaSysSet(String path, String serializedJSONValue, OutputStream outputStream) throws ProviderException {
		
		// Try to set value of BaSys VAB element
		try {
			
			// Deserialize json body. If parameter is not ex
			Object parameter = extractParameter(path, serializedJSONValue, outputStream);

			// Set the value of the element
			providerBackend.setValue(path, parameter);

			// Send response
			outputStream.write("".getBytes(StandardCharsets.UTF_8));

		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}

	
	/**
	 * Process a BaSys invoke operation
	 * @throws ProviderException 
	 */
	@SuppressWarnings("unchecked")
	public void processBaSysInvoke(String path, String serializedJSONValue, OutputStream outputStream) throws ProviderException {

		try {
			
			// Deserialize json body. 
			Object parameter = extractParameter(path, serializedJSONValue, outputStream);
			
			// If only a single parameter has been sent, pack it into an array so it can be
			// casted safely
			if (parameter instanceof Collection<?>) {
				Collection<Object> list = (Collection<Object>) parameter;
				Object[] parameterArray = new Object[list.size()];
				int i = 0;
				for (Object o : list) {
					parameterArray[i] = o;
					i++;
				}
				parameter = parameterArray;
			}
			
			if (!(parameter instanceof Object[])) {
				Object[] parameterArray = new Object[1];
				Object tmp = parameter;
				parameterArray[0] = tmp;
				parameter = parameterArray;
			}

			Object result = providerBackend.invokeOperation(path, (Object[]) parameter);

			// Serialize result as json string
			String jsonString = serializer.serialize(result);
			
			// Send response
			outputStream.write(jsonString.getBytes(StandardCharsets.UTF_8));

		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}

	
	/**
	 * Implement "Delete" operation. Deletes any resource under the given path.
	 *
	 * @param path
	 * @param serializedJSONValue If this parameter is not null (basystype),we remove an element from a collection by index / remove from map by key. We assume that the parameter only contains 1 element
	 * @param outputStream
	 * @throws ProviderException 
	 */
	public void processBaSysDelete(String path, String serializedJSONValue, OutputStream outputStream) throws ProviderException {
		
		try {

			// Deserialize json body. If parameter is not ex
			Object parameter = extractParameter(path, serializedJSONValue, outputStream);
			
			// Process delete request with or without argument
			if (parameter == null) {
				this.providerBackend.deleteValue(path);
			} else {
				this.providerBackend.deleteValue(path, parameter);
			}

			// Send response
			outputStream.write("".getBytes(StandardCharsets.UTF_8));

		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}

	
	/**
	 * Creates a resource under the given path
	 * 
	 * @param path
	 * @param serializedJSONValue
	 * @param outputStream
	 * @throws ProviderException 
	 */
	public void processBaSysCreate(String path, String serializedJSONValue, OutputStream outputStream) throws ProviderException {

		try {
			// Deserialize json body. 
			Object parameter = extractParameter(path, serializedJSONValue, outputStream);
						

			providerBackend.createValue(path, parameter);

			// Send response
			outputStream.write("".getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}

	/**
	 * Uploads a resource at given path
	 * @param path
	 * @param fileContent
	 * @param outputStream
	 */
	public void processBaSysUpload(String path, InputStream fileContent, ServletOutputStream outputStream) {
		try {
			providerBackend.createValue(path, fileContent);

			// Send response
			outputStream.write("".getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			sendException(outputStream, e);
		}
	}
}
