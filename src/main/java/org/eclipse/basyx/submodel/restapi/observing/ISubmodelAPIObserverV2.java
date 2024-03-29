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
package org.eclipse.basyx.submodel.restapi.observing;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.observer.IObserver;

/**
 * Interface for an observer of {@link ObservableSubmodelAPIV2}
 * 
 * @author conradi, siebert
 *
 */
public interface ISubmodelAPIObserverV2 extends IObserver {

	/**
	 * Is called when a SubmodelElement is added
	 * 
	 * @param idShortPath
	 *            the idShortPath of the added element
	 * @param newValue
	 *            the value of the new element
	 */
	public void elementAdded(String idShortPath, Object newValue, String aasId, String submodelId, String repoId);

	/**
	 * Is called when a SubmodelElement is deleted
	 * 
	 * @param idShortPath
	 *            the idShortPath of the deleted element
	 */
	public void elementDeleted(String idShortPath, ISubmodelElement submodelElement, String aasId, String submodelId, String repoId);

	/**
	 * Is called when a SubmodelElement is updated
	 * 
	 * @param idShortPath
	 *            the idShortPath of the updated element
	 */
	public void elementUpdated(String idShortPath, ISubmodelElement submodelElement, String aasId, String submodelId, String repoId);

	/**
	 * Is called when a SubmodelElement value is updated
	 * 
	 * @param value
	 */
	public void elementValue(String idShortPath, Object value, String aasId, String submodelId, String repoId);
}
