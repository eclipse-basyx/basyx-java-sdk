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

import org.eclipse.basyx.extensions.shared.authorization.AbacRuleChecker;
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

  public SimpleAbacSubmodelAPIAuthorizer(AbacRuleChecker abacRuleChecker, RoleAuthenticator roleAuthenticator) {
    this.abacRuleChecker = abacRuleChecker;
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
