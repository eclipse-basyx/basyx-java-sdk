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
package org.eclipse.basyx.extensions.submodel.authorization;

import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Interface for the authorization points used in {@link AuthorizedSubmodelAPI}.
 *
 * @author wege
 */
public interface ISubmodelAPIAuthorizer<SubjectInformationType> {
  /**
   * Checks authorization for {@link ISubmodelAPI#getSubmodelElements()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smSupplier
   *                           supplier for the submodel.
   * @param smElListSupplier
   *                           supplier for the submodel element list.
   * @return a list of authorized submodel elements
   * @throws InhibitException if authorization failed
   */
  public Collection<ISubmodelElement> authorizeGetSubmodelElements(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<ISubmodel> smSupplier,
      final Supplier<Collection<ISubmodelElement>> smElListSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#getSubmodelElement(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @param smElSupplier
   *                           supplier for the submodel element.
   * @return the authorized submodel element
   * @throws InhibitException if authorization failed
   */
  public ISubmodelElement authorizeGetSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<ISubmodelElement> smElSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#getSubmodel()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
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
   * Checks authorization for {@link ISubmodelAPI#addSubmodelElement(String, ISubmodelElement)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @throws InhibitException if authorization failed
   */
  public void authorizeAddSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#deleteSubmodelElement(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @throws InhibitException if authorization failed
   */
  public void authorizeDeleteSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  /**
   *Checks authorization for {@link ISubmodelAPI#updateSubmodelElement(String, Object)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @throws InhibitException if authorization failed
   */
  public void authorizeUpdateSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#getSubmodelElementValue(String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @param valueSupplier
   *                           supplier for the submodel element value.
   * @return the authorized submodel element value.
   * @throws InhibitException if authorization failed
   */
  public Object authorizeGetSubmodelElementValue(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<Object> valueSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#getOperations()}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smSupplier
   *                           supplier for the submodel.
   * @param operationListSupplier
   *                           supplier for the collection of operations.
   * @return the authorized collection of operations.
   * @throws InhibitException if authorization failed
   */
  public Collection<IOperation> authorizeGetOperations(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final Supplier<ISubmodel> smSupplier,
      final Supplier<Collection<IOperation>> operationListSupplier
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#invokeOperation(String, Object...)} and {@link ISubmodelAPI#invokeAsync(String, Object...)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @throws InhibitException if authorization failed
   */
  public void authorizeInvokeOperation(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  /**
   * Checks authorization for {@link ISubmodelAPI#getOperationResult(String, String)}.
   *
   * @param subjectInformation
   *                           information of the requester.
   * @param aas
   *                           the aas the submodel belongs to as passed in the constructor
   *                           of {@link AuthorizedSubmodelAPI}, may be null.
   * @param smId
   *                           id of the submodel.
   * @param smElIdShortPath
   *                           short path id of the submodel element.
   * @param requestId
   *                           id of the operation request.
   * @param operationResultSupplier
   *                           supplier for the result of the operation.
   * @return the authorized result of the operation.
   * @throws InhibitException if authorization failed
   */
  public Object authorizeGetOperationResult(
      final SubjectInformationType subjectInformation,
      final IAssetAdministrationShell aas,
      final IIdentifier smId,
      final String smElIdShortPath,
      final String requestId,
      final Supplier<Object> operationResultSupplier
  ) throws InhibitException;
}
