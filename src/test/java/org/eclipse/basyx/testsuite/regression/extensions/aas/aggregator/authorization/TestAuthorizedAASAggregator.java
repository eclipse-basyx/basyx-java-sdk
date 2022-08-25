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
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.authorization;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AASAggregatorScopes;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AuthorizedAASAggregator;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.SimpleRbacAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.KeycloakAuthenticationContextProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests authorization with the AuthorizedAASAggregator
 *
 * @author jungjan, fried, fischer, wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedAASAggregator {
	@Mock
	private IAASAggregator aggregatorMock;
	private AuthorizedAASAggregator<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";

	@Before
	public void setUp() {
		rbacRuleSet.addRule(RbacRule.of(
				adminRole,
				AASAggregatorScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		rbacRuleSet.addRule(RbacRule.of(
				adminRole,
				AASAggregatorScopes.WRITE_SCOPE,
				"*",
				"*",
				"*"
		));
		rbacRuleSet.addRule(RbacRule.of(
				readerRole,
				AASAggregatorScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		testSubject = new AuthorizedAASAggregator<>(
				aggregatorMock,
				new SimpleRbacAASAggregatorAuthorizer<>(
						new PredefinedSetRbacRuleChecker(rbacRuleSet),
						new KeycloakRoleAuthenticator()
				),
				new JWTAuthenticationContextProvider()
		);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	private AssetAdministrationShell invokeCreateAAS() {
		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		testSubject.createAAS(shell);
		return shell;
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenCreateAAS_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		invokeCreateAAS();
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		final AssetAdministrationShell shell = invokeCreateAAS();
		Mockito.verify(aggregatorMock).createAAS(shell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenCreateAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		invokeCreateAAS();
	}

	private IIdentifier invokeDeleteAAS() {
		final IIdentifier shellId = new ModelUrn("urn:test");
		testSubject.deleteAAS(shellId);
		return shellId;
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		final IIdentifier shellId = invokeDeleteAAS();
		Mockito.verify(aggregatorMock).deleteAAS(shellId);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		invokeDeleteAAS();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell expectedShell = new AssetAdministrationShell("test", shellId, new Asset());
		Mockito.when(aggregatorMock.getAAS(shellId)).thenReturn(expectedShell);

		final IAssetAdministrationShell shell = testSubject.getAAS(shellId);

		Assert.assertEquals(expectedShell, shell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier shellId = new ModelUrn("urn:test1");

		testSubject.getAAS(shellId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final IAssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		final Collection<IAssetAdministrationShell> expectedAASDescriptorList = Collections.singletonList(shell);
		Mockito.when(aggregatorMock.getAASList()).thenReturn(expectedAASDescriptorList);
		Mockito.when(aggregatorMock.getAAS(shellId)).thenReturn(shell);

		final Collection<IAssetAdministrationShell> shellList = testSubject.getAASList();

		Assert.assertEquals(expectedAASDescriptorList, shellList);
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetAASList_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		final Collection<IAssetAdministrationShell> aasList = Collections.singletonList(shell);
		Mockito.when(aggregatorMock.getAASList()).thenReturn(aasList);
		//Mockito.when(aggregatorMock.getAAS(shellId)).thenReturn(shell);
		final Collection<IAssetAdministrationShell> returnedAASList = testSubject.getAASList();
		Assert.assertEquals(Collections.emptyList(), returnedAASList);
	}
}
