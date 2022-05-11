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
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Simple attribute based implementation for {@link IAASAPIAuthorizer}.
 *
 * @author wege
 */
public class SimpleAbacAASAPIAuthorizer implements IAASAPIAuthorizer {
  protected AbacRuleChecker abacRuleChecker;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacAASAPIAuthorizer(final AbacRuleChecker abacRuleChecker, final RoleAuthenticator roleAuthenticator) {
    this.abacRuleChecker = abacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public IAssetAdministrationShell enforceGetAAS(IIdentifier aasId, IAssetAdministrationShell aas)
      throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAPIScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return aas;
  }

  @Override
  public void enforceAddSubmodel(IIdentifier aasId, IReference smId) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAPIScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getReferenceId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceRemoveSubmodel(IIdentifier aasId, String smIdShort) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAPIScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        smIdShort,
        null
    )) {
      throw new InhibitException();
    }
  }
}
