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

import java.util.Collections;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests rbac rule checking with {@link PredefinedSetRbacRuleChecker)
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestPredefinedSetRbacRuleChecker {
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String normalRole = "normal";

	private final String someId = "someId";
	private final String otherId = "other";
	private final String readAction = "read";
	private final String writeAction = "write";

	private final String any = "*";

	@Test
	public void givenNothing_whenCheckForRuleNull$Null$NullNullNull_thenReturnFalse() {
		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(new RbacRuleSet());

		final boolean result = testSubject.checkRbacRuleIsSatisfied(null, null, new BaSyxObjectTargetInformation(null, null, null, null));

		Assert.assertFalse(result);
	}

	@Test
	public void givenNothing_whenCheckForRuleEmpty$Null$NullNullNull_thenReturnFalse() {
		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.emptyList(), null, new BaSyxObjectTargetInformation(null, null, null, null));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Empty$NullNullNull_whenCheckForRuleEmpty$Null$NullNullNull_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, "", new BaSyxObjectTargetInformation(null, null, null, null)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.emptyList(), null, new BaSyxObjectTargetInformation(null, null, null, null));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Empty$NullNullNull_whenCheckForRuleAdmin$Null$NullNullNull_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, "", new BaSyxObjectTargetInformation(null, null, null, null)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), null, new BaSyxObjectTargetInformation(null, null, null, null));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Empty$NullNullNull_whenCheckForRuleAdmin$Read$SomSomeSome_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, "", new BaSyxObjectTargetInformation(null, null, null, null)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Read$SomeSomeSome_whenCheckForRuleAdmin$Read$SomeSomeSome_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Read$SomeSomeSome_whenCheckForRuleNormal$Read$SomeSomeSome_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(normalRole), readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Read$SomeSomeSome_whenCheckForRuleAdmin$Write$SomeSomeSome_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, readAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$SomeSomeSome_whenCheckForRuleAdmin$Write$OtherSomeSome_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(someId, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, someId, someId, someId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnySomeSome_whenCheckForRuleAdmin$Write$OtherSomeSome_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, someId, someId, someId));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnySomeSome_whenCheckForRuleAdmin$Write$OtherOtherSome_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, someId, someId, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, someId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnySome_whenCheckForRuleAdmin$Write$OtherOtherSome_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, someId));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnySome_whenCheckForRuleAdmin$Write$OtherOtherOther_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, someId)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, otherId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyAny_whenCheckForRuleAdmin$Write$OtherOtherOther_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, any)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, otherId));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyFooAny_whenCheckForRuleAdmin$Write$OtherOtherOther_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, "foo/" + any)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, otherId));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyFooAny_whenCheckForRuleAdmin$Write$OtherOtherFooOther_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, "foo/" + any)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, "foo/other"));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyFooAnyBar_whenCheckForRuleAdmin$Write$OtherOtherFooOtherFoobar_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, "foo/" + any + "/bar")));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, "foo/other/foobar"));

		Assert.assertFalse(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyFooAnyFoobar_whenCheckForRuleAdmin$Write$OtherOtherFooOtherFoobar_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, "foo/" + any + "/foobar")));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, "foo/other/foobar"));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnyFooAnyFoobar_and_RuleNormal$Read$AnyAnyAny_whenCheckForRuleAdmin$Write$OtherOtherFooOtherFoobar_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, "foo/" + any + "/foobar")));

		rbacRuleSet.addRule(new RbacRule(normalRole, readAction, new BaSyxObjectTargetInformation(any, any, any, any)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, "foo/other/foobar"));

		Assert.assertTrue(result);
	}

	@Test
	public void givenRuleAdmin$Write$AnyAnySomeId_and_RuleNormal$Read$AnyAnyAny_whenCheckForRuleAdmin$Write$OtherOtherFooOtherFoobar$_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(new RbacRule(adminRole, writeAction, new BaSyxObjectTargetInformation(any, any, any, someId)));

		rbacRuleSet.addRule(new RbacRule(normalRole, readAction, new BaSyxObjectTargetInformation(any, any, any, any)));

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(Collections.singletonList(adminRole), writeAction, new BaSyxObjectTargetInformation(otherId, otherId, otherId, "foo/other/foobar"));

		Assert.assertFalse(result);
	}
}
