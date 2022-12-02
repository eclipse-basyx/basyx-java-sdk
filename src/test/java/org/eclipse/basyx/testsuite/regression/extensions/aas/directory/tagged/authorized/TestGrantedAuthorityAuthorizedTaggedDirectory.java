/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.directory.tagged.authorized;

import static org.junit.Assert.assertEquals;
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
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.AuthorizationContextProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests authorization implementation for the AASTaggedDirectory
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestGrantedAuthorityAuthorizedTaggedDirectory {
	@Mock
	private IAASTaggedDirectory apiMock;
	private AuthorizedTaggedDirectory<?> testSubject;
	private AuthorizationContextProvider securityContextProvider;

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
		testSubject = new AuthorizedTaggedDirectory<>(apiMock);
		securityContextProvider = new AuthorizationContextProvider(AuthorizedAASRegistry.READ_AUTHORITY, AuthorizedAASRegistry.WRITE_AUTHORITY, null);

		shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
		aasDescriptor = new TaggedAASDescriptor(shell, "");
		aasDescriptor.addTag(AAS_TAG);
		submodel = new Submodel(SUBMODEL_ID, SUBMODEL_IDENTIFIER);
		smDescriptor = new TaggedSubmodelDescriptor(submodel, "");
		smDescriptor.addTag(SM_TAG);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegister_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.register(aasDescriptor);
		Mockito.verify(apiMock).register(aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenRegister_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.register(aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegister_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupTag_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.lookupAAS(new ModelUrn(SHELL_ID))).thenReturn(aasDescriptor);
		Mockito.when(apiMock.lookupTag(AAS_TAG)).thenReturn(Collections.singleton(aasDescriptor));

		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTag(AAS_TAG);
		Mockito.verify(apiMock).lookupTag(AAS_TAG);
		assertTrue(returnedAASDescriptors.contains(aasDescriptor));
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenLookupTag_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupTag(AAS_TAG);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupTag_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.lookupTag(AAS_TAG);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.lookupAAS(new ModelUrn(SHELL_ID))).thenReturn(aasDescriptor);
		Mockito.when(apiMock.lookupTag(AAS_TAG)).thenReturn(Collections.singleton(aasDescriptor));
		Mockito.when(apiMock.lookupTags(Collections.singleton(AAS_TAG))).thenReturn(Collections.singleton(aasDescriptor));

		final Set<TaggedAASDescriptor> returnedAASDescriptors = testSubject.lookupTags(Collections.singleton(AAS_TAG));
		assertTrue(returnedAASDescriptors.contains(aasDescriptor));
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenLookupTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupTags(Collections.singleton(AAS_TAG));
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.lookupTags(Collections.singleton(AAS_TAG));
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
		Mockito.verify(apiMock).registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenRegisterSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.registerSubmodel(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodelTag_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTag(SM_TAG);
		Mockito.verify(apiMock).lookupSubmodelTag(SM_TAG);
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenLookupSubmodelTag_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupSubmodelTag(SM_TAG);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupSubmodelTag_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.lookupSubmodelTag(SM_TAG);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodelTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupSubmodelTags(Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
		Mockito.verify(apiMock).lookupSubmodelTags(Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenLookupSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.lookupSubmodelTags(Collections.singleton(SM_TAG));
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupBothAasAndSubmodelTags_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.lookupSubmodel(new ModelUrn("*"), SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);
		Mockito.when(apiMock.lookupSubmodelTag(SM_TAG)).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupSubmodelTags(Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));
		Mockito.when(apiMock.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG))).thenReturn(Collections.singleton(smDescriptor));

		final Set<TaggedSubmodelDescriptor> returnedSmDescriptors = testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		Mockito.verify(apiMock).lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
		assertTrue(returnedSmDescriptors.contains(smDescriptor));
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenLookupBothAasAndSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenLookupBothAasAndSubmodelTags_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		testSubject.lookupBothAasAndSubmodelTags(Collections.singleton(AAS_TAG), Collections.singleton(SM_TAG));
	}
}
