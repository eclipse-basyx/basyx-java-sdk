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
package org.eclipse.basyx.aas.metamodel.api.parts.asset;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IIdentifiable;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;

/**
 * An Asset describes meta data of an asset that is represented by an AAS. The
 * asset may either represent an asset type or an asset instance. The asset has
 * a globally unique identifier plus – if needed – additional domain specific
 * (proprietary) identifiers.
 * 
 * @author rajashek, schnicke
 *
 */

public interface IAsset extends IHasDataSpecification, IIdentifiable {
	/**
	 * Gets the asset kind
	 * 
	 * @return
	 */
	AssetKind getAssetKind();

	/**
	 * Gets the reference to a Submodel that defines the handling of additional
	 * domain specific (proprietary) Identifiers for the asset like e.g. serial
	 * number etc.
	 * 
	 * @return
	 */
	IReference getAssetIdentificationModel();

	/**
	 * Gets bill of material of the asset represented by a submodel of the same AAS.
	 * This submodel contains a set of entities describing the material used to
	 * compose the composite I4.0 Component.
	 * 
	 * @return
	 */
	IReference getBillOfMaterial();
}
