/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.http.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.basyx.vab.coder.json.provider.JSONProvider;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;


/**
 * VAB provider class that enables access to an IModelProvider via HTTP REST
 * interface<br>
 * <br>
 * REST http interface is as following: <br>
 * - GET /aas/submodels/{subModelId} Retrieves submodel with ID {subModelId}
 * <br>
 * - GET /aas/submodels/{subModelId}/properties/a Retrieve property a of
 * submodel {subModelId}<br>
 * - GET /aas/submodels/{subModelId}/properties/a/b Retrieve property a/b of
 * submodel {subModelId} <br>
 * - POST /aas/submodels/{subModelId}/operations/a Invoke operation a of
 * submodel {subModelId}<br>
 * - POST /aas/submodels/{subModelId}/operations/a/b Invoke operation a/b of
 * submodel {subModelId}
 * 
 * @author kuhn
 *
 */
public class VABHTTPInterface<ModelProvider extends IModelProvider> extends BasysHTTPServlet {
	
	private static Logger logger = LoggerFactory.getLogger(VABHTTPInterface.class);
	
	/**
	 * Version information to identify the version of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Reference to IModelProvider backend
	 */
	protected JSONProvider<ModelProvider> providerBackend = null;

	
	
	/**
	 * Constructor
	 */
	public VABHTTPInterface(ModelProvider provider) {
		// Store provider reference
		providerBackend = new JSONProvider<ModelProvider>(provider);
	}

	
	/**
	 * Access model provider
	 */
	public ModelProvider getModelProvider() {
		return providerBackend.getBackendReference();
	}

	/**
	 * Send JSON encoded response
	 */
	protected void sendJSONResponse(String path, PrintWriter outputStream, Object jsonValue) {
		// Output result
		outputStream.write(jsonValue.toString()); // FIXME throws nullpointer exception if jsonValue is null
		outputStream.flush();
	}

	
	/**
	 * Implement "Get" operation
	 * 
	 * Process HTTP get request - get sub model property value
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String path = extractPath(req);

			// Setup HTML response header
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");

			resp.setStatus(200);

			// Process get request
			providerBackend.processBaSysGet(path, resp.getOutputStream());
		} catch(ProviderException e) {
			int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
			resp.setStatus(httpCode);
			logger.debug("Exception in HTTP-GET. Response-code: " + httpCode, e);
		}
		
	}

	
	/**
	 * Implement "Set" operation
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String path = extractPath(req);
			String serValue = extractSerializedValue(req);
			logger.trace("DoPut: {}", serValue);

			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.setStatus(200);

			providerBackend.processBaSysSet(path, serValue.toString(), resp.getOutputStream());
		} catch(ProviderException e) {
			int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
			resp.setStatus(httpCode);
			logger.debug("Exception in HTTP-PUT. Response-code: " + httpCode, e);
		}
	}

	
	/**
	 * Handle HTTP POST operation. Creates a new Property, Operation, Event,
	 * Submodel or AAS or AASX or invokes an operation.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String path = extractPath(req);
			
			setPostResponseHeader(resp);

			if (ServletFileUpload.isMultipartContent(req)) {
				handleMultipartFormDataRequest(req, path, resp);
			} else {
				handleJSONPostRequest(req, path, resp);
			}
		} catch (ProviderException e) {
			int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
			resp.setStatus(httpCode);
			logger.debug("Exception in HTTP-POST. Response-code: " + httpCode, e);
		}
	}

	
	/**
	 * Handle a HTTP PATCH operation. Updates a map or collection
	 * 
	 */
	@Override
	protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String path = extractPath(req);
			String serValue = extractSerializedValue(req);
			logger.trace("DoPatch: {}", serValue);

			resp.setStatus(200);

