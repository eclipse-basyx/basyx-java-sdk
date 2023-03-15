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
package org.eclipse.basyx.testsuite.regression.extensions.shared.internal;

import java.io.IOException;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSetDeserializer;
import org.eclipse.basyx.extensions.shared.authorization.internal.TagTargetInformation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests rbac rule set deserialization.
 *
 * @author wege
 */
public class TestRbacRuleSetDeserializer {
	@Test
	public void test() throws IOException {
		final RbacRuleSetDeserializer rbacRuleSetDeserializer = new RbacRuleSetDeserializer();
		final RbacRuleSet rbacRuleSet = rbacRuleSetDeserializer.fromFile("/authorization/internal/rbac_rules.json");
		Assert.assertEquals(17, rbacRuleSet.getRules().size());
		Assert.assertTrue(rbacRuleSet.getRules().contains(new RbacRule("reader", "urn:org.eclipse.basyx:scope:aas-registry:read", new BaSyxObjectTargetInformation("*", "*", null, "*"))));
		Assert.assertTrue(rbacRuleSet.getRules().contains(new RbacRule("reader", "urn:org.eclipse.basyx:scope:aas-registry:read", new TagTargetInformation("tag"))));
		Assert.assertTrue(rbacRuleSet.getRules().contains(new RbacRule("reader", "urn:org.eclipse.basyx:scope:aas-registry:read", new BaSyxObjectTargetInformation("*", "*", "example-semantic-id", "*"))));
	}

	final String[] multipleActions = new String[]{
		"urn:org.eclipse.basyx:scope:aas-aggregator:read",
		"urn:org.eclipse.basyx:scope:aas-aggregator:write",
		"urn:org.eclipse.basyx:scope:aas-api:read",
		"urn:org.eclipse.basyx:scope:aas-api:write",
		"urn:org.eclipse.basyx:scope:sm-aggregator:read",
		"urn:org.eclipse.basyx:scope:sm-api:read",
		"urn:org.eclipse.basyx:scope:aas-registry:read"
	};

	@Test
	public void multipleActions() throws IOException {
		final RbacRuleSetDeserializer rbacRuleSetDeserializer = new RbacRuleSetDeserializer();
		final RbacRuleSet rbacRuleSet = rbacRuleSetDeserializer.fromFile("/authorization/internal/rbac_rules_multiple_actions.json");
		Assert.assertEquals(multipleActions.length, rbacRuleSet.getRules().size());
		for (final String action : multipleActions) {
			Assert.assertTrue(rbacRuleSet.getRules().contains(new RbacRule("admin", action, new BaSyxObjectTargetInformation("*", "*", "*", "*"))));
		}
	}
}
