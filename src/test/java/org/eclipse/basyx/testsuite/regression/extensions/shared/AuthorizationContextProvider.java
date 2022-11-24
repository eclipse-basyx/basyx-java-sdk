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
package org.eclipse.basyx.testsuite.regression.extensions.shared;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Can set various types of SecurityContexts for test purposes for testing
 * invalid, read or write access.
 *
 * @author espen, wege
 *
 */
public class AuthorizationContextProvider {
  private final String READ_SCOPE;
  private final String WRITE_SCOPE;
  private final String EXECUTE_SCOPE;

  public AuthorizationContextProvider(final String readScope, final String writeScope, final String executeScope) {
    READ_SCOPE = readScope;
    WRITE_SCOPE = writeScope;
    EXECUTE_SCOPE = executeScope;
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

  public void setSecurityContextWithExecuteAuthority() {
    final SecurityContext context = _getSecurityContextWithAuthorities(EXECUTE_SCOPE);
    SecurityContextHolder.setContext(context);
  }

  public void clearContext() {
    SecurityContextHolder.clearContext();
  }
}