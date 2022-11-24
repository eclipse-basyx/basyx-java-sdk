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
package org.eclipse.basyx.testsuite.regression.extensions.aas.aggregator.authorization;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.extensions.aas.aggregator.authorization.AuthorizedAASAggregator;
import org.eclipse.basyx.extensions.shared.authorization.NotAuthorized;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.AuthorizationContextProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests authorization with the AuthorizedAASAggregator
 *
 * @author jungjan, fried, fischer
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class TestGrantedAuthorityAuthorizedAASAggregator {
  @Mock
  private IAASAggregator aggregatorMock;
  private AuthorizedAASAggregator<?> testSubject;
  private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedAASAggregator.READ_AUTHORITY, AuthorizedAASAggregator.WRITE_AUTHORITY, null);

  private static final String SHELL_ID = "shell";
  private static final Identifier SHELL_IDENTIFIER = new Identifier(IdentifierType.IRI, SHELL_ID);
  private static final String ASSET_ID = "asset";
  private static final Identifier ASSET_IDENTIFIER = new Identifier(IdentifierType.IRI, ASSET_ID);
  private static final Asset SHELL_ASSET = new Asset(ASSET_ID, ASSET_IDENTIFIER, AssetKind.INSTANCE);

  private static AssetAdministrationShell shell;

  @Before
  public void setUp() {
    testSubject = new AuthorizedAASAggregator<>(aggregatorMock);
    shell = new AssetAdministrationShell(SHELL_ID, SHELL_IDENTIFIER, SHELL_ASSET);
  }

  @After
  public void tearDown() {
    securityContextProvider.clearContext();
    Mockito.verifyNoMoreInteractions(aggregatorMock);
  }

  private AssetAdministrationShell invokeCreateAAS() {
    testSubject.createAAS(shell);
    return shell;
  }

  @Test(expected = NotAuthorized.class)
  public void givenSecurityContextIsEmpty_whenCreateAAS_thenThrowNotAuthorized() {
    securityContextProvider.setEmptySecurityContext();
    invokeCreateAAS();
  }

  @Test
  public void givenPrincipalHasWriteAuthority_whenCreateAAS_thenInvocationIsForwarded() {
    securityContextProvider.setSecurityContextWithWriteAuthority();
    final AssetAdministrationShell shell = invokeCreateAAS();
    Mockito.verify(aggregatorMock).createAAS(shell);
  }

  @Test(expected = NotAuthorized.class)
  public void givenPrincipalIsMissingWriteAuthority_whenCreateAAS_thenThrowNotAuthorized() {
    securityContextProvider.setSecurityContextWithoutAuthorities();
    invokeCreateAAS();
  }

  private IIdentifier invokeDeleteAAS() {
    testSubject.deleteAAS(SHELL_IDENTIFIER);
    return SHELL_IDENTIFIER;
  }

  @Test
  public void givenPrincipalHasWriteAuthority_whenDeleteAAS_thenInvocationIsForwarded() {
    securityContextProvider.setSecurityContextWithWriteAuthority();
    final IIdentifier shellId = invokeDeleteAAS();
    Mockito.verify(aggregatorMock).deleteAAS(shellId);
  }

  @Test(expected = NotAuthorized.class)
  public void givenPrincipalIsMissingWriteAuthority_whenDeleteAAS_thenThrowNotAuthorized() {
    securityContextProvider.setSecurityContextWithoutAuthorities();
    invokeDeleteAAS();
  }

  @Test
  public void givenPrincipalHasReadAuthority_whenGetAAS_thenInvocationIsForwarded() {
    securityContextProvider.setSecurityContextWithReadAuthority();

    final AssetAdministrationShell expectedShell = shell;
    Mockito.when(aggregatorMock.getAAS(SHELL_IDENTIFIER)).thenReturn(expectedShell);

    final IAssetAdministrationShell shell = testSubject.getAAS(SHELL_IDENTIFIER);

    Assert.assertEquals(expectedShell, shell);
  }

  @Test(expected = NotAuthorized.class)
  public void givenPrincipalIsMissingReadAuthority_whenGetAAS_thenThrowNotAuthorized() {
    securityContextProvider.setSecurityContextWithoutAuthorities();

    testSubject.getAAS(SHELL_IDENTIFIER);
  }

  @Test
  public void givenPrincipalHasReadAuthority_whenGetAASList_thenInvocationIsForwarded() {
    securityContextProvider.setSecurityContextWithReadAuthority();

    final Collection<IAssetAdministrationShell> expectedAASDescriptorList = Collections.singletonList(shell);
    Mockito.when(aggregatorMock.getAASList()).thenReturn(expectedAASDescriptorList);
    Mockito.when(aggregatorMock.getAAS(SHELL_IDENTIFIER)).thenReturn(shell);

    final Collection<IAssetAdministrationShell> shellList = testSubject.getAASList();

    Assert.assertEquals(expectedAASDescriptorList, shellList);
  }

  @Test(expected = NotAuthorized.class)
  public void givenPrincipalIsMissingReadAuthority_whenGetAASList_thenThrowNotAuthorized() {
    securityContextProvider.setSecurityContextWithoutAuthorities();

    testSubject.getAASList();
  }
}