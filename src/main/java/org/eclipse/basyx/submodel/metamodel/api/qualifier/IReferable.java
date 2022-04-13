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
package org.eclipse.basyx.submodel.metamodel.api.qualifier;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;

/**
 * An element that is referable by its idShort. This id is not globally unique.
 * This id is unique within the name space of the element.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IReferable {
	/**
	 * Gets the identifying string of the element within its name space.
	 * 
	 * @return
	 */
	public String getIdShort();

	/**
	 * Gets the category of the referable. The category is a value that gives
	 * further meta information w.r.t. to the class of the element. It affects the
	 * expected existence of attributes and the applicability of constraints.
	 * 
	 * @return
	 */
	public String getCategory();

	/**
	 * Gets the description or comments on the element.<br>
	 * <br>
	 * The description can be provided in several languages.
	 * 
	 * @return
	 */
	public LangStrings getDescription();

	/**
	 * Gets the parent of the referable.
	 * 
	 * @return
	 */
	public IReference getParent();

	public IReference getReference();
}
