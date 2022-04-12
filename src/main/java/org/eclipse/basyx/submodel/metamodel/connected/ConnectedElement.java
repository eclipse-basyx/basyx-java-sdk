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
