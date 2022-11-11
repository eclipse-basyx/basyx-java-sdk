package org.eclipse.basyx.extensions.shared.authorization;

import java.util.List;

/**
 * Utility methods for RBAC access control scheme.
 *
 * @author wege
 */
public class SimpleRbacUtil {
  private SimpleRbacUtil() {}

  public static <SubjectInformationType> void checkRule(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator, final SubjectInformationType subjectInformation, final String action, final ITargetInformation targetInformation) throws SimpleRbacInhibitException {
    final List<String> roles = roleAuthenticator.getRoles(subjectInformation);
    if (!rbacRuleChecker.checkRbacRuleIsSatisfied(
        roles,
        action,
        targetInformation
    )) {
      throw new SimpleRbacInhibitException(roles, action, targetInformation);
    }
  }
}
