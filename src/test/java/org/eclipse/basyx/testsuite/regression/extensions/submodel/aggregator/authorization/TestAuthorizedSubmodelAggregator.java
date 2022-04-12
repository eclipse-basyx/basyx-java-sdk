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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.basyx.extensions.submodel.aggregator.authorization.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.AuthorizationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
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
public class TestAuthorizedSubmodelAggregator {
	@Mock
	private ISubmodelAggregator aggregatorMock;
	private AuthorizedSubmodelAggregator authorizedSubmodelAggregator;

	protected static Submodel submodel;
	protected static ISubmodelAPI submodelAPI;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedSubmodelAggregator.READ_AUTHORITY, AuthorizedSubmodelAggregator.WRITE_AUTHORITY);


	@BeforeClass
	public static void setUpClass() throws MqttException, IOException {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		submodelAPI = new VABSubmodelAPI(new VABMapProvider(submodel));
	}

	@Before
	public void setUp() {
		authorizedSubmodelAggregator = new AuthorizedSubmodelAggregator(aggregatorMock);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(aggregatorMock);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelList_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.getSubmodelList();
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelList_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelList();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Collection<ISubmodel> expectedList = new ArrayList<>();
		expectedList.add(submodel);
		Mockito.when(aggregatorMock.getSubmodelList()).thenReturn(expectedList);
		Collection<ISubmodel> smList = authorizedSubmodelAggregator.getSubmodelList();
		assertEquals(expectedList, smList);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(aggregatorMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAggregator.getSubmodel(SUBMODEL_IDENTIFIER);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whengetSubmodelbyIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whengetSubmodelbyIdShort_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelbyIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(aggregatorMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);
		ISubmodel returnedSubmodel = authorizedSubmodelAggregator.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodel, returnedSubmodel);
	}


	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIById_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIById_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIById_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(aggregatorMock.getSubmodelAPIById(SUBMODEL_IDENTIFIER)).thenReturn(submodelAPI);
		ISubmodelAPI returnedSubmodelAPI = authorizedSubmodelAggregator.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(aggregatorMock.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT)).thenReturn(submodelAPI);
		ISubmodelAPI returnedSubmodelAPI = authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIByIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIByIdShort_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAggregator.createSubmodel(submodel);
		Mockito.verify(aggregatorMock).createSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.createSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodel_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.createSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodelAPI_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
		Mockito.verify(aggregatorMock).createSubmodel(submodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodelAPI_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodelAPI_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.createSubmodel(submodelAPI);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAggregator.updateSubmodel(submodel);
		Mockito.verify(aggregatorMock).updateSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodel_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.updateSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdentifier_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		Mockito.verify(aggregatorMock).deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelByIdentifier_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdentifier_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
		Mockito.verify(aggregatorMock).deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelByIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdShort_ThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();
		authorizedSubmodelAggregator.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

}
