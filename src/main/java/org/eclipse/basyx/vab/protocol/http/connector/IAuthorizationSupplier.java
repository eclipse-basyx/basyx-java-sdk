/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.http.connector;

import java.util.Optional;

/**
 * Interface for a supplier of values that can be placed in the HTTP Authorization request header.
 *
 * @author pneuschwander
 */
@FunctionalInterface
public interface IAuthorizationSupplier {

	/**
	 * Might provide a value that can be placed in the HTTP Authorization request header.
	 *
	 * @return Optional that is either empty or contains a non-blank String that can be placed in the HTTP Authorization request header
	 */
	Optional<String> getAuthorization();
}
