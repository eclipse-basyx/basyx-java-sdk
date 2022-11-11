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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of {@link RbacRule} used in policy enforcement points or in policy information points.
 *
 * @author wege
 */
public class RbacRuleSet {
	private static final Logger logger = LoggerFactory.getLogger(RbacRuleSet.class);

	private final Set<RbacRule> rules;

	public RbacRuleSet() {
		this.rules = new HashSet<>();
	}

	public Set<RbacRule> getRules() {
		return Collections.unmodifiableSet(this.rules);
	}

	public boolean addRule(final RbacRule rbacRule) {
		return this.rules.add(rbacRule);
	}

	public boolean deleteRule(final RbacRule rbacRule) {
		return this.rules.remove(rbacRule);
	}

	@Override
	public String toString() {
		return new StringBuilder("RbacRuleSet{")
				.append("rules=").append(rules)
				.append('}')
				.toString();
	}

	public static RbacRuleSet fromFile(final String filePath) {
		if (filePath == null) {
			throw new IllegalArgumentException("filePath must not be null");
		}

		logger.info("loading rbac rules...");
		try (final InputStream inputStream = RbacRuleSet.class.getResourceAsStream(filePath)) {
			if (inputStream == null) {
				throw new FileNotFoundException("could not find " + filePath);
			}
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			final JsonReader jsonReader = new JsonReader(inputStreamReader);
			final RbacRuleDTO[] rbacRuleDTOs = new Gson().fromJson(jsonReader, RbacRuleDTO[].class);

			final RbacRule[] rbacRules = Arrays.stream(rbacRuleDTOs).map(RbacRuleSet::convertRbacRuleDTOToRbacRule).toArray(RbacRule[]::new);

			logger.info("Read rbac rules: {}", Arrays.toString(rbacRules));
			final RbacRuleSet rbacRuleSet = new RbacRuleSet();
			Arrays.stream(rbacRules).forEach(rbacRuleSet::addRule);
			return rbacRuleSet;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return new RbacRuleSet();
	}

	private static RbacRule convertRbacRuleDTOToRbacRule(final RbacRuleDTO dto) {
		final TargetInformation targetInformation = new TargetInformation();

		targetInformation.putAll(dto.getTargetInformation());

		return RbacRule.of(dto.getRole(), dto.getAction(), targetInformation);
	}
}