			providerBackend.processBaSysDelete(path, serValue, resp.getOutputStream());
		} catch(ProviderException e) {
			int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
			resp.setStatus(httpCode);
			logger.debug("Exception in HTTP-PATCH. Response-code: " + httpCode, e);
		}
	}

	
	/**
	 * Implement "Delete" operation. Deletes any resource under the given path.
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String path = extractPath(req);

			// No parameter to read! Provide serialized null
			String nullParam = "";

			resp.setStatus(200);


			providerBackend.processBaSysDelete(path, nullParam, resp.getOutputStream());
		} catch(ProviderException e) {
			int httpCode = ExceptionToHTTPCodeMapper.mapFromException(e);
			resp.setStatus(httpCode);
			logger.debug("Exception in HTTP-DELETE. Response-code: " + httpCode, e);
		}
	}

	
	private String extractPath(HttpServletRequest req) throws UnsupportedEncodingException {
		// Extract path
		String uri = req.getRequestURI();

		// Normalizes URI
		String nUri = "/" + VABPathTools.stripSlashes(uri);
		String contextPath = req.getContextPath();
		if (nUri.startsWith(contextPath) && nUri.length() > getEnvironmentPathSize(req) - 1) {
			String path = nUri.substring(getEnvironmentPathSize(req));
			String extractedParameters = extractParameters(req);

			path = VABPathTools.stripSlashes(path);

			if (extractedParameters.isEmpty()) {
				path += "/";
			} else {
				path += extractedParameters;
			}
			return path;
		}
		throw new MalformedRequestException("The passed path " + uri + " is not a possbile path for this server.");
	}

	/**
	 * Extracts request parameters from the request
	 * 
	 * @param req
	 * @return
	 */
	private String extractParameters(HttpServletRequest req) {
		Enumeration<String> parameterNames = req.getParameterNames();
		
		// Collect list of parameters
		List<String> parameters = new ArrayList<>();
		while (parameterNames.hasMoreElements()) {

			StringBuilder ret = new StringBuilder();
			String paramName = parameterNames.nextElement();
			ret.append(paramName);
			ret.append("=");

			String[] paramValues = req.getParameterValues(paramName);
			for (int i = 0; i < paramValues.length; i++) {
				ret.append(paramValues[i]);
			}
			parameters.add(ret.toString());

		}

		// If no parameter is existing, return an empty string. Else join the parameters
		// and return them prefixed with a ?
		if (parameters.isEmpty()) {
			return "";
		} else {
			StringJoiner joiner = new StringJoiner("&");
			parameters.stream().forEach(s -> joiner.add(s));
			return "?" + joiner.toString();
		}
	}

	private int getEnvironmentPathSize(HttpServletRequest req) {
		return req.getContextPath().length() + req.getServletPath().length();
	}


	/**
	 * Read serialized value 
	 * @param req
	 * @return
	 * @throws IOException
	 */
	private String extractSerializedValue(HttpServletRequest req) throws IOException {
		// https://www.baeldung.com/convert-input-stream-to-string#guava
        return getByteSource(req).asCharSource(Charsets.UTF_8).read();
	}
	
	/**
	 * Extracts input streams from request
	 * @param req
	 * @return
	 * @throws IOException
	 * @throws ServletException 
	 */
	private Collection<InputStream> extractInputStreams(HttpServletRequest req) throws IOException, ServletException{
		Collection<InputStream> fileStreams = new ArrayList<InputStream>();
		try {
	        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
	        for (FileItem item : items) {
	            if (!item.isFormField()) {
	                fileStreams.add(item.getInputStream());
	            }
	        }
	    } catch (FileUploadException e) {
	    	throw new ServletException("Cannot parse multipart request.", e);
	    }
		return fileStreams;
	}
	
	/**
	 * Gets a {@link ByteSource} from request stream
	 * @return
	 */
	private ByteSource getByteSource(HttpServletRequest req) {
		return new ByteSource() {
	        @Override
	        public InputStream openStream() throws IOException {
	            return req.getInputStream();
	        }
	    };
	}
	
	/**
	 * Setup HTML response header for HttpPost
	 * @param resp
	 */
	private void setPostResponseHeader(HttpServletResponse resp) {
		resp.setStatus(201);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
	}


	/**
	 * Handles multipart/form-data request in HttpPost
	 * @param req
	 * @param path
	 * @param resp
	 * @throws IOException
	 * @throws ServletException 
	 */
	private void handleMultipartFormDataRequest(HttpServletRequest req, String path, HttpServletResponse resp) throws IOException, ServletException {
		Collection<InputStream> fileStreams = extractInputStreams(req);	
		for (InputStream fileStream : fileStreams) {
		    providerBackend.processBaSysUpload(path, fileStream, resp.getOutputStream());
		    fileStream.close();	
		}
	}


	/**
	 * Handles POST request with JSON body
	 * @param req
	 * @param path
	 * @param resp
	 * @throws IOException
	 */
	private void handleJSONPostRequest(HttpServletRequest req, String path, HttpServletResponse resp) throws IOException {
		String serValue = extractSerializedValue(req);
		logger.trace("DoPost: {}", serValue);
		
		// Check if request is for property creation or operation invoke
		if (VABPathTools.isOperationInvokationPath(path)) {
			// Invoke BaSys VAB 'invoke' primitive
			providerBackend.processBaSysInvoke(path, serValue, resp.getOutputStream());
		} else {
			// Invoke the BaSys 'create' primitive
			providerBackend.processBaSysCreate(path, serValue, resp.getOutputStream());
		}
	}
}
