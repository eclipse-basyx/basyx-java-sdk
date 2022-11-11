package org.eclipse.basyx.extensions.shared.authorization;

/**
 * Specialization of {@link InhibitException} for Granted Authority access control scheme.
 *
 * @author wege
 */
public class GrantedAuthorityInhibitException extends InhibitException {
  private final String missingAuthority;

  public GrantedAuthorityInhibitException(final String missingAuthority) {
    super("no authority " + missingAuthority);

    this.missingAuthority = missingAuthority;
  }

  @Override
  public InhibitException reductSmIdAsSemanticIdOfSmIdShortPath(final String smIdShortPath) {
    return this;
  }
}
