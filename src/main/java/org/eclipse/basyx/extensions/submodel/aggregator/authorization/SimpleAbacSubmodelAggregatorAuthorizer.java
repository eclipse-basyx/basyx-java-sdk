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

import org.eclipse.basyx.extensions.shared.authorization.AbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;

/**
 * Simple attribute based implementation for {@link ISubmodelAggregatorAuthorizer}.
 *
 * @author wege
 */
public class SimpleAbacSubmodelAggregatorAuthorizer implements ISubmodelAggregatorAuthorizer {
  protected AbacRuleChecker abacRuleChecker;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacSubmodelAggregatorAuthorizer(final AbacRuleChecker abacRuleChecker, final RoleAuthenticator roleAuthenticator) {
    this.abacRuleChecker = abacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public ISubmodel enforceGetSubmodel(IIdentifier smId, ISubmodel sm) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAggregatorScopes.READ_SCOPE,
        null,
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
    return sm;
  }

  @Override
  public ISubmodelAPI enforceGetSubmodelAPI(IIdentifier smId, ISubmodelAPI smAPI) throws InhibitException {
    if(!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAggregatorScopes.READ_SCOPE,
        null,
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
    return smAPI;
  }

  @Override
  public void enforceCreateSubmodel(IIdentifier smId) throws InhibitException {
    if(!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAggregatorScopes.WRITE_SCOPE,
        null,
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUpdateSubmodel(IIdentifier smId) throws InhibitException {
    if(!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAggregatorScopes.WRITE_SCOPE,
        null,
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceDeleteSubmodel(IIdentifier smId) throws InhibitException {
    if(!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAggregatorScopes.WRITE_SCOPE,
        null,
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }
}
