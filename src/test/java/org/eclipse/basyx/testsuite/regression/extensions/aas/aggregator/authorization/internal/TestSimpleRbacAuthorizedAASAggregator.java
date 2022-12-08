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
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.authorization.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AASAggregatorScopes;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.AuthorizedAASAggregator;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.internal.SimpleRbacAASAggregatorAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.KeycloakAuthenticationContextProvider;
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
public class TestSimpleRbacAuthorizedAASAggregator {
	@Mock
	private IAASAggregator apiMock;
	private AuthorizedAASAggregator<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";
	private final String partialReaderRole = "partialReader";

	private static final String SHELL_ID = "shell";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final String ASSET_ID = "asset";
	private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
	private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);
	private static final String SECOND_SHELL_ID = "second_shell";
	private static final Identifier SECOND_SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SECOND_SHELL_ID);
	private static final String SECOND_ASSET_ID = "second_asset";
	private static final Identifier SECOND_ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, SECOND_ASSET_ID);
	private static final Asset SECOND_SHELL_ASSET = new Asset(ASSET_ID, SECOND_ASSET_IDENTIFIER, AssetKind.INSTANCE);

	private static AssetAdministrationShell shell;
	private static AssetAdministrationShell secondShell;

	@Before
	public void setUp() {
		rbacRuleSet.addRule(new RbacRule(adminRole, AASAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, AASAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, AASAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(partialReaderRole, AASAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation(SHELL_IDENTIFIER.getId(), "*", "*")));
		testSubject = new AuthorizedAASAggregator<>(apiMock, new SimpleRbacAASAggregatorAuthorizer<>(new PredefinedSetRbacRuleChecker(rbacRuleSet), new KeycloakRoleAuthenticator()), new JWTAuthenticationContextProvider());
		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
		secondShell = new AssetAdministrationShell(SECOND_SHELL_ID, SECOND_SHELL_IDENTIFIER, SECOND_SHELL_ASSET);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	private AssetAdministrationShell invokeCreateAAS() {
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
		Mockito.verify(apiMock).createAAS(shell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenCreateAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		invokeCreateAAS();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenUpdateAAS_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		final AssetAdministrationShell shell = new AssetAdministrationShell();
		testSubject.updateAAS(shell);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		final AssetAdministrationShell shell = new AssetAdministrationShell();
		testSubject.updateAAS(shell);
		Mockito.verify(apiMock).updateAAS(shell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenUpdateAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final AssetAdministrationShell shell = new AssetAdministrationShell();
		testSubject.updateAAS(shell);
	}

	private IIdentifier invokeDeleteAAS() {
		testSubject.deleteAAS(SHELL_IDENTIFIER);
		return SHELL_IDENTIFIER;
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		final IIdentifier shellId = invokeDeleteAAS();
		Mockito.verify(apiMock).deleteAAS(shellId);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		invokeDeleteAAS();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final AssetAdministrationShell expectedShell = shell;
		Mockito.when(apiMock.getAAS(SHELL_IDENTIFIER)).thenReturn(expectedShell);

		final IAssetAdministrationShell shell = testSubject.getAAS(SHELL_IDENTIFIER);

		Assert.assertEquals(expectedShell, shell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.getAAS(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final Collection<IAssetAdministrationShell> expectedAASDescriptorList = Collections.singletonList(shell);
		Mockito.when(apiMock.getAASList()).thenReturn(Collections.singletonList(shell));
		Mockito.when(apiMock.getAAS(SHELL_IDENTIFIER)).thenReturn(shell);

		final Collection<IAssetAdministrationShell> shellList = testSubject.getAASList();

		Assert.assertEquals(expectedAASDescriptorList, shellList);
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetAASList_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final Collection<IAssetAdministrationShell> aasList = Collections.singletonList(shell);
		Mockito.when(apiMock.getAASList()).thenReturn(aasList);
		final Collection<IAssetAdministrationShell> returnedAASList = testSubject.getAASList();
		Assert.assertEquals(Collections.emptyList(), returnedAASList);
	}

	@Test
	public void givenPrincipalHasPartialReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(partialReaderRole);

		final Collection<IAssetAdministrationShell> expectedAASDescriptorList = Collections.singletonList(shell);
		Mockito.when(apiMock.getAASList()).thenReturn(new HashSet<>(Arrays.asList(shell, secondShell)));
		Mockito.when(apiMock.getAAS(SHELL_IDENTIFIER)).thenReturn(shell);

		final Collection<IAssetAdministrationShell> returnedShellList = testSubject.getAASList();

		Assert.assertEquals(expectedAASDescriptorList, returnedShellList);
	}
}
