package org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Can set various types of SecurityContexts for test purposes for testing
 * invalid, read or write access.
 * 
 * @author espen
 *
 */
public class AuthorizationContextProvider {
	private final String READ_SCOPE;
	private final String WRITE_SCOPE;

	public AuthorizationContextProvider(String readScope, String writeScope) {
		READ_SCOPE = readScope;
		WRITE_SCOPE = writeScope;
	}

	private SecurityContext _getSecurityContextWithAuthorities(String... authorities) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final Authentication authentication = new TestingAuthenticationToken(null, null, authorities);
		context.setAuthentication(authentication);
		return context;
	}

	public void setEmptySecurityContext() {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(context);
	}

	public void setSecurityContextWithoutAuthorities() {
		SecurityContext context = _getSecurityContextWithAuthorities();
		SecurityContextHolder.setContext(context);
	}

	public void setSecurityContextWithReadAuthority() {
		SecurityContext context = _getSecurityContextWithAuthorities(READ_SCOPE);
		SecurityContextHolder.setContext(context);
	}

	public void setSecurityContextWithWriteAuthority() {
		SecurityContext context = _getSecurityContextWithAuthorities(WRITE_SCOPE);
		SecurityContextHolder.setContext(context);
	}

	public void clearContext() {
		SecurityContextHolder.clearContext();
	}
}
