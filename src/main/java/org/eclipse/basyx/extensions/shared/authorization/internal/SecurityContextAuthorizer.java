package org.eclipse.basyx.extensions.shared.authorization.internal;

import java.util.Optional;

import org.eclipse.basyx.extensions.aas.api.authorization.internal.IAASAPIAuthorizer;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal.ITaggedDirectoryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.IAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.ISubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.internal.ISubmodelAPIAuthorizer;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Checks authorization for a specific authority according to the current
 * security context
 *
 * @author espen
 *
 */

/**
 * @deprecated This class has been replaced in favor of more adapted and implementable authorizer interfaces
 * {@link org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.IAASAggregatorAuthorizer}
 * {@link IAASAPIAuthorizer}
 * {@link IAASRegistryAuthorizer}
 * {@link ITaggedDirectoryAuthorizer}
 * {@link ISubmodelAggregatorAuthorizer}
 * {@link ISubmodelAPIAuthorizer}
 */
@Deprecated
public class SecurityContextAuthorizer {
  public void throwExceptionInCaseOfInsufficientAuthorization(final String requiredAuthority) {
    final Optional<Authentication> authentication = getAuthentication();
    if (!authentication.isPresent()) {
      throw new ProviderException("Access denied for unauthenticated requestor");
    }
    if (!hasRequiredAuthority(authentication.get(), requiredAuthority)) {
      throw new ProviderException("Access denied as required authority is missing for requestor");
    }
  }

  private Optional<Authentication> getAuthentication() {
    final SecurityContext context = SecurityContextHolder.getContext();
    return Optional.ofNullable(context.getAuthentication());
  }

  private boolean hasRequiredAuthority(final Authentication authentication, final String requiredAuthority) {
    return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(requiredAuthority::equals);
  }
}