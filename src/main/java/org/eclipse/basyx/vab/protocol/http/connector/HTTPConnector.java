/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.http.connector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.api.IBaSyxConnector;
import org.eclipse.basyx.vab.protocol.http.server.ExceptionToHTTPCodeMapper;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpMethod;

/**
 * HTTP connector class
 * 
 * @author kuhn, pschorn, schnicke
 *
 */
public class HTTPConnector implements IBaSyxConnector {
	
	private static Logger logger = LoggerFactory.getLogger(HTTPConnector.class);
	
	private String address;
	private String mediaType;
	protected Client client;

	/**
	 * Invoke a BaSys get operation via HTTP GET
	 * 
	 * @param address
	 *            the server address from the directory
	 * @param servicePath
	 *            the URL suffix for the requested path
	 * @return the requested object
	 */
	@Override
	public String getValue(String servicePath) {
		return httpGet(servicePath);
	}

	public HTTPConnector(String address) {
		this(address, MediaType.APPLICATION_JSON + ";charset=UTF-8");
	}

	public HTTPConnector(String address, String mediaType) {
		this.address = address;
		this.mediaType = mediaType;
		
		// Create client
		client = ClientBuilder.newClient();

		logger.trace("Create with addr: {}", address);
	}

	/**
	 * Invokes BasysPut method via HTTP PUT. Overrides existing property, operation
	 * or event.
	 * 
	 * @param address
	 *            the server address from the directory
	 * @param servicePath
	 *            the URL suffix for the requested property, operation or event
	 * @param newValue
	 *            should be an IElement of type Property, Operation or Event
	 */
	@Override
	public String setValue(String servicePath, String newValue) throws ProviderException {

		return httpPut(servicePath, newValue);
	}

	/**
	 * Invoke a BaSys Delete operation via HTTP PATCH. Deletes an element from a map
	 * or collection by key
	 * 
	 * @param address
	 *            the server address from the directory
	 * @param servicePath
	 *            the URL suffix for the requested property
	 * @param obj
	 *            the key or index of the entry that should be deleted
	 * @throws ProviderException
	 */
	@Override
	public String deleteValue(String servicePath, String obj) throws ProviderException {

		return httpPatch(servicePath, obj);
	}

	/**
	 * Invoke a BaSys invoke operation via HTTP. Implemented as HTTP POST.
	 * 
	 * @throws ProviderException
	 */
	@Override
	public String createValue(String servicePath, String newValue) throws ProviderException {

		return httpPost(servicePath, newValue);
	}

	/**
	 * Invoke basysDelete operation via HTTP DELETE. Deletes any resource under the
	 * given path.
	 * 
	 * @throws ProviderException
	 */
	@Override
	public String deleteValue(String servicePath) throws ProviderException {

		return httpDelete(servicePath);
	}

	/**
	 * Execute a web service, return JSON string
	 */
	protected Builder buildRequest(Client client, String wsURL) {
		// Called URL
		WebTarget resource = client.target(wsURL);

		// Build request, set JSON encoding
		Builder request = resource.request();
		request.accept(mediaType);
		// Return JSON request
		return request;
	}

	/**
	 * Create web service path
	 */
	protected String createWSPath(String part1, String part2) {
		// Null pointer check
		if (part1 == null)
			return part2;
		if (part2 == null)
			return part1;

		// Return combined string
		if (part1.endsWith("/"))
			return part1 + part2;

		return part1 + "/" + part2;
	}
	
	/**
	 * Perform a HTTP get request
	 * 
	 * @param servicePath
	 * @return
	 */
	private String httpGet(String servicePath) throws ProviderException {
		logger.trace("[HTTP Get] {}", VABPathTools.concatenatePaths(address, servicePath));

		Builder request = retrieveBuilder(servicePath);

		// Perform request
		Response rsp = null;
		try {
			rsp = request.get();
		} finally {
			if (!isRequestSuccess(rsp)) {
				throw this.handleProcessingException(HttpMethod.GET, getStatusCode(rsp));	
			}
		}

		// Return response message (header)
		return rsp.readEntity(String.class);
	}

