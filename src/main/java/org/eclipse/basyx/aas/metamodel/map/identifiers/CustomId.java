/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.map.identifiers;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;

/**
 * CustomId identifier
 * 
 * @author schnicke
 *
 */
public class CustomId extends Identifier {

	/**
	 * Creates a new Identifier with IdentifierType == IdentifierType.CUSTOM
	 * 
	 * @param id
	 */
	public CustomId(String id) {
		super(IdentifierType.CUSTOM, id);
	}
}
