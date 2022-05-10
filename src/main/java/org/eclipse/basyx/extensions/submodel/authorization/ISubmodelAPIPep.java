/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.authorization;

import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedSubmodelAPI}.
 *
 * @author wege
 */
public interface ISubmodelAPIPep {
  ISubmodelElement enforceGetSubmodelElement(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort,
      ISubmodelElement smEl
  ) throws InhibitException;

  ISubmodel enforceGetSubmodel(
      IIdentifier aasId,
      IIdentifier smId,
      ISubmodel sm
  ) throws InhibitException;

  void enforceAddSubmodelElement(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort
  ) throws InhibitException;

  void enforceDeleteSubmodelElement(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort
  ) throws InhibitException;

  /*Collection<IOperation> enforceGetOperations(
      IIdentifier aasId,
      IIdentifier identification
  );*/

  void enforceUpdateSubmodelElement(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort
  ) throws InhibitException;

  Object enforceGetSubmodelElementValue(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort,
      Object value
  ) throws InhibitException;

  void enforceInvokeOperation(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort
  ) throws InhibitException;

  Object enforceGetOperationResult(
      IIdentifier aasId,
      IIdentifier smId,
      String smElIdShort,
      String requestId,
      Object operationResult
  ) throws InhibitException;
}
