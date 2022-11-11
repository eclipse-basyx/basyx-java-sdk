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
package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Authentication type that indicates that execution was caused by internal code and therefore should bypass access control to get the required information or do the required changes.
 *
 * @author wege
 */
public class CodeAuthentication extends AbstractAuthenticationToken {
  public CodeAuthentication() {
    super(Collections.emptyList());
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }

  public static boolean isCodeAuthentication() {
    return AuthenticationContextProvider.getAuthentication().map(CodeAuthentication.class::isInstance).orElse(false);
  }

  public static class CodeAuthenticationAreaHandler implements AutoCloseable {
    @Override
    public void close() {
      leaveCodeAuthenticationArea();
    }
  }

  private static final ThreadLocal<Deque<CodeAuthenticationAreaHandler>> codeAuthenticationAreaHandlerStack = ThreadLocal.withInitial(
      LinkedList::new);

  public static void leaveCodeAuthenticationArea() {
    codeAuthenticationAreaHandlerStack.get().pop();
    if (codeAuthenticationAreaHandlerStack.get().isEmpty()) {
      unsetCodeAuthentication();
    }
  }

  public static CodeAuthenticationAreaHandler enterCodeAuthenticationArea() {
    final CodeAuthenticationAreaHandler handler = new CodeAuthenticationAreaHandler();
    codeAuthenticationAreaHandlerStack.get().add(handler);
    setCodeAuthentication();
    return handler;
  }

  private static final ThreadLocal<SecurityContext> previousSecurityContext = new ThreadLocal<>();

  public static void setCodeAuthentication() {
    if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() instanceof CodeAuthentication) {
      return;
    }

    previousSecurityContext.set(SecurityContextHolder.getContext());

    final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new CodeAuthentication());
    SecurityContextHolder.setContext(securityContext);
  }

  public static void unsetCodeAuthentication() {
    SecurityContextHolder.clearContext();
    if (previousSecurityContext.get() != null) {
      SecurityContextHolder.setContext(previousSecurityContext.get());
      previousSecurityContext.remove();
    }
  }
}
