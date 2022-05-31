package org.eclipse.basyx.testsuite.regression.extensions.shared;

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

  public AuthorizationContextProvider(final String readScope, final String writeScope) {
    READ_SCOPE = readScope;
    WRITE_SCOPE = writeScope;
  }

  private SecurityContext _getSecurityContextWithAuthorities(final String... authorities) {
    final SecurityContext context = SecurityContextHolder.createEmptyContext();
    final Authentication authentication = new TestingAuthenticationToken(null, null, authorities);
    context.setAuthentication(authentication);
    return context;
  }

  public void setEmptySecurityContext() {
    final SecurityContext context = SecurityContextHolder.createEmptyContext();
    SecurityContextHolder.setContext(context);
  }

  public void setSecurityContextWithoutAuthorities() {
    final SecurityContext context = _getSecurityContextWithAuthorities();
    SecurityContextHolder.setContext(context);
  }

  public void setSecurityContextWithReadAuthority() {
    final SecurityContext context = _getSecurityContextWithAuthorities(READ_SCOPE);
    SecurityContextHolder.setContext(context);
  }

  public void setSecurityContextWithWriteAuthority() {
    final SecurityContext context = _getSecurityContextWithAuthorities(WRITE_SCOPE);
    SecurityContextHolder.setContext(context);
  }

  public void clearContext() {
    SecurityContextHolder.clearContext();
  }
}