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
import org.eclipse.basyx.extensions.shared.authorization.AbacRulePip;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Simple attribute based implementation for {@link IAASAggregatorPep}.
 *
 * @author wege
 */
public class SimpleAbacAASAggregatorPep implements IAASAggregatorPep {
  protected AbacRulePip abacRulePip;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacAASAggregatorPep(final AbacRuleSet abacRuleSet, final RoleAuthenticator roleAuthenticator) {
    abacRulePip = new AbacRulePip(abacRuleSet);
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public IAssetAdministrationShell enforceGetAAS(IIdentifier aasId, IAssetAdministrationShell aas) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAggregatorScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return aas;
  }

  @Override
  public IModelProvider enforceGetAASProvider(IIdentifier aasId, IModelProvider modelProvider) throws InhibitException {
    return modelProvider;
  }

  @Override
  public void enforceCreateAAS(IIdentifier aasId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUpdateAAS(IIdentifier aasId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceDeleteAAS(IIdentifier aasId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASAggregatorScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }
}
