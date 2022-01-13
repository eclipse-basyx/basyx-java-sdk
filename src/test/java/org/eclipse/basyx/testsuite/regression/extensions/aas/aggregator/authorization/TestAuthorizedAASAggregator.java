/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.authorization;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AASAggregatorScopes;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AuthorizedAASAggregator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization with the AuthorizedAASAggregator
 * 
 * @author jungjan, fried, fischer
 */
public class TestAuthorizedAASAggregator {

	@Mock
	private IAASAggregator aggregatorMock;
	private AuthorizedAASAggregator testSubject;

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
		return _getSecurityContextWithAuthorities("SCOPE_" + AASAggregatorScopes.READ_SCOPE);
	}

	private SecurityContext getSecurityContextWithWriteAuthority() {
		return _getSecurityContextWithAuthorities("SCOPE_" + AASAggregatorScopes.WRITE_SCOPE);
	}

	@Before
	public void setUp() {
		testSubject = new AuthorizedAASAggregator(aggregatorMock);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
		Mockito.verifyNoMoreInteractions(aggregatorMock);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenCreateAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getEmptySecurityContext());

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		testSubject.createAAS(shell);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		testSubject.createAAS(shell);

		Mockito.verify(aggregatorMock).createAAS(shell);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenCreateAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell shell = new AssetAdministrationShell("test", shellId, new Asset());
		testSubject.createAAS(shell);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithWriteAuthority());

		final IIdentifier shellId = new ModelUrn("urn:test");
		testSubject.deleteAAS(shellId);

		Mockito.verify(aggregatorMock).deleteAAS(shellId);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier shellId = new ModelUrn("urn:test");
		testSubject.deleteAAS(shellId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAAS_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final IIdentifier shellId = new ModelUrn("urn:test1");
		final AssetAdministrationShell expectedShell = new AssetAdministrationShell("test", shellId, new Asset());
		Mockito.when(aggregatorMock.getAAS(shellId)).thenReturn(expectedShell);

		final IAssetAdministrationShell shell = testSubject.getAAS(shellId);

		Assert.assertEquals(expectedShell, shell);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetAAS_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		final IIdentifier shellId = new ModelUrn("urn:test1");


		testSubject.getAAS(shellId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
		SecurityContextHolder.setContext(getSecurityContextWithReadAuthority());

		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas"));
		// Mockito.when(registryMock.lookupAll()).thenReturn(expectedAASDescriptorList);

		final Collection<IAssetAdministrationShell> shellList = testSubject.getAASList();

		Assert.assertEquals(expectedAASDescriptorList, shellList);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetAASList_thenThrowProviderException() {
		SecurityContextHolder.setContext(getSecurityContextWithoutAuthorities());

		testSubject.getAASList();
	}
}
