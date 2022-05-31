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
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedSubmodelAPI}.
 *
 * @author wege
 */
public interface ISubmodelAPIAuthorizer<SubjectInformationType> {
  Collection<ISubmodelElement> enforceGetSubmodelElements(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final Supplier<Collection<ISubmodelElement>> smElListSupplier
  ) throws InhibitException;

  ISubmodelElement enforceGetSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<ISubmodelElement> smElSupplier
  ) throws InhibitException;

  ISubmodel enforceGetSubmodel(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final Supplier<ISubmodel> smSupplier
  ) throws InhibitException;

  void enforceAddSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  void enforceDeleteSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  void enforceUpdateSubmodelElement(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  Object enforceGetSubmodelElementValue(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath,
      final Supplier<Object> valueSupplier
  ) throws InhibitException;

  Collection<IOperation> enforceGetOperations(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final Supplier<Collection<IOperation>> operationListSupplier
  ) throws InhibitException;

  void enforceInvokeOperation(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath
  ) throws InhibitException;

  Object enforceGetOperationResult(
      final SubjectInformationType subjectInformation,
      final IIdentifier aasId,
      final IIdentifier smId,
      final String smElIdShortPath,
      final String requestId,
      final Supplier<Object> operationResultSupplier
  ) throws InhibitException;
}
