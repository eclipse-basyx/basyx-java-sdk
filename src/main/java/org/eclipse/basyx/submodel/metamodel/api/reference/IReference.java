/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.api.reference;

import java.util.List;

/**
 * Reference to either a model element of the same or another AAs or to an
 * external entity. <br>
 * <br>
 * A reference is an ordered list of keys, each key referencing an element. The
 * complete list of keys may for example be concatenated to a path that then
 * gives unique access to an element or entity.
 * 
 * @author rajashek, schnicke
 *
 */

public interface IReference {

	/**
	 * Gets the keys describing the reference.
	 * 
	 * @return
	 */
	public List<IKey> getKeys();
}
