/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.registration.authorization;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedAASRegistry}.
 *
 * @author wege
 */
public interface IAASRegistryPep {
  void enforceRegisterAas(
      IIdentifier aasId
  ) throws InhibitException;

  void enforceRegisterSubmodel(
      IIdentifier aasId,
      IIdentifier smId
  ) throws InhibitException;

  void enforceUnregisterAas(
      IIdentifier aasId
  ) throws InhibitException;

  void enforceUnregisterSubmodel(
      IIdentifier aasId,
      IIdentifier smId
  ) throws InhibitException;

  AASDescriptor enforceLookupAas(
      IIdentifier aasId,
      AASDescriptor aas
  ) throws InhibitException;

  SubmodelDescriptor enforceLookupSubmodel(
      IIdentifier aasId,
      IIdentifier smId,
      SubmodelDescriptor sm
  ) throws InhibitException;
}