package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Optional;

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
