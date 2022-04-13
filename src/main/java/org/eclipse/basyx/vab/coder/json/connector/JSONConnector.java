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
package org.eclipse.basyx.vab.coder.json.connector;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.vab.coder.json.metaprotocol.IMetaProtocolHandler;
import org.eclipse.basyx.vab.coder.json.metaprotocol.MetaprotocolHandler;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.coder.json.serialization.GSONToolsFactory;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IBaSyxConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector Class responsible for serializing parameters and de-serializing
 * results. It verifies the results, removes the message header and returns the
 * requested entity.
 * 
 * @author pschorn
 *
 */
public class JSONConnector implements IModelProvider {

	private static final Logger LOGGER_DEFAULT = LoggerFactory.getLogger(JSONConnector.class);
	private static final Logger LOGGER_COMMUNICATION = LoggerFactory.getLogger(LOGGER_DEFAULT.getName() + ".MALFORMED");

	/**
	 * Reference to Connector backend
	 */
	protected IBaSyxConnector provider = null;

	/**
	 * Reference to serializer / deserializer
	 */
	protected GSONTools serializer = null;

	/**
	 * Handle meta protocol in JSON String
	 */
	protected IMetaProtocolHandler metaProtocolHandler = null;

	/**
	 * Constructor
	 * 
	 * @param provider
	 */
	public JSONConnector(IBaSyxConnector provider) {
		// Store provider backend
		this.provider = provider;

		// Create the meta protocal handler
		this.metaProtocolHandler = new MetaprotocolHandler();

		// Create GSON serializer
		serializer = new GSONTools(new DefaultTypeFactory());
	}

	/**
	 * Constructor that accepts specific factory for serializer
	 * 
	 * @param provider
	 */
	public JSONConnector(IBaSyxConnector provider, GSONToolsFactory factory) {
		// Store provider backend
		this.provider = provider;

		// Create GSON serializer
		serializer = new GSONTools(factory);
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		// Get element from server
		String message = provider.getValue(path);

		// De-serialize and verify
		try {
			return metaProtocolHandler.deserialize(message);
		} catch (ProviderException e) {
			throw e;
		} catch (RuntimeException e) {
			String messageCorrelation = UUID.randomUUID().toString();
			String msg = "Failed to deserialize request for '" + provider.getEndpointRepresentation(path) + "' (" + messageCorrelation + ")";
			LOGGER_DEFAULT.warn(msg);
			LOGGER_COMMUNICATION.warn(msg + ": " + message);
			throw new ProviderException(msg, e);
		}
	}

	@Override
	public void setValue(String path, Object newValue) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		// Serialize value Object
		String jsonString = serializer.serialize(newValue);

		String message = provider.setValue(path, jsonString);

		// De-serialize and verify
		metaProtocolHandler.deserialize(message);
	}

	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		// Serialize value Object
		String jsonString = serializer.serialize(newEntity);

		String message = provider.createValue(path, jsonString);

		// De-serialize and verify
		metaProtocolHandler.deserialize(message);
	}

	@Override
	public void deleteValue(String path) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		String message = provider.deleteValue(path);

		// De-serialize and verify
		metaProtocolHandler.deserialize(message);
	}

	@Override
	public void deleteValue(String path, Object obj) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		// Serialize parameter
		String jsonString = serializer.serialize(obj);

		String message = provider.deleteValue(path, jsonString);

		// De-serialize and verify
		metaProtocolHandler.deserialize(message);
	}

	@Override
	public Object invokeOperation(String path, Object... parameter) throws ProviderException {
		VABPathTools.checkPathForNull(path);

		// Serialize parameter
		String jsonString;
		if (parameter.length == 1 && parameter[0] instanceof InvocationRequest) {
			jsonString = serializer.serialize(parameter[0]);
		} else {
			jsonString = serializer.serialize(Arrays.asList(parameter));
		}

		String message = provider.invokeOperation(path, jsonString);

		// De-serialize and verify
		return metaProtocolHandler.deserialize(message);
	}
}
