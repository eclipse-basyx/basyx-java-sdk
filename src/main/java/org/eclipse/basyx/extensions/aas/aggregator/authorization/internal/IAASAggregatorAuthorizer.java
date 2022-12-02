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
package org.eclipse.basyx.extensions.aas.aggregator.authorization.internal;

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Interface for the authorization points used in {@link AuthorizedAASAggregator}.
 *
 * @author wege
 */
public interface IAASAggregatorAuthorizer<SubjectInformationType> {
  /**
   * Checks authorization for {@link IAASAggregator#getAAS(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param aasSupplier
   *                           supplier for the AAS.
   * @throws InhibitException if authorization failed
   */
  public IAssetAdministrationShell authorizeGetAAS(
      SubjectInformationType subjectInformation,
      IIdentifier aasId,
      Supplier<IAssetAdministrationShell> aasSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASAggregator#getAASList()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasListSupplier
   *                           supplier for the collection of AASes.
   * @return the authorized collection of AASes
   * @throws InhibitException if authorization failed
   */
  public Collection<IAssetAdministrationShell> authorizeGetAASList(
      SubjectInformationType subjectInformation,
      Supplier<Collection<IAssetAdministrationShell>> aasListSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASAggregator#getAASProvider(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @param modelProviderSupplier
   *                           supplier for the model provider.
   * @return the authorized model provider
   * @throws InhibitException if authorization failed
   */
  public IModelProvider authorizeGetAASProvider(
      SubjectInformationType subjectInformation,
      IIdentifier aasId,
      Supplier<IModelProvider> modelProviderSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASAggregator#createAAS(AssetAdministrationShell)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the AAS.
   * @throws InhibitException if authorization failed
   */
  public void authorizeCreateAAS(
      SubjectInformationType subjectInformation,
      AssetAdministrationShell aas
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASAggregator#updateAAS(AssetAdministrationShell)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the AAS.
   * @throws InhibitException if authorization failed
   */
  public void authorizeUpdateAAS(
      SubjectInformationType subjectInformation,
      AssetAdministrationShell aas
  ) throws InhibitException;

  /**
   * Checks authorization for {@link IAASAggregator#deleteAAS(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aasId
   *                           id of the AAS.
   * @throws InhibitException if authorization failed
   */
  public void authorizeDeleteAAS(
      SubjectInformationType subjectInformation,
      IIdentifier aasId
  ) throws InhibitException;
}
