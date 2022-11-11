package org.eclipse.basyx.extensions.shared.authorization;

import org.springframework.security.core.GrantedAuthority;

/**
 * Utility methods for Granted Authority access control scheme.
 *
 * @author wege
 */
public class GrantedAuthorityUtil {
  private GrantedAuthorityUtil() {}

  public static <SubjectInformationType> void checkAuthority(final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator, final SubjectInformationType subjectInformation, final String requiredAuthority) throws InhibitException {
    if (grantedAuthorityAuthenticator.getAuthorities(subjectInformation).stream()
        .map(GrantedAuthority::getAuthority)
        .noneMatch(authority -> authority.equals(requiredAuthority))) {
      //final Supplier<InhibitException> effectiveInhibitExceptionSupplier = inhibitExceptionSupplier == null ? () -> new GrantedAuthorityInhibitException(requiredAuthority) : inhibitExceptionSupplier;
      //throw inhibitExceptionSupplier.get();
      throw new GrantedAuthorityInhibitException(requiredAuthority);
    }
  }
}
