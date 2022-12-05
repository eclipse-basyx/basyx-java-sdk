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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.authorization.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.basyx.extensions.shared.authorization.internal.BaSyxObjectTargetInformation;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRule;
import org.eclipse.basyx.extensions.shared.authorization.internal.RbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.extensions.shared.authorization.internal.PredefinedSetRbacRuleChecker;
import org.eclipse.basyx.extensions.submodel.authorization.internal.AuthorizedSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.authorization.internal.SimpleRbacSubmodelAPIAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.internal.SubmodelAPIScopes;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.KeycloakAuthenticationContextProvider;
import org.junit.After;
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
 * @author espen, wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestSimpleRbacAuthorizedSubmodelAPI {
	@Mock
	private ISubmodelAPI apiMock;
	private AuthorizedSubmodelAPI<?> authorizedSubmodelAPI;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private RbacRuleSet rbacRuleSet = new RbacRuleSet();

	private final String adminRole = "admin";
	private final String readerRole = "reader";
	private final String executorRole = "executor";

	protected static Submodel submodel;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);
	private static final String PROPERTY_IDSHORT = "testProp";
	private static final boolean PROPERTY_VALUE = true;
	private static final ISubmodelElement PROPERTY = new Property(PROPERTY_IDSHORT, PROPERTY_VALUE);
	private static final String OPERATION_IDSHORT = "testOperation";
	private static final String ASYNC_REQUEST_ID = "requestId";
	private static final IOperation OPERATION = new Operation(OPERATION_IDSHORT);

	@BeforeClass
	public static void setUpClass() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
	}

	@Before
	public void setUp() {
		rbacRuleSet.addRule(new RbacRule(adminRole, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, SubmodelAPIScopes.WRITE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(adminRole, SubmodelAPIScopes.EXECUTE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(readerRole, SubmodelAPIScopes.READ_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		rbacRuleSet.addRule(new RbacRule(executorRole, SubmodelAPIScopes.EXECUTE_SCOPE, new BaSyxObjectTargetInformation("*", "*", "*")));
		authorizedSubmodelAPI = new AuthorizedSubmodelAPI<>(apiMock, new SimpleRbacSubmodelAPIAuthorizer<>(new PredefinedSetRbacRuleChecker(rbacRuleSet), new KeycloakRoleAuthenticator()), new JWTAuthenticationContextProvider());
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodel()).thenReturn(submodel);
		final ISubmodel returnedSubmodel = authorizedSubmodelAPI.getSubmodel();
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElementWithPath_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElementWithPath_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElementWithPath_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelElement_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodelElement_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
		Mockito.verify(apiMock).updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElement_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		final ISubmodelElement returnedProperty = authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
		assertEquals(PROPERTY, returnedProperty);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperations_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getOperations();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetOperations_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getOperations();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperations_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final Collection<IOperation> expectedOperations = new ArrayList<>();
		expectedOperations.add(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(expectedOperations);
		Mockito.when(apiMock.getSubmodelElement(OPERATION_IDSHORT)).thenReturn(OPERATION);
		final Collection<IOperation> returnedOperations = authorizedSubmodelAPI.getOperations();
		assertEquals(expectedOperations, returnedOperations);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElements_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElements_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElements_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		final Collection<ISubmodelElement> expectedElements = new ArrayList<>();
		expectedElements.add(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(expectedElements);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		final Collection<ISubmodelElement> returnedElements = authorizedSubmodelAPI.getSubmodelElements();
		assertEquals(expectedElements, returnedElements);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElementValue_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElementValue_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElementValue_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelElementValue(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		final Object returnedValue = authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeOperation_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenInvokeOperation_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeOperation_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(executorRole);
		Mockito.when(apiMock.invokeOperation(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		final Object returnedValue = authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeAsync_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenInvokeAsync_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeAsync_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(executorRole);
		Mockito.when(apiMock.invokeAsync(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		final Object returnedValue = authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperationResult_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetOperationResult_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperationResult_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID)).thenReturn(PROPERTY_VALUE);
		final Object returnedValue = authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}
}
