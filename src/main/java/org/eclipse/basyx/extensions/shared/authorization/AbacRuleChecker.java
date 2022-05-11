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
 * Interface for checking attribute based access rules against some (potentially occurred)
 * role x right x (aas id, submodel id, submodel element id) combination.
 *
 * @author wege
 */
public interface AbacRuleChecker {
  boolean abacRuleGrantsPermission(
      final List<String> roles,
      final String right,
      final String aasId,
      final String smId,
      final String smElId
  );
}
