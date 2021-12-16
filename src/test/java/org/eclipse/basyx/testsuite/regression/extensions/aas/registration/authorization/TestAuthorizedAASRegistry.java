/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.authorization;

import java.util.Collections;
import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
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
public class TestAuthorizedAASRegistry {

	@Mock
	private IAASRegistry registryMock;
	private AuthorizedAASRegistry testSubject;

	private SecurityContext _getSecurityContextWithAuthorities(String... authorities) {
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
		return _getSecurityContextWithAuthorities("SCOPE_" + AASRegistryScopes.READ_SCOPE);
	}

	private SecurityContext getSecurityContextWithWriteAuthority() {
		return _getSecurityContextWithAuthorities("SCOPE_" + AASRegistryScopes.WRITE_SCOPE);
	}

	@Before
	public void setUp() {
		testSubject = new AuthorizedAASRegistry(registryMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
		Mockito.verifyNoMoreInteractions(registryMock);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenRegisterAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getEmptySecurityContext());

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterAASDescriptor_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);

		Mockito.verify(registryMock).register(aasDescriptor);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterAASDescriptor_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodelDescriptor_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final IIdentifier aas = new ModelUrn("urn:test");
		final SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel");
		testSubject.register(aas, submodelDescriptor);

		Mockito.verify(registryMock).register(aas, submodelDescriptor);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodelDescriptor_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aas = new ModelUrn("urn:test");
		final SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel");
		testSubject.register(aas, submodelDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final IIdentifier aasId = new ModelUrn("urn:test");
		testSubject.delete(aasId);

		Mockito.verify(registryMock).delete(aasId);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aasId = new ModelUrn("urn:test");
		testSubject.delete(aasId);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodel_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		testSubject.delete(aasId, smId);

		Mockito.verify(registryMock).delete(aasId, smId);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodel_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		testSubject.delete(aasId, smId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final AASDescriptor expectedAASDescriptor = new AASDescriptor("test", aasId, "http://test.example/aas");
		Mockito.when(registryMock.lookupAAS(aasId)).thenReturn(expectedAASDescriptor);

		final AASDescriptor aasDescriptor = testSubject.lookupAAS(aasId);

		Assert.assertEquals(expectedAASDescriptor, aasDescriptor);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aasId = new ModelUrn("urn:test1");

		testSubject.lookupAAS(aasId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAll_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas"));
		Mockito.when(registryMock.lookupAll()).thenReturn(expectedAASDescriptorList);

		final List<AASDescriptor> aasDescriptorList = testSubject.lookupAll();

		Assert.assertEquals(expectedAASDescriptorList, aasDescriptorList);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAll_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.lookupAll();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodels_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final List<SubmodelDescriptor> expectedSubmodelDescriptorList = Collections.singletonList(new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel"));
		Mockito.when(registryMock.lookupSubmodels(aasId)).thenReturn(expectedSubmodelDescriptorList);

		final List<SubmodelDescriptor> submodelDescriptorList = testSubject.lookupSubmodels(aasId);

		Assert.assertEquals(expectedSubmodelDescriptorList, submodelDescriptorList);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodels_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aasId = new ModelUrn("urn:test1");

		testSubject.lookupSubmodels(aasId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodel_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		final SubmodelDescriptor expectedSubmodelDescriptor = new SubmodelDescriptor("test", smId, "http://test.example/submodel");
		Mockito.when(registryMock.lookupSubmodel(aasId, smId)).thenReturn(expectedSubmodelDescriptor);

		final SubmodelDescriptor submodelDescriptor = testSubject.lookupSubmodel(aasId, smId);

		Assert.assertEquals(expectedSubmodelDescriptor, submodelDescriptor);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodel_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");

		testSubject.lookupSubmodel(aasId, smId);
	}

}
