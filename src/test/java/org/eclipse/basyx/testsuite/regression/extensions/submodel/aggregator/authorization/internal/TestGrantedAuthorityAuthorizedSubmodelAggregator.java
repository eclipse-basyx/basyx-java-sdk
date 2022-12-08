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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.basyx.extensions.shared.authorization.internal.NotAuthorizedException;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal.AuthorizedSubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.testsuite.regression.extensions.shared.internal.AuthorizationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
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
public class TestGrantedAuthorityAuthorizedSubmodelAggregator {

	@Mock
	private ISubmodelAggregator apiMock;
	private AuthorizedSubmodelAggregator<?> testSubject;

	protected static Submodel submodel;
	protected static ISubmodelAPI submodelAPI;
	private static final String SUBMODEL_IDSHORT = "submodelIdShort";
	private static final String SUBMODEL_ID = "submodelId";
	private static final Identifier SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, SUBMODEL_ID);

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedSubmodelAggregator.READ_AUTHORITY, AuthorizedSubmodelAggregator.WRITE_AUTHORITY, null);

	@BeforeClass
	public static void setUpClass() {
		submodel = new Submodel(SUBMODEL_IDSHORT, SUBMODEL_IDENTIFIER);
		submodelAPI = new VABSubmodelAPI(new VABMapProvider(submodel));
	}

	@Before
	public void setUp() {
		testSubject = new AuthorizedSubmodelAggregator<>(apiMock);
	}

	@After
	public void tearDown() {
		securityContextProvider.clearContext();
		Mockito.verifyNoMoreInteractions(apiMock);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelList_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.getSubmodelList();
	}

	@Test(expected = NotAuthorizedException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelList_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelList();
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelList_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		final Collection<ISubmodel> expectedList = new ArrayList<>();
		expectedList.add(submodel);
		Mockito.when(apiMock.getSubmodelList()).thenReturn(expectedList);
		Mockito.when(apiMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);

		final Collection<ISubmodel> smList = testSubject.getSubmodelList();
		assertEquals(expectedList, smList);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodel_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodel(SUBMODEL_IDENTIFIER)).thenReturn(submodel);

		final ISubmodel returnedSubmodel = testSubject.getSubmodel(SUBMODEL_IDENTIFIER);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelbyIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelbyIdShort_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelbyIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);

		final ISubmodel returnedSubmodel = testSubject.getSubmodelbyIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodel, returnedSubmodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIById_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIById_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIById_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelAPIById(SUBMODEL_IDENTIFIER)).thenReturn(submodelAPI);

		final ISubmodelAPI returnedSubmodelAPI = testSubject.getSubmodelAPIById(SUBMODEL_IDENTIFIER);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test
	public void givenPrincipalHasReadAuthority_whenGetSubmodelAPIByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		Mockito.when(apiMock.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT)).thenReturn(submodelAPI);

		final ISubmodelAPI returnedSubmodelAPI = testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
		assertEquals(submodelAPI, returnedSubmodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenGetSubmodelAPIByIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenGetSubmodelAPIByIdShort_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.getSubmodelAPIByIdShort(SUBMODEL_IDSHORT);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.createSubmodel(submodel);
		Mockito.verify(apiMock).createSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.createSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodel_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.createSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenCreateSubmodelAPI_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.createSubmodel(submodelAPI);
		Mockito.verify(apiMock).createSubmodel(submodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenCreateSubmodelAPI_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.createSubmodel(submodelAPI);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenCreateSubmodelAPI_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.createSubmodel(submodelAPI);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenUpdateSubmodel_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.updateSubmodel(submodel);
		Mockito.verify(apiMock).updateSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenUpdateSubmodel_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.updateSubmodel(submodel);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenUpdateSubmodel_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.updateSubmodel(submodel);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdentifier_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
		Mockito.verify(apiMock).deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelByIdentifier_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdentifier_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		testSubject.deleteSubmodelByIdentifier(SUBMODEL_IDENTIFIER);
	}

	@Test
	public void givenPrincipalHasWriteAuthority_whenDeleteSubmodelByIdShort_thenInvocationIsForwarded() {
		securityContextProvider.setSecurityContextWithWriteAuthority();

		Mockito.when(apiMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);
		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
		Mockito.verify(apiMock).deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenPrincipalIsMissingReadAuthority_whenDeleteSubmodelByIdShort_thenThrowProviderException() {
		securityContextProvider.setSecurityContextWithoutAuthorities();

		Mockito.when(apiMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);
		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

	@Test(expected = ProviderException.class)
	public void givenSecurityContextIsEmpty_whenDeleteSubmodelByIdShort_thenThrowProviderException() {
		securityContextProvider.setEmptySecurityContext();

		Mockito.when(apiMock.getSubmodelbyIdShort(SUBMODEL_IDSHORT)).thenReturn(submodel);
		testSubject.deleteSubmodelByIdShort(SUBMODEL_IDSHORT);
	}

}