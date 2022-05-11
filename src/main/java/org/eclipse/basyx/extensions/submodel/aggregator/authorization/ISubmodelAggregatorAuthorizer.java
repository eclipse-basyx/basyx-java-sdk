/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.submodel.aggregator.authorization;

import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedSubmodelAggregator}.
 *
 * @author wege
 */
public interface ISubmodelAggregatorAuthorizer {
  ISubmodel enforceGetSubmodel(
      IIdentifier smId,
      ISubmodel smSupplier
  ) throws InhibitException;

  ISubmodelAPI enforceGetSubmodelAPI(
      IIdentifier smId,
      ISubmodelAPI smAPI
  ) throws InhibitException;

  void enforceCreateSubmodel(
      IIdentifier smId
  ) throws InhibitException;

  void enforceUpdateSubmodel(
      IIdentifier smId
  ) throws InhibitException;

  void enforceDeleteSubmodel(
      IIdentifier smId
  ) throws InhibitException;
}
