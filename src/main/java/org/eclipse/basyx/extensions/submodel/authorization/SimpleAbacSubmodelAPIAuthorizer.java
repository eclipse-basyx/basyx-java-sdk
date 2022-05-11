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

import org.eclipse.basyx.extensions.shared.authorization.AbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;

/**
 * Simple attribute based implementation for {@link ISubmodelAPIAuthorizer}.
 *
 * @author wege
 */
public class SimpleAbacSubmodelAPIAuthorizer implements ISubmodelAPIAuthorizer {
  protected AbacRuleChecker abacRuleChecker;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacSubmodelAPIAuthorizer(AbacRuleSet abacRuleSet, RoleAuthenticator roleAuthenticator) {
    this.abacRuleChecker = new AbacRuleChecker(abacRuleSet);
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public ISubmodelElement enforceGetSubmodelElement(IIdentifier aasId, IIdentifier smId, String smElIdShort, ISubmodelElement smEl) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
    return smEl;
  }

  @Override
  public ISubmodel enforceGetSubmodel(IIdentifier aasId, IIdentifier smId, ISubmodel sm) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
    return sm;
  }

  @Override
  public void enforceAddSubmodelElement(IIdentifier aasId, IIdentifier smId, String smElIdShort) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceDeleteSubmodelElement(IIdentifier aasId, IIdentifier smId, String smElIdShort) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUpdateSubmodelElement(IIdentifier aasId, IIdentifier smId, String smElIdShort) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public Object enforceGetSubmodelElementValue(IIdentifier aasId, IIdentifier smId, String smElIdShort, Object value) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
    return value;
  }

  @Override
  public void enforceInvokeOperation(IIdentifier aasId, IIdentifier smId, String smElIdShort) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.EXECUTE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public Object enforceGetOperationResult(IIdentifier aasId, IIdentifier smId, String smElIdShort, String requestId, Object operationResult) throws InhibitException {
    if (!abacRuleChecker.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        SubmodelAPIScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        smElIdShort
    )) {
      throw new InhibitException();
    }
    return operationResult;
  }
}
