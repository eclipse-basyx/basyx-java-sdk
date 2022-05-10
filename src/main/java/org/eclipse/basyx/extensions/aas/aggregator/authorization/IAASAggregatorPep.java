/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.aggregator.authorization;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedAASAggregator}.
 *
 * @author wege
 */
public interface IAASAggregatorPep {
  IAssetAdministrationShell enforceGetAAS(
      IIdentifier aasId,
      IAssetAdministrationShell aas
  ) throws InhibitException;

  IModelProvider enforceGetAASProvider(
      IIdentifier aasId,
      IModelProvider modelProvider
  ) throws InhibitException;

  void enforceCreateAAS(
      IIdentifier aasId
  ) throws InhibitException;

  void enforceUpdateAAS(
      IIdentifier aasId
  ) throws InhibitException;

  void enforceDeleteAAS(
      IIdentifier aasId
  ) throws InhibitException;
}
