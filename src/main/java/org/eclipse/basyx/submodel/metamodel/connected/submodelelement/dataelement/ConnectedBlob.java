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
package org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IBlob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;

/**
 * "Connected" implementation of IBlob
 * @author rajashek
 *
 */
public class ConnectedBlob extends ConnectedDataElement implements IBlob {
	
	public ConnectedBlob(VABElementProxy proxy) {
		super(proxy);		
	}

	@Override
	public String getValue() {
		Object connectedValue = getProxy().getValue(Property.VALUE);
		if (connectedValue instanceof String) {
			return (String) connectedValue;
		} else {
			return null;
		}
	}

	@Override
	public String getMimeType() {
		return (String)	getElem().get(Blob.MIMETYPE);
	}
	
	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.BLOB;
	}

	@Override
	public Blob getLocalCopy() {
		return Blob.createAsFacade(getElem()).getLocalCopy();
	}

	@Override
	public void setValue(String value) {
		if (value instanceof String) {
			// Assume a Base64 encoded String
			super.setValue(value);
		} else {
			throw new IllegalArgumentException("Given Object is not a String");
		}
	}


	@Override
	public byte[] getByteArrayValue() {
		String value = getValue();
		if (value != null) {
			return Base64.getDecoder().decode(value);
		} else {
			return null;
		}
	}

	@Override
	public void setByteArrayValue(byte[] value) {
		setValue((Object) Base64.getEncoder().encodeToString(value));
	}

	@Override
	public String getUTF8String() {
		return getLocalCopy().getUTF8String();
	}

	@Override
	public void setUTF8String(String text) {
		byte[] byteArray = text.getBytes(StandardCharsets.UTF_8);
		setByteArrayValue(byteArray);
	}
}

