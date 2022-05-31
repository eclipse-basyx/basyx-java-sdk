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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single attribute based access control rule consisting of
 * role x right x (aas id, submodel id, submodel element id).
 *
 * @author wege
 */
public class AbacRule {
	private final String role;
	private final String right;
	private final String aasId;
	private final String smId;
	private final String smElIdShortPath;

	private AbacRule(final String role, final String right, final String aasId, final String smId, final String smElIdShortPath) {
		this.role = role;
		this.right = right;
		this.aasId = aasId;
		this.smId = smId;
		this.smElIdShortPath = smElIdShortPath;
	}

	public static AbacRule of(final String role, final String right, final String aasId, final String smId, final String smElIdShortPath) {
		return new AbacRule(role, right, aasId, smId, smElIdShortPath);
	}

	public String getRole() {
		return role;
	}

	public String getRight() {
		return right;
	}

	public String getAasId() {
		return aasId;
	}

	public String getSmId() {
		return smId;
	}

	public String getSmElIdShortPath() {
		return smElIdShortPath;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof AbacRule))
			return false;

		AbacRule abacRule = (AbacRule) o;

		return new EqualsBuilder()
				.append(getRole(), abacRule.getRole())
				.append(getRight(), abacRule.getRight())
				.append(getAasId(), abacRule.getAasId())
				.append(getSmId(), abacRule.getSmId())
				.append(getSmElIdShortPath(), abacRule.getSmElIdShortPath())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getRole())
				.append(getRight())
				.append(getAasId())
				.append(getSmId())
				.append(getSmElIdShortPath())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("AbacRule{")
				.append("role='").append(role).append('\'')
				.append(", right='").append(right).append('\'')
				.append(", aasId='").append(aasId).append('\'')
				.append(", smId='").append(smId).append('\'')
				.append(", smElIdShortPath='").append(smElIdShortPath).append('\'')
				.append('}')
				.toString();
	}
}
