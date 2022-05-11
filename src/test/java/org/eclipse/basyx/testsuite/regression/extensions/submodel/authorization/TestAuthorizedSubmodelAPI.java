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
package org.eclipse.basyx.testsuite.regression.extensions.submodel.authorization;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.basyx.extensions.shared.authorization.AbacRule;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.PredefinedSetAbacRuleChecker;
import org.eclipse.basyx.extensions.submodel.authorization.AuthorizedSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.authorization.SimpleAbacSubmodelAPIAuthorizer;
import org.eclipse.basyx.extensions.submodel.authorization.SubmodelAPIScopes;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.KeycloakAuthenticationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
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
 * @author espen, wege
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedSubmodelAPI {
	@Mock
	private ISubmodelAPI apiMock;
	private AuthorizedSubmodelAPI authorizedSubmodelAPI;
	private KeycloakAuthenticationContextProvider securityContextProvider = new KeycloakAuthenticationContextProvider();
	private AbacRuleSet abacRuleSet = new AbacRuleSet();

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
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				SubmodelAPIScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				SubmodelAPIScopes.WRITE_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				adminRole,
				SubmodelAPIScopes.EXECUTE_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				readerRole,
				SubmodelAPIScopes.READ_SCOPE,
				"*",
				"*",
				"*"
		));
		abacRuleSet.addRule(AbacRule.of(
				executorRole,
				SubmodelAPIScopes.EXECUTE_SCOPE,
				"*",
				"*",
				"*"
		));
		authorizedSubmodelAPI = new AuthorizedSubmodelAPI(
				apiMock,
				new SimpleAbacSubmodelAPIAuthorizer(
						new PredefinedSetAbacRuleChecker(abacRuleSet),
						new KeycloakAuthenticator()
				)
		);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodel()).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAPI.getSubmodel();
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElementWithPath_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElementWithPath_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElementWithPath_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(adminRole);
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
		Mockito.verify(apiMock).updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		ISubmodelElement returnedProperty = authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
		assertEquals(PROPERTY, returnedProperty);
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetOperations_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final Collection<IOperation> operationList = Collections.singletonList(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(operationList);
		Mockito.when(apiMock.getSubmodelElement(OPERATION_IDSHORT)).thenReturn(OPERATION);
		final Collection<IOperation> returnedOperationCollection = authorizedSubmodelAPI.getOperations();
		Assert.assertEquals(Collections.emptyList(), returnedOperationCollection);
	}

	@Test
	public void givenSecurityContextIsEmpty_whenGetOperations_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		final Collection<IOperation> operationList = Collections.singletonList(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(operationList);
		Mockito.when(apiMock.getSubmodelElement(OPERATION_IDSHORT)).thenReturn(OPERATION);
		final Collection<IOperation> returnedOperationCollection = authorizedSubmodelAPI.getOperations();
		Assert.assertEquals(Collections.emptyList(), returnedOperationCollection);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperations_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Collection<IOperation> expectedOperations = new ArrayList<>();
		expectedOperations.add(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(expectedOperations);
		Mockito.when(apiMock.getSubmodelElement(OPERATION_IDSHORT)).thenReturn(OPERATION);
		Collection<IOperation> returnedOperations = authorizedSubmodelAPI.getOperations();
		assertEquals(expectedOperations, returnedOperations);
	}

	@Test
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElements_thenReturnEmptyCollection() {
		securityContextProvider.setSecurityContextWithoutRoles();
		final Collection<ISubmodelElement> submodelElementList = Collections.singletonList(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(submodelElementList);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		final Collection<ISubmodelElement> returnedSubmodelElementCollection = authorizedSubmodelAPI.getSubmodelElements();
		Assert.assertEquals(Collections.emptyList(), returnedSubmodelElementCollection);
	}

	@Test
	public void givenSecurityContextIsEmpty_whenGetSubmodelElements_thenReturnEmptyCollection() {
		securityContextProvider.setEmptySecurityContext();
		securityContextProvider.setSecurityContextWithoutRoles();
		final Collection<ISubmodelElement> submodelElementList = Collections.singletonList(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(submodelElementList);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		final Collection<ISubmodelElement> returnedSubmodelElementCollection = authorizedSubmodelAPI.getSubmodelElements();
		Assert.assertEquals(Collections.emptyList(), returnedSubmodelElementCollection);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElements_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Collection<ISubmodelElement> expectedElements = new ArrayList<>();
		expectedElements.add(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(expectedElements);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		Collection<ISubmodelElement> returnedElements = authorizedSubmodelAPI.getSubmodelElements();
		assertEquals(expectedElements, returnedElements);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElementValue_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElementValue_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElementValue_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getSubmodelElementValue(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeOperation_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenInvokeOperation_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeOperation_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(executorRole);
		Mockito.when(apiMock.invokeOperation(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeAsync_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenInvokeAsync_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeAsync_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(executorRole);
		Mockito.when(apiMock.invokeAsync(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperationResult_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutRoles();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetOperationResult_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperationResult_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithRoles(readerRole);
		Mockito.when(apiMock.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}
}
