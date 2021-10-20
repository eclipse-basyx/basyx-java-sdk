/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ICapability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of ICapability
 * 
 * @author espen, fischer
 *
 */
public class ConnectedCapability extends ConnectedSubmodelElement implements ICapability {

	public ConnectedCapability(VABElementProxy proxy) {
		super(proxy);
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.CAPABILITY;
	}

	@Override
	public Capability getLocalCopy() {
		return Capability.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("A Capability has no value");
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("A Capability has no value");
	}
}
