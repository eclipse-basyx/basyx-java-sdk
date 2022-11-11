package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
