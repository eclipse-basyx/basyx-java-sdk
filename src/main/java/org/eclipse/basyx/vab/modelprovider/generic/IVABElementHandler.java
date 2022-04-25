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
package org.eclipse.basyx.vab.modelprovider.generic;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * Interface for a handler, that can handle an object in the VAB. Can process
 * the object and supports all primitives defined by {@link IModelProvider}.
 * 
 * @author espen
 *
 */
public interface IVABElementHandler {
	/**
	 * Handles internal objects after they have been processed
	 */
	public default Object postprocessObject(Object element) {
		return element;
	}

	public Object getElementProperty(Object element, String propertyName);

	public void setModelPropertyValue(Object element, String propertyName, Object newValue);

	public void createValue(Object element, Object newValue);

	public void deleteValue(Object element, String propertyName);

	public void deleteValue(Object element, Object property);
}
