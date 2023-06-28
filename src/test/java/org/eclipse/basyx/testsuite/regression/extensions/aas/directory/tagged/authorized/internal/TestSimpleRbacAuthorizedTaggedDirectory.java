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
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.authorized.internal;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal.AuthorizedTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.internal.SimpleRbacTaggedDirectoryAuthorizer;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.TagTargetInformation;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.KeycloakAuthenticationContextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization implementation for the AASTaggedDirectory
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestSimpleRbacAuthorizedTaggedDirectory {
	@Mock
	private IAASTaggedDirectory apiMock;
	private AuthorizedTaggedDirectory<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";

	private static final String SHELL_ID = "shell_one";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final String SUBMODEL_ID = "submodel_1";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String ASSET_ID = "asset_one";
	private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
	private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);
	private static final String AAS_TAG = "AAS_TAG";
	private static final String SM_TAG = "SM_TAG";

	private static AssetAdministrationShell shell;
	private static TaggedAASDescriptor aasDescriptor;
	private static Submodel submodel;
	private static TaggedSubmodelDescriptor smDescriptor;

	@Before
	public void setUp() {
		rbacRuleSet.addRule(new RbacRule(adminRole, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, AASRegistryScopes.READ_SCOPE, new TagTargetInformation("*")));
		testSubject = new AuthorizedTaggedDirectory<>(apiMock, new SimpleRbacTaggedDirectoryAuthorizer<>(new PredefinedSetRbacRuleChecker(rbacRuleSet), new KeycloakRoleAuthenticator()), new JWTAuthenticationContextProvider());

		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
		aasDescriptor = new TaggedAASDescriptor(shell, "");
		aasDescriptor.addTag(AAS_TAG);
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);
		smDescriptor = new TaggedSubmodelDescriptor(submodel, "");
		smDescriptor.addTag(SM_TAG);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegister_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.register(aasDescriptor);
		Mockito.verify(apiMock).register(aasDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenRegister_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.register(aasDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegister_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupTag_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.lookupAAS(new ModelUrn(SHELL_ID))).thenReturn(aasDescriptor);
		Mockito.when(apiMock.lookupTag(AAS_TAG)).thenReturn(Collections.singleton(aasDescriptor));

		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTag(AAS_TAG);
		Mockito.verify(apiMock).lookupTag(AAS_TAG);
		assertTrue(returnedAASDescriptors.contains(aasDescriptor));
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenLookupTag_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupTag(AAS_TAG);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupTag_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.lookupTag(AAS_TAG);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.lookupAAS(new ModelUrn(SHELL_ID))).thenReturn(aasDescriptor);
		Mockito.when(apiMock.lookupTag(AAS_TAG)).thenReturn(Collections.singleton(aasDescriptor));
		Mockito.when(apiMock.lookupTags(Collections.singleton(AAS_TAG))).thenReturn(Collections.singleton(aasDescriptor));

		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTags(Collections.singleton(AAS_TAG));
		assertTrue(returnedAASDescriptors.contains(aasDescriptor));
	}

	@Test
	public void givenSecurityContextIsEmpty_whenLookupTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTags(Collections.singleton(AAS_TAG));
		assertTrue(returnedAASDescriptors.isEmpty());
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenLookupTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTags(Collections.singleton(AAS_TAG));
		assertTrue(returnedAASDescriptors.isEmpty());
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
		Mockito.verify(apiMock).registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenRegisterSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodelTag_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTag(SM_TAG);
		Mockito.verify(apiMock).lookupSubmodelTag(SM_TAG);
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenLookupSubmodelTag_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupSubmodelTag(SM_TAG);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupSubmodelTag_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		testSubject.lookupSubmodelTag(SM_TAG);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodelTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupSubmodelTags(Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
		Mockito.verify(apiMock).lookupSubmodelTags(Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test
	public void givenSecurityContextIsEmpty_whenLookupSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.isEmpty());
	}

	@Test
	public void givenPrincipalIsMissingWriteAuthority_whenLookupSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.isEmpty());
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupBothAasAndSubmodelTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupSubmodelTags(Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		Mockito.verify(apiMock).lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test
	public void givenSecurityContextIsEmpty_whenLookupBothAasAndSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.isEmpty());
	}

	@Test
	public void givenPrincipalIsMissingWriteAuthority_whenLookupBothAasAndSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.isEmpty());
	}
}
