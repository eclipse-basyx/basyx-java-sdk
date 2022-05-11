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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.aggregator.authorization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.basyx.extensions.shared.authorization.AbacRule;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetAbacRuleChecker;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SimpleAbacSubmodelAggregatorAuthorizer;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SubmodelAggregatorScopes;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.KeycloakAuthenticationContextProvider;
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
 * @author espen
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedSubmodelAggregator {
	@Mock
	private ISubmodelAggregator aggregatorMock;
	private AuthorizedSubmodelAggregator authorizedSubmodelAggregator;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private AbacRuleSet abacRuleSet = new AbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";

	protected static Submodel submodel;
	protected static ISubmodelAPI submodelAPI;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	@BeforeClass
	public static void setUpClass() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		submodelAPI = new VABSubmodelAPI(new VABMapProvider(submodel));
	}

	@Before
	public void setUp() {
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				SubmodelAggregatorScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				SubmodelAggregatorScopes.WRITE_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				readerRole,
				SubmodelAggregatorScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		authorizedSubmodelAggregator = new AuthorizedSubmodelAggregator(
				aggregatorMock,
				new SimpleAbacSubmodelAggregatorAuthorizer(
						new PredefinedSetAbacRuleChecker(abacRuleSet),
						new KeycloakAuthenticator()
				)
		);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelList_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		Assert.assertEquals(Collections.emptyList(), authorizedSubmodelAggregator.getSubmodelList());
	}

	@Test
	public void givenSecurityContextIsEmpty_whenGetSubmodelList_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		Assert.assertEquals(Collections.emptyList(), authorizedSubmodelAggregator.getSubmodelList());
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Collection<ISubmodel> expectedList = new ArrayList<>();
		expectedList.add(submodel);
		Mockito.when(aggregatorMock.getSubmodelList()).thenReturn(expectedList);
		//Mockito.when(aggregatorMock.getSubmodel(submodel.getIdentification())).thenReturn(submodel);
		Collection<ISubmodel> smList = authorizedSubmodelAggregator.getSubmodelList();
		assertEquals(expectedList, smList);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(aggregatorMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whengetSubmodelbyIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whengetSubmodelbyIdShort_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelbyIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(aggregatorMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIById_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIById_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIById_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(aggregatorMock.getSubmodelAPIById(SUBMODEL_IDENTIFIER)).thenReturn(submodelAPI);
		ISubmodelAPI returnedSubmodelAPI = authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(aggregatorMock.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT)).thenReturn(submodelAPI);
		ISubmodelAPI returnedSubmodelAPI = authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIByIdShort_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAggregator.createSubmodel(submodel);
		Mockito.verify(aggregatorMock).createSubmodel(submodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.createSubmodel(submodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodel_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.createSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodelAPI_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
		Mockito.verify(aggregatorMock).createSubmodel(submodelAPI);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodelAPI_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodelAPI_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAggregator.updateSubmodel(submodel);
		Mockito.verify(aggregatorMock).updateSubmodel(submodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodel_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdentifier_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		Mockito.verify(aggregatorMock).deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodelByIdentifier_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdentifier_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
		Mockito.verify(aggregatorMock).deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingWriteAuthority_whenDeleteSubmodelByIdShort_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdShort_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

}
