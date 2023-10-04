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
package org.eclipse.basyx.extensions.shared.authorization.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IRbacRuleChecker} that works with a predefined
 * {@link RbacRuleSet}.
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
	 *
	 * @param roles
	 *            roles of the subject
	 * @param action
	 *            action which needs authorization
	 * @param targetInformation
	 *            target attributes
	 * @return true if the requested rbac tuple was found, false otherwise
	 */
	public boolean checkRbacRuleIsSatisfied(final List<String> roles, final String action, final TargetInformation targetInformation) {
		final Optional<RbacRule> matchingRule = getMatchingRules(roles, action, targetInformation).findAny();
		logger.info("roles: {}, action: {}, targetInfo: {} - matching-rule?: {}", roles, action, targetInformation, matchingRule);
		return matchingRule.isPresent();
	}

	private Stream<RbacRule> getMatchingRules(final List<String> roles, final String action, final TargetInformation targetInformation) {
		return this.rbacRuleSet.getRules().parallelStream().filter(rbacRule -> checkRolesMatchRbacRule(rbacRule, roles)).filter(rbacRule -> checkActionMatchesRbacRule(rbacRule, action))
				.filter(rbacRule -> checkRbacRuleMatchesTargetInformation(rbacRule, targetInformation));
	}

	private boolean checkRolesMatchRbacRule(final RbacRule rbacRule, final List<String> roles) {
		return rbacRule.getRole().equals("*") || (roles != null && roles.stream().anyMatch(role -> rbacRule.getRole().equals(role)));
	}

	private boolean checkActionMatchesRbacRule(final RbacRule rbacRule, final String action) {
		return rbacRule.getAction().equals("*") || rbacRule.getAction().equals(action);
	}

	private boolean checkRbacRuleMatchesTargetInformation(final RbacRule rbacRule, final TargetInformation matchTargetInformation) {
		final TargetInformation rbacRuleTargetInformation = rbacRule.getTargetInformation();
		if (!rbacRuleTargetInformation.getClass().isAssignableFrom(matchTargetInformation.getClass())) {
			// return false if the type of the target is not the same or a subtype of the target information specified in the rbac rule
			// otherwise two target information objects of unrelated types may be found equal if their properties match since
			// the type property is not considered
			return false;
		}

		final Map<String, String> matchTargetInformationMap = matchTargetInformation.toMap();
		final Map<String, String> rbacRuleTargetInformationMap = rbacRule.getTargetInformation().toMap();
		for (final Map.Entry<String, String> matchTargetInformationMapEntry : matchTargetInformationMap.entrySet()) {
			final String key = matchTargetInformationMapEntry.getKey();
			final String matchTargetInformationSingleValue = matchTargetInformationMapEntry.getValue();
			final String rbacRuleValue = rbacRuleTargetInformationMap.get(key);

			if (!checkRegexStringMatch(rbacRuleValue, matchTargetInformationSingleValue)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkRegexStringMatch(final String actualString, final String requiredString) {
		if (requiredString == null) {
			return true;
		}
		if (actualString == null) {
			return false;
		}
		return actualString.equals("*") || requiredString.matches(actualString.replaceAll("\\*", "[A-Za-z0-9.]+"));
	}
}
