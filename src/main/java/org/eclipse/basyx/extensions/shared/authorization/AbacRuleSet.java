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
