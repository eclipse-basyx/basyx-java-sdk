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
import org.eclipse.basyx.extensions.shared.authorization.AbacRule;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetAbacRuleChecker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests abac rule checking with PredefinedSetAbacRuleChecker
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestPredefinedSetAbacRuleChecker {
	private AbacRuleSet abacRuleSet = new AbacRuleSet();

	private final String adminRole = "admin";
	private final String normalRole = "normal";
	
	private final String someId = "someId";
	private final String otherId = "other";
	private final String readAction = "read";
	private final String writeAction = "write";

	private final String any = "*";

	@Test
	public void givenAbacRuleSetIsEmpty_whenCheckAbacRuleIsSatisfiedWithRolesIsNullAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(new AbacRuleSet());

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				null,
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetIsEmpty_whenCheckAbacRuleIsSatisfiedWithRolesIsEmptyAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.emptyList(),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRhAdminRoleAndActionIsEmptyString_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.emptyList(),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsEmptyStringAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsNullAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				null,
				null,
				null,
				null
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsEmptyStringAndAasIdIsNullAndSmIdIsNullAndSmElIdShortPathIsNull_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						"",
						null,
						null,
						null
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsNormalRoleAndActionIsReadAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(normalRole),
				readAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						readAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				someId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsSomeIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						someId,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				someId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				someId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsSomeIdAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsSomeId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						someId,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				someId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsSomeId_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				someId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathAndSomeId_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAny_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsOtherId_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				otherId
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAny_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherId_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyBar_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/bar"
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertFalse(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyFoobar_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/foobar"
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsFooAnyFoobarAndRuleWithRoleIsNormalRoleAndActionIsReadAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnTrue() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						"foo/" + any + "/foobar"
				)
		);

		abacRuleSet.addRule(
				AbacRule.of(
						normalRole,
						readAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertTrue(result);
	}

	@Test
	public void givenAbacRuleSetContainsRuleWithRoleIsAdminRoleAndActionIsWriteAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsSomeIdAndRuleWithRoleIsNormalRoleAndActionIsReadAndAasIdIsAnyAndSmIdIsAnyAndSmElIdShortPathIsAny_whenCheckAbacRuleIsSatisfiedWithRoleIsAdminRoleAndWriteActionAndAasIdIsOtherIdAndSmIdIsOtherIdAndSmElIdShortPathIsFooOtherIdFoobar_thenReturnFalse() {
		final AbacRuleSet abacRuleSet = new AbacRuleSet();

		abacRuleSet.addRule(
				AbacRule.of(
						adminRole,
						writeAction,
						any,
						any,
						someId
				)
		);

		abacRuleSet.addRule(
				AbacRule.of(
						normalRole,
						readAction,
						any,
						any,
						any
				)
		);

		final PredefinedSetAbacRuleChecker testSubject = new PredefinedSetAbacRuleChecker(abacRuleSet);

		final boolean result = testSubject.checkAbacRuleIsSatisfied(
				Collections.singletonList(adminRole),
				writeAction,
				otherId,
				otherId,
				"foo/other/foobar"
		);

		Assert.assertFalse(result);
	}
}
