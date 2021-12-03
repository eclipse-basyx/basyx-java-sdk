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

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServlet;



/**
 * BaSyx context that contains an Industrie 4.0 Servlet infrastructure 
 * 
 * @author kuhn, haque
 *
 */
public class BaSyxContext extends HashMap<String, HttpServlet> {

	
	/**
	 * Version of serialized instance
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Requested server context path
	 */
	protected String contextPath;
	
	
	/**
	 * Requested server documents base path
	 */
	protected String docBasePath;
	
	/**
	 * hostname, e.g. ip or localhost
	 */
	protected String hostname;

	/**
	 * Requested Tomcat apache port
	 */
	protected int port;
	
	/**
	 * Indicates whether the connection is secures or not
	 */
	private boolean isSecuredConnection;
	
	/**
	 * Password of the SSL key
	 */
	private String keyPassword;
	
	/**
	 * Path to the certificate
	 */
	private String certificatePath;
	
	/**
	 * Servlet parameter
	 */
	protected Map<String, Map<String, String>> servletParameter = new HashMap<>();


	public Object AASHTTPServerResource;

	@Nullable
	private JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration ;
	
	/**
	 * Constructor with default port
	 */
	public BaSyxContext(String reqContextPath, String reqDocBasePath) {
		// Invoke constructor
		this(reqContextPath, reqDocBasePath, "localhost", 8080);
	}

	
	/**
	 * Initiates a BasyxContext
	 * @param reqContextPath context path
	 * @param reqDocBasePath  base path of doc
	 * @param hostn host name
	 * @param reqPort connection port
	 */
	public BaSyxContext(String reqContextPath, String reqDocBasePath, String hostn, int reqPort) {
		// Store context path and doc base path
		contextPath = reqContextPath;
		docBasePath = reqDocBasePath;
		hostname = hostn;
		port        = reqPort;
		isSecuredConnection = false;
	}
	
	/**
	 * Initiates a BasyxContext. this constructor can indicate whether 
	 * the connection is secured or not
	 * @param reqContextPath context path
	 * @param reqDocBasePath  base path of doc
	 * @param hostn host name
	 * @param reqPort connection port
	 * @param isSecuredCon boolean value indicating the connection is secured or not
	 * @param keyPass password of the SSL key
	 * @param keyPath path to the SSL certificate
	 */
	public BaSyxContext(String reqContextPath, String reqDocBasePath, String hostn, int reqPort, boolean isSecuredCon, String keyPath,  String keyPass) {
		this(reqContextPath, reqDocBasePath, hostn, reqPort);
		this.isSecuredConnection = isSecuredCon;
		this.certificatePath = keyPath;
		this.keyPassword = keyPass;
	}
	
	
	/**
	 * Add a servlet mapping
	 */
	public BaSyxContext addServletMapping(String key, HttpServlet servlet) {
		// Add servlet mapping
		put(key, servlet);

		// Return 'this' reference to enable chaining of operations
		return this;
	}

	/**
	 * Add a servlet mapping with parameter
	 */
	public BaSyxContext addServletMapping(String key, HttpServlet servlet, Map<String, String> servletParameter) {
		// Add servlet mapping
		addServletMapping(key, servlet);
		
		// Add Servlet parameter
		addServletParameter(key, servletParameter);

		// Return 'this' reference to enable chaining of operations
		return this;
	}

	
	/**
	 * Add servlet parameter
	 */
	public BaSyxContext addServletParameter(String key, Map<String, String> parameter) {
		// Add servlet parameter
		servletParameter.put(key, parameter);
		
		// Return 'this' reference to enable chaining of operations
		return this;
	}
	
	
	
	/**
	 * Get servlet parameter
	 */
	public Map<String, String> getServletParameter(String key) {
		// Return servlet parameter iff parameter map contains requested key
		if (servletParameter.containsKey(key)) return servletParameter.get(key);
		
		// Return empty map
		return new HashMap<String, String>();
	}
	
	
	/**
	 * Return Tomcat server port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Return Tomcat context path.
	 */
	public String getContextPath() {
	    return contextPath;
	}
	
	/**
	 * Return Tomcat hostname.
	 */
	public String getHostname() {
	    return hostname;
	}
	
	/**
	 * Returns whether the secured connection enabled or not
	 * @return
	 */
	public boolean isSecuredConnectionEnabled() {
		return this.isSecuredConnection;
	}

	/**
	 * Returns password of the SSL key
	 * @return
	 */
	public String getKeyPassword() {
		return keyPassword;
	}


	/**
	 * Sets password of the SSL key
	 * @param keyPassword
	 */
	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}


	/**
	 * Returns Path to the certificate
	 * @return
	 */
	public String getCertificatePath() {
		return certificatePath;
	}


	/**
	 * Sets Certificate Path
	 * @param certificatePath
	 */
	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	public Optional<JwtBearerTokenAuthenticationConfiguration> getJwtBearerTokenAuthenticationConfiguration() {
		return Optional.ofNullable(jwtBearerTokenAuthenticationConfiguration);
	}

	public void setJwtBearerTokenAuthenticationConfiguration(@Nullable final JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration) {
		this.jwtBearerTokenAuthenticationConfiguration = jwtBearerTokenAuthenticationConfiguration;
	}
}

