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
package org.eclipse.basyx.testsuite.regression.extensions.aas.registration.authorization;

import java.util.Collections;
import java.util.List;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.extensions.aas.registration.authorization.SimpleRbacAASRegistryAuthorizer;
import org.eclipse.basyx.extensions.shared.authorization.BaSyxObjectTargetInformation;
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
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests authorization with the AuthorizedAASRegistry
 *
 * @author pneuschwander, wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedAASRegistry {
	@Mock
	private IAASRegistry registryMock;
	private AuthorizedAASRegistry<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";

	@Before
	public void setUp() {
		rbacRuleSet.addRule(RbacRule.of(
				adminRole,
				AASRegistryScopes.READ_SCOPE,
				new BaSyxObjectTargetInformation("*", "*", "*")
		));
		rbacRuleSet.addRule(RbacRule.of(
				adminRole,
				AASRegistryScopes.WRITE_SCOPE,
				new BaSyxObjectTargetInformation("*", "*", "*")
		));
		rbacRuleSet.addRule(RbacRule.of(
				readerRole,
				AASRegistryScopes.READ_SCOPE,
				new BaSyxObjectTargetInformation("*", "*", "*")
		));
		testSubject = new AuthorizedAASRegistry<>(registryMock,
				new SimpleRbacAASRegistryAuthorizer<>(
						new PredefinedSetRbacRuleChecker(rbacRuleSet),
						new KeycloakRoleAuthenticator()
				),
				new JWTAuthenticationContextProvider()
		);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenRegisterAAS_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterAASDescriptor_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);

		Mockito.verify(registryMock).register(aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterAASDescriptor_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final AASDescriptor aasDescriptor = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		testSubject.register(aasDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenRegisterSubmodelDescriptor_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		final IIdentifier aas = new ModelUrn("urn:test");
		final SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel");
		testSubject.register(aas, submodelDescriptor);

		Mockito.verify(registryMock).register(aas, submodelDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenRegisterSubmodelDescriptor_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aas = new ModelUrn("urn:test");
		final SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel");
		testSubject.register(aas, submodelDescriptor);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		final IIdentifier aasId = new ModelUrn("urn:test");
		testSubject.delete(aasId);

		Mockito.verify(registryMock).delete(aasId);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aasId = new ModelUrn("urn:test");
		testSubject.delete(aasId);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		testSubject.delete(aasId, smId);

		Mockito.verify(registryMock).delete(aasId, smId);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		testSubject.delete(aasId, smId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAAS_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final AASDescriptor expectedAASDescriptor = new AASDescriptor("test", aasId, "http://test.example/aas");
		Mockito.when(registryMock.lookupAAS(aasId)).thenReturn(expectedAASDescriptor);

		final AASDescriptor aasDescriptor = testSubject.lookupAAS(aasId);

		Assert.assertEquals(expectedAASDescriptor, aasDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAAS_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aasId = new ModelUrn("urn:test1");

		testSubject.lookupAAS(aasId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupAll_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final AASDescriptor aas = new AASDescriptor("test", new ModelUrn("urn:test"), "http://test.example/aas");
		final List<AASDescriptor> expectedAASDescriptorList = Collections.singletonList(aas);
		Mockito.when(registryMock.lookupAll()).thenReturn(expectedAASDescriptorList);
		Mockito.when(registryMock.lookupAAS(aas.getIdentifier())).thenReturn(aas);

		final List<AASDescriptor> aasDescriptorList = testSubject.lookupAll();

		Assert.assertEquals(expectedAASDescriptorList, aasDescriptorList);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupAll_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.lookupAll();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodels_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final SubmodelDescriptor sm = new SubmodelDescriptor("test", new ModelUrn("urn:test"), "http://test.example/submodel");
		final List<SubmodelDescriptor> expectedSubmodelDescriptorList = Collections.singletonList(sm);
		Mockito.when(registryMock.lookupSubmodels(aasId)).thenReturn(expectedSubmodelDescriptorList);
		Mockito.when(registryMock.lookupSubmodel(aasId, sm.getIdentifier())).thenReturn(sm);

		final List<SubmodelDescriptor> submodelDescriptorList = testSubject.lookupSubmodels(aasId);

		Assert.assertEquals(expectedSubmodelDescriptorList, submodelDescriptorList);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodels_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aasId = new ModelUrn("urn:test1");

		testSubject.lookupSubmodels(aasId);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenLookupSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");
		final SubmodelDescriptor expectedSubmodelDescriptor = new SubmodelDescriptor("test", smId, "http://test.example/submodel");
		Mockito.when(registryMock.lookupSubmodel(aasId, smId)).thenReturn(expectedSubmodelDescriptor);

		final SubmodelDescriptor submodelDescriptor = testSubject.lookupSubmodel(aasId, smId);

		Assert.assertEquals(expectedSubmodelDescriptor, submodelDescriptor);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenLookupSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final IIdentifier aasId = new ModelUrn("urn:test1");
		final IIdentifier smId = new ModelUrn("urn:test2");

		testSubject.lookupSubmodel(aasId, smId);
	}

}
