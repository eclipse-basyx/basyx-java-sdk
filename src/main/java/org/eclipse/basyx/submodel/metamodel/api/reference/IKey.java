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

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;

/**
 * A key is a reference to an element by its id.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IKey {

	/**
	 * Denotes which kind of entity is referenced.
	 * 
	 * @return
	 */
	public KeyElements getType();

	/**
	 * Gets if the key references a model element of the same AAS (=true) or not
	 * (=false). In case of local = false the key may reference a model element of
	 * another AAS or an entity outside any AAS that has a global unique id.
	 * 
	 * @return
	 */
	public boolean isLocal();

	/**
	 * Gets the key value, for example an IRDI if the idType=IRDI.
	 * 
	 * @return
	 */
	public String getValue();

	/**
	 * Gets the type of the key value. <br>
	 * In case of idType = idShort local shall be true. In case type=GlobalReference
	 * idType shall not be IdShort.
	 * 
	 * @return
	 */
	public KeyType getIdType();
}
