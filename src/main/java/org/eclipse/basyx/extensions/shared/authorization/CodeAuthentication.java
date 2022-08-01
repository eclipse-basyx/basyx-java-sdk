package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Collections;
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

  public static void setCodeAuthentication() {
    final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new CodeAuthentication());
    SecurityContextHolder.setContext(securityContext);
  }
}
