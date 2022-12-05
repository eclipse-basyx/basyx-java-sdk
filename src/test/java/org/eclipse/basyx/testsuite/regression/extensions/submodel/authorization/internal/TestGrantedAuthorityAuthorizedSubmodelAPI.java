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
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorized;
import org.eclipse.basyx.extensions.submodel.authorization.internal.AuthorizedSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.AuthorizationContextProvider;
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
public class TestGrantedAuthorityAuthorizedSubmodelAPI {

	@Mock
	private ISubmodelAPI apiMock;
	private AuthorizedSubmodelAPI<?> authorizedSubmodelAPI;

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

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedSubmodelAPI.READ_AUTHORITY, AuthorizedSubmodelAPI.WRITE_AUTHORITY, AuthorizedSubmodelAPI.EXECUTE_AUTHORITY);

	@BeforeClass
	public static void setUpClass() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
	}

	@Before
	public void setUp() {
		authorizedSubmodelAPI = new AuthorizedSubmodelAPI<>(apiMock);
		Mockito.when(apiMock.getSubmodel()).thenReturn(submodel);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getSubmodel();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getSubmodel();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();

		final ISubmodel returnedSubmodel = authorizedSubmodelAPI.getSubmodel();
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElement_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElementWithPath_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElementWithPath_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElementWithPath_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelElement_ThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
		Mockito.verify(apiMock).updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElement_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();

		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		final ISubmodelElement returnedProperty = authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
		assertEquals(PROPERTY, returnedProperty);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperations_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getOperations();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetOperations_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getOperations();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperations_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		final Collection<IOperation> expectedOperations = new ArrayList<>();
		expectedOperations.add(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(expectedOperations);
		Mockito.when(apiMock.getSubmodelElement(OPERATION_IDSHORT)).thenReturn(OPERATION);

		final Collection<IOperation> returnedOperations = authorizedSubmodelAPI.getOperations();
		assertEquals(expectedOperations, returnedOperations);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElements_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElements_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElements_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		final Collection<ISubmodelElement> expectedElements = new ArrayList<>();
		expectedElements.add(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(expectedElements);
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);

		final Collection<ISubmodelElement> returnedElements = authorizedSubmodelAPI.getSubmodelElements();
		assertEquals(expectedElements, returnedElements);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElementValue_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElementValue_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElementValue_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelElementValue(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);

		final Object returnedValue = authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeOperation_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenInvokeOperation_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeOperation_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithExecuteAuthority();
		Mockito.when(apiMock.invokeOperation(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);

		final Object returnedValue = authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingExecuteAuthority_whenInvokeAsync_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenInvokeAsync_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasExecuteAuthority_whenInvokeAsync_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithExecuteAuthority();
		Mockito.when(apiMock.invokeAsync(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);

		final Object returnedValue = authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = NotAuthorized.class)
	public void givenPrincipalIsMissingExecuteAuthority_whenGetOperationResult_thenThrowNotAuthorized() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test(expected = NotAuthorized.class)
	public void givenSecurityContextIsEmpty_whenGetOperationResult_thenThrowNotAuthorized() {
		securityContextProvider.setEmptySecurityContext();

		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperationResult_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID)).thenReturn(PROPERTY_VALUE);

		final Object returnedValue = authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}
}