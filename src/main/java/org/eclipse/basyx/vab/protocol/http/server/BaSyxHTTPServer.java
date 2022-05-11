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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

/**
 * Starter Class for Apache Tomcat HTTP server that adds the provided servlets
 * and respective mappings on startup.
 * 
 * @author pschorn, espen, haque, danish
 * 
 */
public class BaSyxHTTPServer {

	private static final Logger logger = LoggerFactory.getLogger(BaSyxHTTPServer.class);

	private final Tomcat tomcat;

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
		final Context rootCtx = tomcat.addContext(context.contextPath, docBase.getAbsolutePath());

		context.getJwtBearerTokenAuthenticationConfiguration().ifPresent(jwtBearerTokenAuthenticationConfiguration -> addSecurityFiltersToContext(rootCtx, jwtBearerTokenAuthenticationConfiguration));

		Iterator<Entry<String, HttpServlet>> servletIterator = context.entrySet().iterator();
		
		while (servletIterator.hasNext()) {
			addNewServletAndMappingToTomcatEnvironment(context, rootCtx, servletIterator);
		}
	}

	private void addNewServletAndMappingToTomcatEnvironment(BaSyxContext context, final Context rootCtx, Iterator<Entry<String, HttpServlet>> servletIterator) {
		Entry<String, HttpServlet> entry = servletIterator.next();

		String mapping = entry.getKey();
		HttpServlet servlet = entry.getValue();
		
		setCorsOriginToServlet(context, servlet);

		Tomcat.addServlet(rootCtx, Integer.toString(servlet.hashCode()), servlet);
		rootCtx.addServletMappingDecoded(mapping, Integer.toString(servlet.hashCode()));
	}

	private void setCorsOriginToServlet(BaSyxContext context, HttpServlet servlet) {
		if(!isCorsOriginDefined(context)) {
			return;
		}
		
		try {
			((BasysHTTPServlet) servlet).setCorsOrigin(context.getAccessControlAllowOrigin());
		} catch (RuntimeException e) {
			logger.info("DefaultServlet cannot be cast to BasysHTTPServlet " + e);
		}
	}

	private boolean isCorsOriginDefined(BaSyxContext context) {
		return context.getAccessControlAllowOrigin() != null;
	}

	private void addSecurityFiltersToContext(final Context context, final JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration) {
		final FilterChainProxy filterChainProxy = createFilterChainProxy(jwtBearerTokenAuthenticationConfiguration);
		addFilterChainProxyFilterToContext(context, filterChainProxy);
	}

	private void addFilterChainProxyFilterToContext(final Context context, final FilterChainProxy filterChainProxy) {
		final FilterDef filterChainProxyFilterDefinition = createFilterChainProxyFilterDefinition(filterChainProxy);
		context.addFilterDef(filterChainProxyFilterDefinition);

		final FilterMap filterChainProxyFilterMapping = createFilterChainProxyFilterMap();
		context.addFilterMap(filterChainProxyFilterMapping);
	}

	private FilterMap createFilterChainProxyFilterMap() {
		final FilterMap filterChainProxyFilterMapping = new FilterMap();
		filterChainProxyFilterMapping.setFilterName(FilterChainProxy.class.getSimpleName());
		filterChainProxyFilterMapping.addURLPattern("/*");
		return filterChainProxyFilterMapping;
	}

	private FilterDef createFilterChainProxyFilterDefinition(final FilterChainProxy filterChainProxy) {
		final FilterDef filterChainProxyFilterDefinition = new FilterDef();
		filterChainProxyFilterDefinition.setFilterName(FilterChainProxy.class.getSimpleName());
		filterChainProxyFilterDefinition.setFilterClass(FilterChainProxy.class.getName());
		filterChainProxyFilterDefinition.setFilter(filterChainProxy);
		return filterChainProxyFilterDefinition;
	}

	private FilterChainProxy createFilterChainProxy(final JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration) {
		final FilterChainProxy filterChainProxy = new FilterChainProxy(createSecurityFilterChain(jwtBearerTokenAuthenticationConfiguration));
		filterChainProxy.setFirewall(createHttpFirewall());
		return filterChainProxy;
	}

	private HttpFirewall createHttpFirewall() {
		final StrictHttpFirewall httpFirewall = new StrictHttpFirewall();

		// '/' is valid character in an Internationalized Resource Identifier (IRI)
		httpFirewall.setAllowUrlEncodedSlash(true);

		return httpFirewall;
	}

	private SecurityFilterChain createSecurityFilterChain(final JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration) {
		final List<Filter> sortedListOfFilters = new ArrayList<>();

		sortedListOfFilters.add(createBearerTokenAuthenticationFilter(jwtBearerTokenAuthenticationConfiguration));
		sortedListOfFilters.add(createExceptionTranslationFilter());

		return new DefaultSecurityFilterChain(AnyRequestMatcher.INSTANCE, sortedListOfFilters);
	}

	private ExceptionTranslationFilter createExceptionTranslationFilter() {
		final BearerTokenAuthenticationEntryPoint authenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
		return new ExceptionTranslationFilter(authenticationEntryPoint);
	}

	private BearerTokenAuthenticationFilter createBearerTokenAuthenticationFilter(final JwtBearerTokenAuthenticationConfiguration jwtBearerTokenAuthenticationConfiguration) {
		final JwtDecoder jwtDecoder = createJwtDecoder(jwtBearerTokenAuthenticationConfiguration.getIssuerUri(), jwtBearerTokenAuthenticationConfiguration.getJwkSetUri(),
				jwtBearerTokenAuthenticationConfiguration.getRequiredAud().orElse(null));
		final JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
		final AuthenticationManager authenticationManager = new ProviderManager(jwtAuthenticationProvider);
		return new BearerTokenAuthenticationFilter(authenticationManager);
	}

	private JwtDecoder createJwtDecoder(final String issuerUri, final String jwkSetUri, @Nullable final String requiredAud) {
		final NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).jwsAlgorithm(SignatureAlgorithm.from("RS256")).build();
		nimbusJwtDecoder.setJwtValidator(createOAuth2TokenValidator(issuerUri, requiredAud));

		return nimbusJwtDecoder;
	}

	private OAuth2TokenValidator<Jwt> createOAuth2TokenValidator(final String issuerUri, @Nullable final String requiredAud) {
		List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
		validators.add(JwtValidators.createDefaultWithIssuer(issuerUri));

		if (requiredAud != null) {
			validators.add(createJwtClaimValidatorForRequiredAudience(requiredAud));
		}

		return new DelegatingOAuth2TokenValidator<>(validators);
	}

	private JwtClaimValidator<Collection<String>> createJwtClaimValidatorForRequiredAudience(final String requiredAud) {
		return new JwtClaimValidator<>(JwtClaimNames.AUD, aud -> null != aud && aud.contains(requiredAud));
	}

	/**
	 * SSL Configuration for SSL connector
	 * 
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
	 * If an error occurs during server startup the process is aborted and the
	 * method returns immediately. {@link #hasEnded()} returns <code>true</code> in
	 * this case. <br>
	 * This behavior can be disabled by setting the system property
	 * <code>org.apache.catalina.startup.EXIT_ON_INIT_FAILURE = false</code>, for
	 * instance with the <code>-D</code> command line option when launching the JVM,
	 * or through {@link System#setProperty(String, String)} (before the first call
	 * to {@link BaSyxHTTPServer}). In this case the startup is finished regardless
	 * of any errors and subsequent calls to {@link #hasEnded()} return
	 * <code>false</code>, but the server might be left in an undefined and
	 * non-functional state.
	 * 
	 * <p>
	 * TODO: Throw exception upon startup failure. This is a breaking change, so
	 * wait until next major version.
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
	 * This Method stops and destroys the tomcat instance. This is important since
	 * Tomcat would be already bound to port 8080 when new tests are run that
	 * require a start of tomcat
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
	 * @return <code>false</code> if the server is running, <code>true</code>
	 *         otherwise.
	 */
	public boolean hasEnded() {
		return tomcat.getServer().getState() != LifecycleState.STARTED;
	}
}
