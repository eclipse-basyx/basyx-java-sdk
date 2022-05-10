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

import java.util.stream.Collectors;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * Utility methods for handling ids.
 *
 * @author wege
 */
public class IdUtil {
  private IdUtil() {}

  public static String getIdentifierId(final IIdentifier identifier) {
    return identifier != null ? identifier.getId() : null;
  }

  public static String getReferenceId(final IReference reference) {
    return reference != null ? reference.getKeys().stream().map(IKey::getValue).collect(Collectors.joining(";")) : null;
  }
}
