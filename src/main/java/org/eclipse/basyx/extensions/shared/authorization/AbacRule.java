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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A single attribute based access control rule consisting of
 * role x right x (aas id, submodel id, submodel element id).
 *
 * @author wege
 */
public class AbacRule {
	public static class Right {
		private final String right;

		private Right(String right) {
			this.right = right;
		}

		public static Right of(String right) {
			return new Right(right);
		}
	}

	private final String role;
	private final String right;
	private final String aasId;
	private final String smId;
	private final String smElId;

	private AbacRule(String role, String right, String aasId, String smId, String smElId) {
		this.role = role;
		this.right = right;
		this.aasId = aasId;
		this.smId = smId;
		this.smElId = smElId;
	}

	public static AbacRule of(String role, String right, String aasId, String smId, String smElId) {
		return new AbacRule(role, right, aasId, smId, smElId);
	}

	public static AbacRule of(String role, Right right, String aasId, String smId, String smElId) {
		return new AbacRule(role, right.right, aasId, smId, smElId);
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

	public String getSmElId() {
		return smElId;
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
				.append(getSmElId(), abacRule.getSmElId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(getRole())
				.append(getRight())
				.append(getAasId())
				.append(getSmId())
				.append(getSmElId())
				.toHashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbacRule{");
		sb.append("role='").append(role).append('\'');
		sb.append(", right='").append(right).append('\'');
		sb.append(", aasId='").append(aasId).append('\'');
		sb.append(", smId='").append(smId).append('\'');
		sb.append(", smElId='").append(smElId).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
