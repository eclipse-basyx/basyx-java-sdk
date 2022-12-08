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

import java.util.Collections;
import java.util.List;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.internal.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization with the AuthorizedAASRegistry
 *
 * @author pneuschwander
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestGrantedAuthorityAuthorizedAASRegistry {
	@Mock
	private IAASRegistry apiMock;
	private AuthorizedAASRegistry<?> testSubject;

	private static final String SHELL_ID = "shell";
	private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
	private static final String SUBMODEL_ID = "submodel";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private AASDescriptor aasDescriptor;
	private SubmodelDescriptor smDescriptor;

	private SecurityContext _getSecurityContextWithAuthorities(final String... authorities) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final Authentication authentication = new TestingAuthenticationToken(null, null, authorities);
		context.setAuthentication(authentication);
		return context;
	}

	private SecurityContext getEmptySecurityContext() {
		return SecurityContextHolder.createEmptyContext();
	}

	private SecurityContext getSecurityContextWithoutAuthorities() {
		return _getSecurityContextWithAuthorities();
	}

	private SecurityContext getSecurityContextWithReadAuthority() {
		return _getSecurityContextWithAuthorities(AuthorizedAASRegistry.READ_AUTHORITY);
	}

	private SecurityContext getSecurityContextWithWriteAuthority() {
		return _getSecurityContextWithAuthorities(AuthorizedAASRegistry.WRITE_AUTHORITY);
	}

	@Before
	public void setUp() {
		testSubject = new AuthorizedAASRegistry<>(apiMock);
		aasDescriptor = new AASDescriptor(SHELL_ID, SHELL_IDENTIFIER, "");
		smDescriptor = new SubmodelDescriptor(SUBMODEL_ID, SUBMODEL_IDENTIFIER, "");
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenRegisterAAS_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getEmptySecurityContext());

		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterAASDescriptor_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		testSubject.register(aasDescriptor);
		Mockito.verify(apiMock).register(aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterAASDescriptor_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodelDescriptor_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		testSubject.register(SHELL_IDENTIFIER, smDescriptor);
		Mockito.verify(apiMock).register(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodelDescriptor_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.register(SHELL_IDENTIFIER, smDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		testSubject.delete(SHELL_IDENTIFIER);
		Mockito.verify(apiMock).delete(SHELL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.delete(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodel_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		testSubject.delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
		Mockito.verify(apiMock).delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodel_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.delete(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		final AASDescriptor expectedAASDescriptor = aasDescriptor;
		Mockito.when(apiMock.lookupAAS(SHELL_IDENTIFIER)).thenReturn(expectedAASDescriptor);

		final AASDescriptor aasDescriptor = testSubject.lookupAAS(SHELL_IDENTIFIER);
		Assert.assertEquals(expectedAASDescriptor, aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAAS_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.lookupAAS(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAll_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(aasDescriptor);
		Mockito.when(apiMock.lookupAll()).thenReturn(expectedAASDescriptorList);
		Mockito.when(apiMock.lookupAAS(SHELL_IDENTIFIER)).thenReturn(aasDescriptor);

		final List<AASDescriptor> returnedAasDescriptorList = testSubject.lookupAll();
		Assert.assertEquals(expectedAASDescriptorList, returnedAasDescriptorList);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAll_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.lookupAll();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodels_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		final List<SubmodelDescriptor> expectedSubmodelDescriptorList = Collections.singletonList(smDescriptor);
		Mockito.when(apiMock.lookupSubmodels(SHELL_IDENTIFIER)).thenReturn(expectedSubmodelDescriptorList);
		Mockito.when(apiMock.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER)).thenReturn(smDescriptor);

		final List<SubmodelDescriptor> returnedSubmodelDescriptorList = testSubject.lookupSubmodels(SHELL_IDENTIFIER);
		Assert.assertEquals(expectedSubmodelDescriptorList, returnedSubmodelDescriptorList);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodels_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.lookupSubmodels(SHELL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodel_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());
		final SubmodelDescriptor expectedSubmodelDescriptor = smDescriptor;
		Mockito.when(apiMock.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER)).thenReturn(expectedSubmodelDescriptor);

		final SubmodelDescriptor submodelDescriptor = testSubject.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
		Assert.assertEquals(expectedSubmodelDescriptor, submodelDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodel_thenThrowNotAuthorized() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.lookupSubmodel(SHELL_IDENTIFIER, SUBMODEL_IDENTIFIER);
	}

}