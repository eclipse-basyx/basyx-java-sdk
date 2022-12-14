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
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.authorization.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
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
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization with the AuthorizedAASRegistry
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestSimpleRbacAuthorizedAASRegistry {
	@Mock
	private IAASRegistry apiMock;
	private AuthorizedAASRegistry<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private static final String SHELL_ID = "shell";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final String SUBMODEL_ID = "submodel";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String SECOND_SHELL_ID = "secondShell";
	private static final Identifier SECOND_SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SECOND_SHELL_ID);
	private static final String SECOND_SUBMODEL_ID = "secondSubmodel";
	private static final Identifier SECOND_SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SECOND_SUBMODEL_ID);

	private final String adminRole = "admin";
	private final String readerRole = "reader";
	private final String partialReaderRole = "partialReader";

	private AASDescriptor aasDescriptor;
	private SubmodelDescriptor smDescriptor;
	private AASDescriptor secondAasDescriptor;
	private SubmodelDescriptor secondSmDescriptor;

	@Before
	public void setUp() {
		rbacRuleSet.addRule(new RbacRule(adminRole, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, AASRegistryScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(partialReaderRole, AASRegistryScopes.READ_SCOPE, new BaSyxObjectTargetInformation(SHELL_IDENTIFIER.getId(), SUBMODEL_IDENTIFIER.getId(), "*")));
		testSubject = new AuthorizedAASRegistry<>(apiMock, new SimpleRbacAASRegistryAuthorizer<>(new PredefinedSetRbacRuleChecker(rbacRuleSet), new KeycloakRoleAuthenticator()), new JWTAuthenticationContextProvider());
		aasDescriptor = new AASDescriptor(SHELL_ID, SHELL_IDENTIFIER, "");
		smDescriptor = new SubmodelDescriptor(SUBMODEL_ID, SUBMODEL_IDENTIFIER, "");
		secondAasDescriptor = new AASDescriptor(SECOND_SHELL_ID, SECOND_SHELL_IDENTIFIER, "");
		secondSmDescriptor = new SubmodelDescriptor(SECOND_SUBMODEL_ID, SECOND_SUBMODEL_IDENTIFIER, "");
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenRegisterAAS_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterAASDescriptor_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.register(aasDescriptor);
		Mockito.verify(apiMock).register(aasDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterAASDescriptor_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodelDescriptor_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.register(SHELL_IDENTIFIER, smDescriptor);
		Mockito.verify(apiMock).register(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodelDescriptor_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.register(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.delete(SHELL_IDENTIFIER);
		Mockito.verify(apiMock).delete(SHELL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.delete(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
		Mockito.verify(apiMock).delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final AASDescriptor expectedAASDescriptor = aasDescriptor;
		Mockito.when(apiMock.lookupAAS(SHELL_IDENTIFIER)).thenReturn(expectedAASDescriptor);

		final AASDescriptor returnedAasDescriptor = testSubject.lookupAAS(SHELL_IDENTIFIER);
		Assert.assertEquals(expectedAASDescriptor, returnedAasDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.lookupAAS(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAll_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(aasDescriptor);
		Mockito.when(apiMock.lookupAll()).thenReturn(Collections.singletonList(aasDescriptor));
		Mockito.when(apiMock.lookupAAS(SHELL_IDENTIFIER)).thenReturn(aasDescriptor);

		final List<AASDescriptor> returnedAasDescriptorList = testSubject.lookupAll();
		Assert.assertEquals(expectedAASDescriptorList, returnedAasDescriptorList);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAll_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.lookupAll();
	}

	@Test
	public void givenPrincipalHasPartialReadAuthority_whenLookupAll_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(partialReaderRole);
		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(aasDescriptor);
		Mockito.when(apiMock.lookupAll()).thenReturn(Arrays.asList(aasDescriptor, secondAasDescriptor));
		Mockito.when(apiMock.lookupAAS(SHELL_IDENTIFIER)).thenReturn(aasDescriptor);

		final List<AASDescriptor> returnedAasDescriptorList = testSubject.lookupAll();
		Assert.assertEquals(expectedAASDescriptorList, returnedAasDescriptorList);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodels_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final List<SubmodelDescriptor> expectedSubmodelDescriptorList = Collections.singletonList(smDescriptor);
		Mockito.when(apiMock.lookupSubmodels(SHELL_IDENTIFIER)).thenReturn(Collections.singletonList(smDescriptor));
		Mockito.when(apiMock.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);

		final List<SubmodelDescriptor> returnedSubmodelDescriptorList = testSubject.lookupSubmodels(SHELL_IDENTIFIER);
		Assert.assertEquals(expectedSubmodelDescriptorList, returnedSubmodelDescriptorList);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodels_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.lookupSubmodels(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasPartialReadAuthority_whenLookupSubmodels_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(partialReaderRole);
		final List<SubmodelDescriptor> expectedSubmodelDescriptorList = Collections.singletonList(smDescriptor);
		Mockito.when(apiMock.lookupSubmodels(SHELL_IDENTIFIER)).thenReturn(Arrays.asList(smDescriptor, secondSmDescriptor));
		Mockito.when(apiMock.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);

		final List<SubmodelDescriptor> returnedSubmodelDescriptorList = testSubject.lookupSubmodels(SHELL_IDENTIFIER);
		Assert.assertEquals(expectedSubmodelDescriptorList, returnedSubmodelDescriptorList);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final SubmodelDescriptor expectedSubmodelDescriptor = smDescriptor;
		Mockito.when(apiMock.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER)).thenReturn(expectedSubmodelDescriptor);

		final SubmodelDescriptor returnedSubmodelDescriptor = testSubject.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
		Assert.assertEquals(expectedSubmodelDescriptor, returnedSubmodelDescriptor);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

}
