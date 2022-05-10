/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.api.authorization;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Interface for the policy enforcement points used in {@link AuthorizedAASAPI}.
 *
 * @author wege
 */
public interface IAASAPIPep {
	IAssetAdministrationShell enforceGetAAS(
      IIdentifier aasId,
			IAssetAdministrationShell aas
  ) throws InhibitException;

	void enforceAddSubmodel(
			IIdentifier aasId,
			IReference smId
  ) throws InhibitException;

	void enforceRemoveSubmodel(
			IIdentifier aasId,
			String smIdShort
  ) throws InhibitException;
}
