/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
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
  private final TargetInformation targetInformation;

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

  public SimpleRbacInhibitException(final List<String> roles, final String right, final TargetInformation targetInformation) {
    this(
        replaceVars(right, targetInformation, roles),
        roles,
        right,
        targetInformation
    );
  }

  private SimpleRbacInhibitException(final String message, final List<String> roles, final String right, final TargetInformation targetInformation) {
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
