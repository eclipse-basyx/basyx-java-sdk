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

import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starter Class for Apache Tomcat HTTP server that adds the provided servlets and respective mappings on startup.
 * 
 * @author pschorn, espen, haque
 * 
 */
public class BaSyxHTTPServer {
	
	private static Logger logger = LoggerFactory.getLogger(BaSyxHTTPServer.class);
	
	private Tomcat tomcat;

	static {
		// Enable coding of forward slash in tomcat
		System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
		
		// Throw exception on startup error, unless user explicitly disables it
		if (System.getProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE") == null) {
			System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
		}
	}
	   
	/**
	 * Constructor
	 * 
	 * Create new Tomcat instance and add the provided servlet mappings
	 * 
	 * @param context
	 *            Basyx context with of url mappings to HTTPServlet
	 */

	public BaSyxHTTPServer(BaSyxContext context) {
		// Instantiate and setup Tomcat server
		tomcat = new Tomcat();

		// Set random name to prevent lifecycle expections during shutdown of multiple
		// instances
		tomcat.getEngine().setName(UUID.randomUUID().toString());
		
		if (context.isSecuredConnectionEnabled()) {
			Connector httpsConnector = tomcat.getConnector();
			configureSslConnector(context, httpsConnector);
		} else {
			tomcat.setPort(context.port);
		}
		
		tomcat.setHostname(context.hostname);
		tomcat.getHost().setAppBase(".");

		// Create servlet context
		// - Base path for resource files
		File docBase = new File(context.docBasePath); // System.getProperty("java.io.tmpdir"));
		// - Create context for servlets
		Context rootCtx = tomcat.addContext(context.contextPath, docBase.getAbsolutePath());

		// Iterate all servlets in context
		Iterator<Entry<String, HttpServlet>> it = context.entrySet().iterator();
		while (it.hasNext()) {
			// Servlet entry
			Entry<String, HttpServlet> entry = it.next();

			// Servlet mapping
			String mapping = entry.getKey();
			HttpServlet servlet = entry.getValue();

			// Add new Servlet and Mapping to tomcat environment
			Tomcat.addServlet(rootCtx, Integer.toString(servlet.hashCode()), servlet);
			rootCtx.addServletMappingDecoded(mapping, Integer.toString(servlet.hashCode()));
		}
	}
	
	/**
	 * SSL Configuration for SSL connector
	 * @param context
	 * @param httpsConnector
	 */
	private void configureSslConnector(BaSyxContext context, Connector httpsConnector) {
		httpsConnector.setPort(context.port);
		httpsConnector.setSecure(true);
		httpsConnector.setScheme("https");
		httpsConnector.setAttribute("keystoreFile", context.getCertificatePath());
		httpsConnector.setAttribute("clientAuth", "false");
		httpsConnector.setAttribute("sslProtocol", "TLS");
		httpsConnector.setAttribute("SSLEnabled", true);
		httpsConnector.setAttribute("protocol", "HTTP/1.1");
		httpsConnector.setAttribute("keystorePass", context.getKeyPassword());
		
		httpsConnector.setAttribute("keyAlias", "tomcat");
		
		httpsConnector.setAttribute("maxThreads", "200");
		httpsConnector.setAttribute("protocol", "org.apache.coyote.http11.Http11AprProtocol");
	 }
	
	/**
	 * Starts the server in a new thread to avoid blocking the main thread
	 * 
	 * <p>
	 * This method blocks until the server is up and running.
	 * 
	 * <p>
	 * If an error occurs during server startup the process is aborted and the method returns immediately. 
	 * {@link #hasEnded()} returns <code>true</code> in this case.
	 * <br>This behavior can be disabled by setting the system property 
	 * <code>org.apache.catalina.startup.EXIT_ON_INIT_FAILURE = false</code>, for instance with the <code>-D</code> 
	 * command line option when launching the JVM, or through {@link System#setProperty(String, String)} (before the 
	 * first call to {@link BaSyxHTTPServer}). In this case the startup is finished regardless of any errors and 
	 * subsequent calls to {@link #hasEnded()} return <code>false</code>, but the server might be left in an undefined 
	 * and non-functional state.
	 * 
	 * <p>
	 * TODO: Throw exception upon startup failure. This is a breaking change, so wait until next major version.
	 */
	public void start() {
		logger.trace("Starting Tomcat.....");
		
		Thread serverThread = new Thread(() -> {
			try {
				tomcat.stop();

				// Adds listener that notifies the tomcat object when the server has started
				tomcat.getServer().addLifecycleListener(new LifecycleListener() {
					@Override
					public void lifecycleEvent(LifecycleEvent event) {
						if (event.getLifecycle().getState() == LifecycleState.STARTED) {
							synchronized (tomcat) {
								tomcat.notifyAll();
							}
						}
					}
				});

				tomcat.start();
				
				// Keeps the server thread alive until the server is shut down
				tomcat.getServer().await();
			} catch (LifecycleException e) {
				logger.error("Failed to start HTTP server.", e);
				
				synchronized (tomcat) {
					tomcat.notifyAll();
				}
			}
		});
		serverThread.start();
		
		// Wait until tomcat is started before returning
		EnumSet<LifecycleState> returnStates = EnumSet.of(LifecycleState.STARTED, LifecycleState.FAILED);
		synchronized (tomcat) {
			try {
				while (!returnStates.contains(tomcat.getServer().getState())) {
					tomcat.wait();
				}
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting for tomcat to start. Stopping tomcat.", e);
				shutdown();
			}
		}
	}

	/**
	 * This Method stops and destroys the tomcat instance. This is important since Tomcat would be already 
	 * bound to port 8080 when new tests are run that require a start of tomcat
	 */
	public void shutdown() {
		logger.trace("Shutting down BaSyx HTTP Server...");
		
		try {
			tomcat.stop();
			tomcat.destroy();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in shutdown", e);
		}
	}
	
	/**
	 * Returns a value indicating whether the server is currently running.
	 * 
	 * @return <code>false</code> if the server is running, <code>true</code> otherwise.
	 */
	public boolean hasEnded() {
		return tomcat.getServer().getState() != LifecycleState.STARTED;
	}
}
