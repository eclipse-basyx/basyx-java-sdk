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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of {@link AbacRule} used in policy enforcement points or in policy information points.
 *
 * @author wege
 */
public class AbacRuleSet {
	private final Set<AbacRule> rules;

	public AbacRuleSet() {
		this.rules = new HashSet<>();
	}

	public Set<AbacRule> getRules() {
		return Collections.unmodifiableSet(this.rules);
	}

	public boolean addRule(final AbacRule abacRule) {
		return this.rules.add(abacRule);
	}

	public boolean deleteRule(final AbacRule abacRule) {
		return this.rules.remove(abacRule);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbacRuleSet{");
		sb.append("rules=").append(rules);
		sb.append('}');
		return sb.toString();
	}
}
