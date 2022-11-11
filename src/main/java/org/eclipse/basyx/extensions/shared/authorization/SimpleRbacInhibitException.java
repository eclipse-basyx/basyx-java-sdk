package org.eclipse.basyx.extensions.shared.authorization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Specialization of {@link InhibitException} for RBAC access control scheme.
 *
 * @author wege
 */
public class SimpleRbacInhibitException extends InhibitException {
  private final List<String> roles;
  private final String right;
  private final ITargetInformation targetInformation;

  private static final String MESSAGE_FORMAT = "no rule matching action=${action}, targetInfo=${targetInfo} role=(any of ${roles})";

  private static String replaceVars(final String right, final Map<String, String> targetInformationMap, final List<String> roles) {
    final Map<String, String> replaceMap = new HashMap<>();

    replaceMap.put("action", right);
    replaceMap.put("targetInfo", targetInformationMap.keySet().stream().map(key -> key + "=" + targetInformationMap.get(key)).collect(Collectors.joining(",", "{", "}")));
    replaceMap.put("roles", Arrays.toString(roles.toArray()));

    String message = MESSAGE_FORMAT;

    for (final Map.Entry<String, String> replaceMapEntry : replaceMap.entrySet()) {
      message = message.replaceAll(Pattern.quote("${" + replaceMapEntry.getKey() + "}"), Objects.toString(replaceMapEntry.getValue()));
    }

    return message;
  }

  public SimpleRbacInhibitException(final List<String> roles, final String right, final ITargetInformation targetInformation) {
    this(
        replaceVars(right, targetInformation, roles),
        roles,
        right,
        targetInformation
    );
  }

  private SimpleRbacInhibitException(final String message, final List<String> roles, final String right, final ITargetInformation targetInformation) {
    super(message);

    this.roles = roles;
    this.right = right;
    this.targetInformation = targetInformation;
  }

  @Override
  public InhibitException reductSmIdAsSemanticIdOfSmIdShortPath(final String smIdShortPath) {
    final Map<String, String> alteredTargetInformation = new HashMap<>(targetInformation);
    targetInformation.put("smId", String.format("(semantic id of %s)", smIdShortPath));
    return new SimpleRbacInhibitException(
        replaceVars(right, alteredTargetInformation, roles),
        roles,
        right,
        targetInformation
    );
  }
}
