package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provides an {@link Authentication} object taken from the (thread-local) {@link SecurityContext}.
 *
 * @author wege
 */
public class AuthenticationContextProvider implements ISubjectInformationProvider<Authentication> {
  @Override
  public Authentication get() {
    return getAuthentication().orElse(null);
  }

  public static Optional<Authentication> getAuthentication() {
    final SecurityContext context = SecurityContextHolder.getContext();
    return Optional.ofNullable(context.getAuthentication());
  }
}
