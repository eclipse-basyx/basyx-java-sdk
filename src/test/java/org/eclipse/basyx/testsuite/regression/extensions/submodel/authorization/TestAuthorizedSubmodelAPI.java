/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.submodel.authorization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.basyx.extensions.submodel.authorization.AuthorizedSubmodelAPI;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.AuthorizationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.paho.client.mqttv3.MqttException;
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
 * @author espen
 *
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestAuthorizedSubmodelAPI {
	@Mock
	private ISubmodelAPI apiMock;
	private AuthorizedSubmodelAPI authorizedSubmodelAPI;

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

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedSubmodelAPI.READ_AUTHORITY, AuthorizedSubmodelAPI.WRITE_AUTHORITY);


	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
	}

	@Before
	public void setUp() {
		authorizedSubmodelAPI = new AuthorizedSubmodelAPI(apiMock);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodel();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodel()).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAPI.getSubmodel();
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenAddSubmodelElementWithPath_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenAddSubmodelElementWithPath_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenAddSubmodelElementWithPath_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAPI.addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
		Mockito.verify(apiMock).addSubmodelElement(PROPERTY_IDSHORT, PROPERTY);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAPI.deleteSubmodelElement(PROPERTY_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAPI.updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
		Mockito.verify(apiMock).updateSubmodelElement(PROPERTY_IDSHORT, PROPERTY_VALUE);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElement_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElement_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElement_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelElement(PROPERTY_IDSHORT)).thenReturn(PROPERTY);
		ISubmodelElement returnedProperty = authorizedSubmodelAPI.getSubmodelElement(PROPERTY_IDSHORT);
		assertEquals(PROPERTY, returnedProperty);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperations_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getOperations();
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetOperations_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getOperations();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperations_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Collection<IOperation> expectedOperations = new ArrayList<>();
		expectedOperations.add(OPERATION);
		Mockito.when(apiMock.getOperations()).thenReturn(expectedOperations);
		Collection<IOperation> returnedOperations = authorizedSubmodelAPI.getOperations();
		assertEquals(expectedOperations, returnedOperations);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElements_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElements_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElements();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElements_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Collection<ISubmodelElement> expectedElements = new ArrayList<>();
		expectedElements.add(PROPERTY);
		Mockito.when(apiMock.getSubmodelElements()).thenReturn(expectedElements);
		Collection<ISubmodelElement> returnedElements = authorizedSubmodelAPI.getSubmodelElements();
		assertEquals(expectedElements, returnedElements);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelElementValue_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelElementValue_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelElementValue_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelElementValue(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.getSubmodelElementValue(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeOperation_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenInvokeOperation_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenInvokeOperation_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.invokeOperation(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.invokeOperation(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenInvokeAsync_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenInvokeAsync_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenInvokeAsync_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.invokeAsync(PROPERTY_IDSHORT)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.invokeAsync(PROPERTY_IDSHORT);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetOperationResult_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetOperationResult_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetOperationResult_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID)).thenReturn(PROPERTY_VALUE);
		Object returnedValue = authorizedSubmodelAPI.getOperationResult(PROPERTY_IDSHORT, ASYNC_REQUEST_ID);
		assertEquals(PROPERTY_VALUE, returnedValue);
	}
}
