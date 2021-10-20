/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IAnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElementValue;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of AnnotatedRelationshipElement
 * 
 * @author schnicke, conradi
 *
 */
public class ConnectedAnnotatedRelationshipElement extends ConnectedRelationshipElement implements IAnnotatedRelationshipElement {

	public ConnectedAnnotatedRelationshipElement(VABElementProxy proxy) {
		super(proxy);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AnnotatedRelationshipElementValue getValue() {
		Object obj = getProxy().getValue(Property.VALUE);

		return AnnotatedRelationshipElementValue.createAsFacade((Map<String, Object>) obj);
	}

	@Override
	public void setValue(AnnotatedRelationshipElementValue value) {
		setValue((Object) value);
	}
}
