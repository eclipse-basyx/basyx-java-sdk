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
package org.eclipse.basyx.extensions.submodel.aggregator.authorization.internal;

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.internal.InhibitException;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Interface for the authorization points used in {@link AuthorizedSubmodelAggregator}.
 *
 * @author wege
 */
public interface ISubmodelAggregatorAuthorizer<SubjectInformationType> {
  /**
   * Checks authorization for {@link ISubmodelAggregator#getSubmodelList()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smListSupplier
   *                           supplier for the collection of submodels.
   * @return the authorized collection of submodels.
   * @throws InhibitException if authorization failed
   */
  public Collection<ISubmodel> authorizeGetSubmodelList(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<Collection<ISubmodel>> smListSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#getSubmodel(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smSupplier
   *                           supplier for the submodel.
   * @return the authorized submodel.
   * @throws InhibitException if authorization failed
   */
  public ISubmodel authorizeGetSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#getSubmodelbyIdShort(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smIdShortPath
   *                           short path id of the submodel.
   * @param smSupplier
   *                           supplier for the submodel.
   * @return the authorized submodel.
   * @throws InhibitException if authorization failed
   */
  public ISubmodel authorizeGetSubmodelbyIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#getSubmodelAPIById(IIdentifier)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smAPISupplier
   *                           supplier for the submodel API.
   * @return the authorized submodel API
   * @throws InhibitException if authorization failed
   */
  public ISubmodelAPI authorizeGetSubmodelAPIById(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#getSubmodelAPIByIdShort(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smIdShortPath
   *                           short path id of the submodel.
   * @param smAPISupplier
   *                           supplier for the submodel API.
   * @return the authorized submodel API.
   * @throws InhibitException if authorization failed
   */
  public ISubmodelAPI authorizeGetSubmodelAPIByIdShort(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final String smIdShortPath,
      final Supplier<ISubmodelAPI> smAPISupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#createSubmodel(Submodel)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smId
   *                           id of the submodel.
   * @throws InhibitException if authorization failed
   */
  public void authorizeCreateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#updateSubmodel(Submodel)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smId
   *                           id of the submodel.
   * @throws InhibitException if authorization failed
   */
  public void authorizeUpdateSubmodel(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAggregator#deleteSubmodelByIdentifier(IIdentifier)} and {@link ISubmodelAggregator#deleteSubmodelByIdShort(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAggregator}, may be null.
   * @param smId
   *                           id of the submodel.
   * @throws InhibitException if authorization failed
   */
  public void authorizeDeleteSubmodelByIdentifier(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId
  ) throws InhibitException;
}
