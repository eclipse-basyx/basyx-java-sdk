package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Optional;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * A {@link ISubjectInformationProvider} that provides a {@link Jwt} obtained by the
 * {@link org.springframework.security.core.Authentication} from the (thread-local)
 * Spring Security context.
 *
 * @author wege
 */
public class JWTAuthenticationContextProvider implements ISubjectInformationProvider<Jwt> {
  @Override
  public Jwt get() {
    return getAuthentication().map(AbstractOAuth2TokenAuthenticationToken::getToken).orElse(null);
  }

  public Optional<JwtAuthenticationToken> getAuthentication() {
    final SecurityContext context = SecurityContextHolder.getContext();
    return Optional.ofNullable(context.getAuthentication())
        .filter(JwtAuthenticationToken.class::isInstance)
        .map(JwtAuthenticationToken.class::cast);
  }
}
