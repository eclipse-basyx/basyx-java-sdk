/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueTypeHelper;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Connects to a PropertySingleValued as specified by DAAS containing a simple
 * value
 * 
 * @author schnicke
 *
 */
public class ConnectedProperty extends ConnectedDataElement implements IProperty {

	public ConnectedProperty(VABElementProxy proxy) {
		super(proxy);
	}

	@Override
	public ValueType getValueType() {
		return ValueTypeHelper.readTypeDef(getElem().getPath(Property.VALUETYPE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getValueId() {
		return Reference.createAsFacade((Map<String, Object>) getElem().getPath(Property.VALUEID));
	}

	@SuppressWarnings("unchecked")
	protected <T> T retrieveObject() {
		return (T) getProxy().getValue(Property.VALUE);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.PROPERTY;
	}

	@Override
	public Object getValue() {
		Object value =  retrieveObject();
		if(value instanceof String) {
			return ValueTypeHelper.getJavaObject(value, getValueType());
		}else {
			return value;
		}
	}

	@Override
	public Property getLocalCopy() {
		return Property.createAsFacade(getElem()).getLocalCopy();
	}
}
