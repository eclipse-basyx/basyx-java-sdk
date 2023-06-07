/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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


package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.entity;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.IEntity;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.EntityValue;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Connected implementation of IEntity
 * 
 * @author schnicke
 *
 */
public class ConnectedEntity extends ConnectedSubmodelElement implements IEntity {

	/**
	 * Constructs an ConnectedEntity representing the data pointed to by the
	 * elementProxy
	 * 
	 * @param elementProxy
	 */
	public ConnectedEntity(VABElementProxy elementProxy) {
		super(elementProxy);
	}

	@Override
	public Collection<ISubmodelElement> getStatements() {
		return getValue().getStatement();
	}

	@Override
	public EntityType getEntityType() {
		return Entity.createAsFacade(getElem()).getEntityType();
	}

	@Override
	public IReference getAsset() {
		return getValue().getAsset();
	}

	@Override
	public void setValue(EntityValue value) {
		super.setValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EntityValue getValue() {
		return EntityValue.createAsFacade((Map<String, Object>) super.getValue());
	}
}