	private String httpPut(String servicePath, String newValue) throws ProviderException {
		logger.trace("[HTTP Put] {} [[ {} ]]", VABPathTools.concatenatePaths(address, servicePath), newValue);

		Builder request = retrieveBuilder(servicePath);

		// Perform request
		Response rsp = null;
		try {
			rsp = request.put(Entity.entity(newValue, mediaType));
		} finally {
			if (!isRequestSuccess(rsp)) {
				throw this.handleProcessingException(HttpMethod.PUT, getStatusCode(rsp));	
			}
		}

		// Return response message (header)
		return rsp.readEntity(String.class);

	}

	private String httpPatch(String servicePath, String newValue) throws ProviderException {
		logger.trace("[HTTP Patch] {} {}", VABPathTools.concatenatePaths(address, servicePath), newValue);

		// Create and invoke HTTP PATCH request
		Response rsp = null;
		try {
			rsp = this.client.target(VABPathTools.concatenatePaths(address, servicePath)).request().build("PATCH", Entity.text(newValue)).property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).invoke();
		} finally {
			if (!isRequestSuccess(rsp)) {
				throw this.handleProcessingException(HttpMethod.PATCH, getStatusCode(rsp));	
			}
		}

		// Return response message (header)
		return rsp.readEntity(String.class);
	}

	private String httpPost(String servicePath, String parameter) throws ProviderException {
		logger.trace("[HTTP Post] {} {}", VABPathTools.concatenatePaths(address, servicePath), parameter);

		Builder request = retrieveBuilder(servicePath);

		// Perform request
		Response rsp = null;
		try {
			rsp = request.post(Entity.entity(parameter, mediaType));
		} finally {
			if (!isRequestSuccess(rsp)) {
				throw this.handleProcessingException(HttpMethod.POST, getStatusCode(rsp));	
			}
		}

		// Return response message (header)
		return rsp.readEntity(String.class);
	}

	private String httpDelete(String servicePath) throws ProviderException {
		logger.trace("[HTTP Delete] {}", VABPathTools.concatenatePaths(address, servicePath));

		Builder request = retrieveBuilder(servicePath);

		// Perform request
		Response rsp = null;
		try {
			rsp = request.delete();
		} finally {
			if (!isRequestSuccess(rsp)) {
				throw this.handleProcessingException(HttpMethod.DELETE, getStatusCode(rsp));	
			}
		}

		// Return response message (header)
		return rsp.readEntity(String.class);
	}

	@Override
	public String invokeOperation(String path, String parameter) throws ProviderException {

		return httpPost(path, parameter);
	}

	/**
	 * Create the builder depending on the service path
	 * 
	 * @param servicePath
	 * @return
	 */
	private Builder retrieveBuilder(String servicePath) {
		return buildRequest(client, VABPathTools.concatenatePaths(address, servicePath));
	}

	private ProviderException handleProcessingException(HttpMethod method, int statusCode) {
		if (statusCode == 0) {
			return ExceptionToHTTPCodeMapper.mapToException(404, "[HTTP " + method.name() + "] Failed to request " + this.address + " with mediatype " + this.mediaType);
		} else {
			return ExceptionToHTTPCodeMapper.mapToException(statusCode, "[HTTP " + method.name() + "] Failed to request " + this.address + " with mediatype " + this.mediaType);
		}
	}
	
	/**
	 * Get status code from HTTP Response
	 * @param rsp
	 * @return
	 */
	private int getStatusCode(Response rsp) {
		return rsp != null ? rsp.getStatus() : 0;
	}
	
	/**
	 * Returns true if the response is succeeded
	 * @param rsp
	 * @return
	 */
	private boolean isRequestSuccess(Response rsp) {
		return rsp != null && rsp.getStatusInfo().getFamily() == Status.Family.SUCCESSFUL;
	}

	/**
	 * Get string representation of endpoint for given path for debugging. 
	 * @param path Requested path
	 * @return String representing requested endpoint
	 */
	@Override
	public String getEndpointRepresentation(String path) {
		return VABPathTools.concatenatePaths(address, path);
	}
}
