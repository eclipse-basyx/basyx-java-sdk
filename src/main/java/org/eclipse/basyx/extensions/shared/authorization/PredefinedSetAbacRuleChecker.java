/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.shared.authorization;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link AbacRuleChecker} that works with a predefined AbacRuleSet.
 *
 * @author wege
 */
public class PredefinedSetAbacRuleChecker implements AbacRuleChecker {
  private static final Logger logger = LoggerFactory.getLogger(PredefinedSetAbacRuleChecker.class);
  private final AbacRuleSet abacRuleSet;

  public PredefinedSetAbacRuleChecker(final AbacRuleSet abacRuleSet) {
    this.abacRuleSet = abacRuleSet;
  }

  public boolean abacRuleGrantsPermission(
      final List<String> roles,
      final String right,
      final String aasId,
      final String smId,
      final String smElId
  ) {
    final Optional<AbacRule> matchingRule = this.abacRuleSet.getRules().parallelStream()
        .filter(abacRule -> abacRule.getRole().equals("*") || roles.stream().anyMatch(role -> abacRule.getRole().equals(role)))
        .filter(abacRule -> abacRule.getRight().equals("*") || abacRule.getRight().equals(right))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getAasId(), aasId))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getSmId(), smId))
        .filter(abacRule -> checkRegexStringMatch(abacRule.getSmElId(), smElId))
        .findAny();
    logger.info("roles: {}, right: {}, aasId: {}, smId: {}, smElId: {} - matching-rule?: {}", roles, right, aasId, smId, smElId, matchingRule);
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
