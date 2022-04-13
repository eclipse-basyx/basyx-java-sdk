/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
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
