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
import org.eclipse.basyx.extensions.shared.authorization.AbacRule.RightModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logic for checking attribute based access rules against some (potentially occurred)
 * role x right x (aas id, submodel id, submodel element id) combination.
 *
 * @author wege
 */
public class AbacRulePip {
  private static final Logger logger = LoggerFactory.getLogger(AbacRulePip.class);
  private final AbacRuleSet abacRuleSet;

  public AbacRulePip(final AbacRuleSet abacRuleSet) {
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

  public boolean abacRuleIsRedacted(
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
        .filter(abacRule -> abacRule.getRightModifiers().contains(RightModifier.REDACTED))
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
