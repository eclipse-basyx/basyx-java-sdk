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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.aggregator.authorization.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SubmodelAggregatorScopes;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.SimpleRbacSubmodelAggregatorAuthorizer;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.KeycloakAuthenticationContextProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests authorization with the AuthorizedSubmodelAggregator
 *
 * @author wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestSimpleRbacAuthorizedSubmodelAggregator {

	@Mock
	private ISubmodelAggregator apiMock;
	private AuthorizedSubmodelAggregator<?> testSubject;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";
	private final String partialReaderRole = "partialReader";

	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String SECOND_SUBMODEL_IDSHORT = "secondSubmodelIdShort";
	private static final String SECOND_SUBMODEL_ID = "secondSubmodelId";
	private static final Identifier SECOND_SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SECOND_SUBMODEL_ID);

	protected static Submodel submodel;
	protected static ISubmodelAPI submodelAPI;
	protected static Submodel secondSubmodel;

	@BeforeClass
	public static void setUpClass() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		submodelAPI = new VABSubmodelAPI(new VABMapProvider(submodel));
		secondSubmodel = new Submodel(SECOND_SUBMODEL_IDSHORT, SECOND_SUBMODEL_IDENTIFIER);
	}

	@Before
	public void setUp() {
		rbacRuleSet.addRule(new RbacRule(adminRole, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, SubmodelAggregatorScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(partialReaderRole, SubmodelAggregatorScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", SUBMODEL_IDENTIFIER.getId(), "*", "*")));
		testSubject = new AuthorizedSubmodelAggregator<>(apiMock, new SimpleRbacSubmodelAggregatorAuthorizer<>(new PredefinedSetRbacRuleChecker(rbacRuleSet), new KeycloakRoleAuthenticator()), new JWTAuthenticationContextProvider());
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelList_thenResultEmpty() {
		securityContextProvider.setSecurityContextWithoutRoles();

		final Collection<ISubmodel> returnedSubmodels = testSubject.getSubmodelList();
		assertTrue(returnedSubmodels.isEmpty());
	}

	@Test
	public void givenSecurityContextIsEmpty_whenGetSubmodelList_thenResultEmpty() {
		securityContextProvider.setEmptySecurityContext();

		final Collection<ISubmodel> returnedSubmodels = testSubject.getSubmodelList();
		assertTrue(returnedSubmodels.isEmpty());
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final Collection<ISubmodel> expectedList = new ArrayList<>();
		expectedList.add(submodel);
		Mockito.when(apiMock.getSubmodelList()).thenReturn(expectedList);
		Mockito.when(apiMock.getSubmodel(submodel.getIdentification())).thenReturn(submodel);

		final Collection<ISubmodel> smList = testSubject.getSubmodelList();
		assertEquals(expectedList, smList);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);

		final ISubmodel returnedSubmodel = testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whengetSubmodelbyIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whengetSubmodelbyIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelbyIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);

		final ISubmodel returnedSubmodel = testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIById_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIById_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIById_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelAPIById(SUBMODEL_IDENTIFIER)).thenReturn(submodelAPI);

		final ISubmodelAPI returnedSubmodelAPI = testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT)).thenReturn(submodelAPI);

		final ISubmodelAPI returnedSubmodelAPI = testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.createSubmodel(submodel);
		Mockito.verify(apiMock).createSubmodel(submodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.createSubmodel(submodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.createSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodelAPI_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.createSubmodel(submodelAPI);
		Mockito.verify(apiMock).createSubmodel(submodelAPI);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodelAPI_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.createSubmodel(submodelAPI);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodelAPI_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.createSubmodel(submodelAPI);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.updateSubmodel(submodel);
		Mockito.verify(apiMock).updateSubmodel(submodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.updateSubmodel(submodel);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.updateSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdentifier_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		Mockito.verify(apiMock).deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodelByIdentifier_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdentifier_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);

		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodelByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();

		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasPartialReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(partialReaderRole);

		final Collection<ISubmodel> expectedSubmodelList = Collections.singletonList(submodel);
		Mockito.when(apiMock.getSubmodelList()).thenReturn(new HashSet<>(Arrays.asList(submodel, secondSubmodel)));
		Mockito.when(apiMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);

		final Collection<ISubmodel> returnedSubmodelList = testSubject.getSubmodelList();

		Assert.assertEquals(expectedSubmodelList, returnedSubmodelList);
	}
}
