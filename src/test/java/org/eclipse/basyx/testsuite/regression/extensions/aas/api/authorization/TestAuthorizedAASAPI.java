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
package org.eclipse.basyx.testsuite.regression.extensions.aas.api.authorization;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.extensions.aas.api.authorization.AASAPIScopes;
import org.eclipse.basyx.extensions.aas.api.authorization.AuthorizedAASAPI;
import org.eclipse.basyx.extensions.aas.api.authorization.SimpleAbacAASAPIAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.AbacRule;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetAbacRuleChecker;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.KeycloakAuthenticationContextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests authorization with the AuthorizedAASAPI
 *
 * @author espen, wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedAASAPI {
	@Mock
	private IAASAPI apiMock;
	private AuthorizedAASAPI<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private AbacRuleSet abacRuleSet = new AbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";

	private static final String SHELL_ID = "shell_one";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final String SUBMODEL_ID = "submodel_1";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String ASSET_ID = "asset_one";
	private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
	private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);

	private static AssetAdministrationShell shell;
	private static Submodel submodel;

	@Before
	public void setUp() {
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				AASAPIScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				AASAPIScopes.WRITE_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				readerRole,
				AASAPIScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		testSubject = new AuthorizedAASAPI<>(
				apiMock,
				new SimpleAbacAASAPIAuthorizer<>(
						new PredefinedSetAbacRuleChecker(abacRuleSet),
						new KeycloakRoleAuthenticator()
				),
				new JWTAuthenticationContextProvider()
		);
		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getAAS()).thenReturn(shell);

		final IAssetAdministrationShell returnedShell = testSubject.getAAS();
		assertEquals(shell, returnedShell);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetAAS_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.getAAS();
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.getAAS();
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		final IReference smReference2Add = submodel.getReference();
		testSubject.addSubmodel(smReference2Add);
		Mockito.verify(apiMock).addSubmodel(smReference2Add);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.addSubmodel(submodel.getReference());
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.addSubmodel(submodel.getReference());
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRemoveSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		testSubject.removeSubmodel(SUBMODEL_ID);
		Mockito.verify(apiMock).removeSubmodel(SUBMODEL_ID);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenRemoveSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.removeSubmodel(SUBMODEL_ID);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenRemoveSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.removeSubmodel(SUBMODEL_ID);
	}
}
