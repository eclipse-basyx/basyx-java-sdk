/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.connected;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.IElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.vab.model.VABModelMap;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * Conntected Element superclass; Extends LinkedHashMap for local caching used for c# proxy
 * 
 * @author pschorn
 *
 */
public class ConnectedElement implements IElement {

	private VABElementProxy proxy;
	protected VABModelMap<Object> cached;

	public VABElementProxy getProxy() {
		return proxy;
	}

	public ConnectedElement(VABElementProxy proxy) {
		super();
		this.proxy = proxy;
	}


	/**
	 * Returns a live variant of the map. Only use this if access to dynamic data is
	 * intended. Otherwise use {@link #getElem()}
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public VABModelMap<Object> getElemLive() {
		VABModelMap<Object> map = new VABModelMap<>((Map<String, Object>) getProxy().getValue(""));
		// update cache
		cached = map;
		return map;
	}

	/**
	 * Returns the cached variant of the underlying element. <br>
	 * Only use this method if you are accessing static data (e.g. meta data) of the
	 * element. Otherwise use {@link #getElemLive()}
	 * 
	 * @return
	 */
	public VABModelMap<Object> getElem() {
		if (cached == null) {
			return getElemLive();
		} else {
			return cached;
		}
	}

	@Override
	public String toString() {
		return getElemLive().toString();
	}

	protected void throwNotSupportedException() {
		throw new RuntimeException("Not supported on remote object");
	}

	@Override
	public String getIdShort() {
		return (String) getElem().getPath(Referable.IDSHORT);
	}
}
