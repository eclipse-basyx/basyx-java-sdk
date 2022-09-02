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
package org.eclipse.basyx.vab.protocol.http.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;

/**
 * HTTP Servelet superclass to enable HTTP Patch
 * 
 * @author pschorn, danish
 *
 */
public abstract class BasysHTTPServlet extends HttpServlet {

	/**
	 * Version of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Parameter map
	 */
	protected Map<String, String> servletParameter = new LinkedHashMap<>();
	
	protected String corsOrigin;

	/**
	 * GSON instance
	 */
	protected GSONTools serializer = new GSONTools(new DefaultTypeFactory());

	/**
	 * Dispatch service call
	 */
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		addCORSHeaderIfConfigured(response);
		
		if (request.getMethod().equalsIgnoreCase("PATCH")) {
			doPatch(request, response);
		} else {
			super.service(request, response);
		}
	}

	private void addCORSHeaderIfConfigured(HttpServletResponse response) {
		if(!isCorsOriginDefined()) {
			return;
		}
		
		addCorsHeaderToResponse(response);
	}
	
	private boolean isCorsOriginDefined() {
		return getCorsOrigin() != null;
	}

	private void addCorsHeaderToResponse(HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", getCorsOrigin());
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH");
		response.addHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
	}

	/**
	 * Implement Patch request
	 */
	protected abstract void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

	/**
	 * Add a servlet parameter
	 */

	public BasysHTTPServlet withParameter(String parameter, String value) {
		// Add parameter
		servletParameter.put(parameter, value);

		// Return this instance
		return this;
	}

	/**
	 * Get init parameter of servlet
	 */
	@Override
	public String getInitParameter(String name) {
		// Check if servletParameter map contains requested parameter
		if (servletParameter.containsKey(name))
			return servletParameter.get(name);

		// Call base method
		return super.getInitParameter(name);
	}

	/**
	 * Send a response
	 */
	protected void sendResponse(Object value, PrintWriter outputStream) {
		// Output result
		outputStream.write(serializer.serialize(value));
		outputStream.flush();
	}
	
	public String getCorsOrigin() {
		return corsOrigin;
	}

	public void setCorsOrigin(String corsOrigin) {
		this.corsOrigin = corsOrigin;
	}

}
