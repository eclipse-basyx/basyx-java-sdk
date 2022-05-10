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

/**
 * Exception that thrown in order to inhibit some
 * making while making an authorization decision.
 *
 * @author wege
 */
public class InhibitException extends Exception {
  public InhibitException() {}

  public InhibitException(final Exception e) {
    super(e);
  }
}
