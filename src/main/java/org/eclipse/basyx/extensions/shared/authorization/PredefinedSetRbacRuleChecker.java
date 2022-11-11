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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IRbacRuleChecker} that works with a predefined {@link RbacRuleSet}.
 *
 * @author wege
 */
public class PredefinedSetRbacRuleChecker implements IRbacRuleChecker {
  private static final Logger logger = LoggerFactory.getLogger(PredefinedSetRbacRuleChecker.class);
  private final RbacRuleSet rbacRuleSet;

  public PredefinedSetRbacRuleChecker(final RbacRuleSet rbacRuleSet) {
    this.rbacRuleSet = rbacRuleSet;
  }

  /**
   * Checks for a given rbac tuple if it exists within the predefined set.
   * @param roles roles of the subject
   * @param action action which needs authorization
   * @param targetInformation target attributes
   * @return true if the requested rbac tuple was found, false otherwise
   */
  public boolean checkRbacRuleIsSatisfied(
      final List<String> roles,
      final String action,
      final TargetInformation targetInformation
  ) {
    Stream<RbacRule> matchingRules = this.rbacRuleSet.getRules().parallelStream()
        .filter(rbacRule -> rbacRule.getRole().equals("*") || (roles != null && roles.stream().anyMatch(role -> rbacRule.getRole().equals(role))))
        .filter(rbacRule -> rbacRule.getAction().equals("*") || rbacRule.getAction().equals(action));

    for (final Map.Entry<String, String> targetInfo : targetInformation.entrySet()) {
      final String key = targetInfo.getKey();
      final String value = targetInfo.getValue();

      matchingRules = matchingRules.filter(rbacRule -> checkRegexStringMatch(rbacRule.getTargetInformation().get(key), value));
    }

    final Optional<RbacRule> matchingRule = matchingRules.findAny();
    logger.info("roles: {}, action: {}, targetInfo: {} - matching-rule?: {}", roles, action, targetInformation, matchingRule);
    return matchingRule.isPresent();
  }

  private boolean checkRegexStringMatch(final String ruleString, final String actualString) {
    if (ruleString == null) {
      return true;
    }
    return ruleString.equals("*") ||
        (actualString != null && actualString.matches(ruleString.replaceAll("\\*", "[A-Za-z0-9.]+")));
  }
}
