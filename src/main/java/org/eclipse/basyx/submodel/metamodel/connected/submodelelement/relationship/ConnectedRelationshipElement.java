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
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.relationship;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.relationship.IRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElementValue;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of RelationshipElement
 * 
 * @author rajashek
 *
 */
public class ConnectedRelationshipElement extends ConnectedSubmodelElement implements IRelationshipElement {
	public ConnectedRelationshipElement(VABElementProxy proxy) {
		super(proxy);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.RELATIONSHIPELEMENT;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RelationshipElementValue getValue() {
		Object obj = getProxy().getValue(Property.VALUE);

		return RelationshipElementValue.createAsFacade((Map<String, Object>) obj);
	}

	@Override
	public RelationshipElement getLocalCopy() {
		return RelationshipElement.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public void setValue(RelationshipElementValue value) {
		setValue((Object) value);
	}
}
