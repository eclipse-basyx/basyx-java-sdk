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
import org.eclipse.basyx.extensions.shared.authorization.AbacRulePip;
import org.eclipse.basyx.extensions.shared.authorization.AbacRuleSet;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.RoleAuthenticator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

/**
 * Simple attribute based implementation for {@link IAASRegistryPep}.
 *
 * @author wege
 */
public class SimpleAbacAASRegistryPep implements IAASRegistryPep {
  protected AbacRulePip abacRulePip;
  protected RoleAuthenticator roleAuthenticator;

  public SimpleAbacAASRegistryPep(final AbacRuleSet abacRuleSet, final RoleAuthenticator roleAuthenticator) {
    abacRulePip = new AbacRulePip(abacRuleSet);
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public void enforceRegisterAas(IIdentifier aasId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceRegisterSubmodel(IIdentifier aasId, IIdentifier smId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUnregisterAas(IIdentifier aasId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public void enforceUnregisterSubmodel(IIdentifier aasId, IIdentifier smId) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.WRITE_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
  }

  @Override
  public AASDescriptor enforceLookupAas(IIdentifier aasId, AASDescriptor aas) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        null,
        null
    )) {
      throw new InhibitException();
    }
    return aas;
  }

  @Override
  public SubmodelDescriptor enforceLookupSubmodel(IIdentifier aasId, IIdentifier smId, SubmodelDescriptor sm) throws InhibitException {
    if (!abacRulePip.abacRuleGrantsPermission(
        roleAuthenticator.getRoles(),
        AASRegistryScopes.READ_SCOPE,
        IdUtil.getIdentifierId(aasId),
        IdUtil.getIdentifierId(smId),
        null
    )) {
      throw new InhibitException();
    }
    return sm;
  }
}
