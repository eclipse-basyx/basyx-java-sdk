/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.registry.descriptor;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.registry.descriptor.parts.Endpoint;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasSemantics;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

/**
 * AAS descriptor class
 * 
 * @author kuhn
 *
 */
public class SubmodelDescriptor extends ModelDescriptor implements IHasSemantics {

	public static final String MODELTYPE = "SubmodelDescriptor";

	private SubmodelDescriptor() {
	}

	/**
	 * Create a new aas descriptor with minimal information based on an existing
	 * submodel.
	 */
	public SubmodelDescriptor(ISubmodel sm, Collection<Endpoint> endpoints) {
		this(sm.getIdShort(), sm.getIdentification(), endpoints);

		putAll(new HasSemantics(sm.getSemanticId()));
	}

	/**
	 * Create a new descriptor with minimal information
	 */
	public SubmodelDescriptor(String idShort, IIdentifier id, Collection<Endpoint> endpoints) {
		super(idShort, id, endpoints);

		putAll(new ModelType(MODELTYPE));
	}

	/**
	 * Creates a SumodelDescriptor from a given map
	 * 
	 * @param map
	 * @return
	 */
	public static SubmodelDescriptor createAsFacade(Map<String, Object> map) {
		if (!isValid(map)) {
			throw new MalformedRequestException("The given map '" + map + "' is not valid.");
		}
		SubmodelDescriptor facade = new SubmodelDescriptor();
		facade.setMap(map);
		return facade;
	}

	@Override
	protected String getModelType() {
		return MODELTYPE;
	}

	@Override
	public IReference getSemanticId() {
		return HasSemantics.createAsFacade(this).getSemanticId();
	}

	public void setSemanticId(Reference ref) {
		HasSemantics.createAsFacade(this).setSemanticId(ref);
	}


}
