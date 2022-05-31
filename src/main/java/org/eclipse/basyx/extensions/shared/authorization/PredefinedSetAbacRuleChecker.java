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
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IAbacRuleChecker} that works with a predefined AbacRuleSet.
 *
 * @author wege
 */
public class PredefinedSetAbacRuleChecker implements IAbacRuleChecker {
  private static final Logger logger = LoggerFactory.getLogger(PredefinedSetAbacRuleChecker.class);
  private final AbacRuleSet abacRuleSet;

  public PredefinedSetAbacRuleChecker(final AbacRuleSet abacRuleSet) {
    this.abacRuleSet = abacRuleSet;
  }

  /**
   * Checks for a given abac tuple if it exists within the predefined set.
   * @param roles roles of the subject
   * @param action action which needs authorization
   * @param aasId id of the asset admininstration shell or null
   * @param smId id of the submodel or null
   * @param smElIdShortPath id of the submodel element or null
   * @return true if the requested abac tuple was found, false otherwise
   */
  public boolean checkAbacRuleIsSatisfied(
      final List<String> roles,
      final String action,
      final String aasId,
      final String smId,
      final String smElIdShortPath
  ) {
    final Optional<AbacRule> matchingRule = this.abacRuleSet.getRules().parallelStream()
        .filter(abacRule -> abacRule.getRole().equals("*") || roles.stream().anyMatch(role -> abacRule.getRole().equals(role)))
        .filter(abacRule -> abacRule.getRight().equals("*") || abacRule.getRight().equals(action))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getAasId(), aasId))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getSmId(), smId))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getSmElIdShortPath(), smElIdShortPath))
        .findAny();
    logger.info("roles: {}, action: {}, aasId: {}, smId: {}, smElIdShortPath: {} - matching-rule?: {}", roles, action, aasId, smId, smElIdShortPath, matchingRule);
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
