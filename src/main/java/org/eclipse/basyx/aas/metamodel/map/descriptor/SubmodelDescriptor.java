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
package org.eclipse.basyx.aas.metamodel.map.descriptor;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasSemantics;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;




/**
 * AAS descriptor class
 * 
 * @author kuhn
 *
 */
public class SubmodelDescriptor extends ModelDescriptor implements IHasSemantics {
	
	public static final String MODELTYPE = "SubmodelDescriptor";

	/**
	 * Create descriptor from existing hash map
	 */
	public SubmodelDescriptor(Map<String, Object> map) {
		super(map);
		validate(map);
	}
	
	protected SubmodelDescriptor() {
	  super();
	}
	
	/**
	 * Create a new aas descriptor with minimal information based on an existing
	 * submodel.
	 */
	public SubmodelDescriptor(ISubmodel sm, String httpEndpoint) {
		// Create descriptor with minimal information (id and idShort)
		this(sm.getIdShort(), sm.getIdentification(), httpEndpoint);
		
		putAll(new HasSemantics(sm.getSemanticId()));
	}
	
	/**
	 * Create a new descriptor with minimal information
	 */
	public SubmodelDescriptor(String idShort, IIdentifier id, String httpEndpoint) {
		super(idShort, id, harmonizeEndpoint(httpEndpoint));
		
		// Add model type
		putAll(new ModelType(MODELTYPE));
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	private static String harmonizeEndpoint(String endpoint) {
		if (!endpoint.endsWith("/submodel")) {
			return endpoint + "/submodel";
		} else {
			return endpoint;
		}
	}

	@Override
	public IReference getSemanticId() {
		return HasSemantics.createAsFacade(this).getSemanticId();
	}

	public void setSemanticId(Reference ref) {
		HasSemantics.createAsFacade(this).setSemanticId(ref);
	}

}

