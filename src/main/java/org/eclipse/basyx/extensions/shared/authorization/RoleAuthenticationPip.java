/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.shared.authorization;

import java.util.List;

/**
 * Policy information point class that is backed by some role authenticator to provide roles.
 *
 * @author wege
 */
public class RoleAuthenticationPip {
  protected RoleAuthenticator roleAuthenticator;

  public RoleAuthenticationPip(RoleAuthenticator roleAuthenticator) {
    this.roleAuthenticator = roleAuthenticator;
  }

  public List<String> getRoles() {
    return roleAuthenticator.getRoles();
  }
}
