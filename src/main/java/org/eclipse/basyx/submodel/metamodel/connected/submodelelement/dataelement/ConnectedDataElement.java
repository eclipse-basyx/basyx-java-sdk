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

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.DataElement;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of DataElement
 * 
 * @author rajashek
 *
 */
public class ConnectedDataElement extends ConnectedSubmodelElement implements IDataElement {
	public ConnectedDataElement(VABElementProxy proxy) {
		super(proxy);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.DATAELEMENT;
	}
	
	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("getValue is only possible in specific Element");
	}

	@Override
	public DataElement getLocalCopy() {
		return DataElement.createAsFacade(getElem()).getLocalCopy();
	}
}
