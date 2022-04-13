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
package org.eclipse.basyx.aas.metamodel.api;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.api.parts.IView;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.api.security.ISecurity;
import org.eclipse.basyx.submodel.metamodel.api.IElement;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;

/**
 * Asset Administration Shell (AAS) interface
 * 
 * @author kuhn, schnicke
 *
 */

public interface IAssetAdministrationShell extends IElement, IIdentifiable, IHasDataSpecification {
	/**
	 * Return all registered submodels of this AAS
	 * 
	 * @return {@literal IdShort -> ISubmodel}
	 */
	public Map<String, ISubmodel> getSubmodels();

	/**
	 * Return the references to all registered submodels
	 * 
	 * @return
	 */
	public Collection<IReference> getSubmodelReferences();

	/**
	 * Add a submodel to the AAS
	 * 
	 * @param subModel
	 *            The added sub model
	 */
	public void addSubmodel(Submodel subModel);

	/**
	 * Removes a submodel from the AAS
	 * 
	 * @param id
	 */
	public void removeSubmodel(IIdentifier id);

	/**
	 * Gets a submodel from the AAS
	 * 
	 * @param id
	 */
	public ISubmodel getSubmodel(IIdentifier id);

	/**
	 * Gets the definition of the security relevant aspects of the AAS.
	 * 
	 * @return
	 */
	public ISecurity getSecurity();

	/**
	 * Gets the reference to the AAS the AAS was derived from.
	 * 
	 * @return
	 */
	public IReference getDerivedFrom();

	/**
	 * Gets the asset the AAS is representing.
	 * 
	 * @return
	 */
	public IAsset getAsset();

	/**
	 * Gets the reference to the asset the AAS is representing.
	 * 
	 * @return
	 */
	public IReference getAssetReference();

	/**
	 * Gets the views associated with the AAS. <br>
	 * If needed stakeholder specific views can be defined on the elements of the
	 * AAS.
	 * 
	 * @return
	 */
	public Collection<IView> getViews();

	/**
	 * Gets the concept dictionaries associated with the AAS. <br>
	 * An AAS may have one or more concept dictionaries assigned to it. The concept
	 * dictionaries typically contain only descriptions for elements that are also
	 * used within the AAS (via HasSemantics).
	 * 
	 * @return
	 */
	public Collection<IConceptDictionary> getConceptDictionary();
}
