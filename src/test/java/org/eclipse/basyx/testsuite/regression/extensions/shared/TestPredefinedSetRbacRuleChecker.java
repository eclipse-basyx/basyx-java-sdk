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
package org.eclipse.basyx.testsuite.regression.extensions.shared;

import java.util.Collections;
import org.eclipse.basyx.extensions.shared.authorization.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetRbacRuleChecker;
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
	public void givenRbacRuleSetIsEmpty_whenCheckRbacRuleIsSatisfiedWithRolesIsNullAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(new RbacRuleSet());

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				null,
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetIsEmpty_whenCheckRbacRuleIsSatisfiedWithRolesIsEmptyAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.emptyList(),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRhAdminRoleAndActionIsEmptyString_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.emptyList(),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsEmptyStringAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsEmptyStringAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsNormalRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(normalRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				someId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						someId,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathAndSomeId_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAny_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAny_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherId_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyBar_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/bar"
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyFoobar_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/foobar"
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyFoobarAndRuleWithRoleIsNormalRoleAndActionIsReadAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnTrue() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/foobar"
				)
		);

		rbacRuleSet.addRule(
				RbacRule.of(
						normalRole,
						readAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenRbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsSomeIdAndRuleWithRoleIsNormalRoleAndActionIsReadAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckRbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnFalse() {
		final RbacRuleSet rbacRuleSet = new RbacRuleSet();

		rbacRuleSet.addRule(
				RbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		rbacRuleSet.addRule(
				RbacRule.of(
						normalRole,
						readAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetRbacRuleChecker testSubject = new PredefinedSetRbacRuleChecker(rbacRuleSet);

		final boolean result = testSubject.checkRbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertFalse(result);
	}
}
