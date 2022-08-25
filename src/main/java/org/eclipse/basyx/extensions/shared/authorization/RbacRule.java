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

import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single role based access control rule consisting of
 * role x action x (aas id, submodel id, submodel element id).
 *
 * @author wege
 */
public class RbacRule {
	private final String role;
	private final String action;
	private final String aasId;
	private final String smId;
	private final String smElIdShortPath;

	private RbacRule(final String role, final String action, final String aasId, final String smId, final String smElIdShortPath) {
		if (Objects.isNull(role)) {
			throw new IllegalArgumentException("role must not be null");
		}
		if (Objects.isNull(action)) {
			throw new IllegalArgumentException("action must not be null");
		}
		this.role = role;
		this.action = action;
		this.aasId = aasId;
		this.smId = smId;
		this.smElIdShortPath = smElIdShortPath;
	}

	public static RbacRule of(final String role, final String action, final String aasId, final String smId, final String smElIdShortPath) {
		return new RbacRule(role, action, aasId, smId, smElIdShortPath);
	}

	public String getRole() {
		return role;
	}

	public String getAction() {
		return action;
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

		if (!(o instanceof RbacRule))
			return false;

		RbacRule rbacRule = (RbacRule) o;

		return new EqualsBuilder()
				.append(getRole(), rbacRule.getRole())
				.append(getAction(), rbacRule.getAction())
				.append(getAasId(), rbacRule.getAasId())
				.append(getSmId(), rbacRule.getSmId())
				.append(getSmElIdShortPath(), rbacRule.getSmElIdShortPath())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getRole())
				.append(getAction())
				.append(getAasId())
				.append(getSmId())
				.append(getSmElIdShortPath())
				.toHashCode();
	}

	@Override
	public String toString() {
		return new StringBuilder("RbacRule{")
				.append("role='").append(role).append('\'')
				.append(", action='").append(action).append('\'')
				.append(", aasId='").append(aasId).append('\'')
				.append(", smId='").append(smId).append('\'')
				.append(", smElIdShortPath='").append(smElIdShortPath).append('\'')
				.append('}')
				.toString();
	}
}
