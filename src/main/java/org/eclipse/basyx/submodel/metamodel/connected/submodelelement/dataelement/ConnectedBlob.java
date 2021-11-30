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

