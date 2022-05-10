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
 * Interface for a role provider that should be used in the context of
 * authorization by authenticating some request.
 *
 * @author wege
 */
public interface RoleAuthenticator {
  List<String> getRoles();
}
